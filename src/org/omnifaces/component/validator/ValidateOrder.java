/*
 * Copyright 2012 OmniFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.omnifaces.component.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import org.omnifaces.util.Callback;

/**
 * <strong>ValidateOrder</strong> validates if the values of the given {@link UIInput} components as specified in
 * the <code>components</code> attribute are in the order as specified by the <code>type</code> attribute which accepts
 * the following values:
 * <ul>
 * <li><code>lt</code> (default): from least to greatest, without duplicates.</li>
 * <li><code>lte</code>: from least to greatest, allowing duplicates (equal values next to each other).</li>
 * <li><code>gt</code>: from greatest to least, without duplicates.</li>
 * <li><code>gte</code>: from greatest to least, allowing duplicates (equal values next to each other).</li>
 * </ul>
 * <p>
 * The default message is
 * <blockquote>{0}: Please fill out the values of all those fields in order</blockquote>
 * <p>
 * For general usage instructions, refer {@link ValidateMultipleFields} documentation.
 * <p>
 * This validator has the additional requirement that the to-be-validated values must implement {@link Comparable}.
 * This validator throws an {@link IllegalArgumentException} when one or more of the values do not implement it.
 *
 * @author Bauke Scholtz
 */
@FacesComponent(ValidateOrder.COMPONENT_TYPE)
@SuppressWarnings({ "unchecked", "rawtypes" }) // We don't care about the actual Comparable type.
public class ValidateOrder extends ValidateMultipleFields {

	// Public constants -----------------------------------------------------------------------------------------------

	/** The standard component type. */
	public static final String COMPONENT_TYPE = "org.omnifaces.component.validator.ValidateOrder";

	// Private constants ----------------------------------------------------------------------------------------------

	private enum Type {
		LT(new Callback.ReturningWithArgument<Boolean, List<Comparable>>() {
			@Override
			public Boolean invoke(List<Comparable> values) {
				return new ArrayList<Comparable>(new TreeSet<Comparable>(values)).equals(values);
			}
		}),

		LTE(new Callback.ReturningWithArgument<Boolean, List<Comparable>>() {
			@Override
			public Boolean invoke(List<Comparable> values) {
				List<Comparable> sortedValues = new ArrayList<Comparable>(values);
				Collections.sort(sortedValues);
				return sortedValues.equals(values);
			}
		}),

		GT(new Callback.ReturningWithArgument<Boolean, List<Comparable>>() {
			@Override
			public Boolean invoke(List<Comparable> values) {
				List<Comparable> sortedValues = new ArrayList<Comparable>(new TreeSet<Comparable>(values));
				Collections.reverse(sortedValues);
				return sortedValues.equals(values);
			}
		}),

		GTE(new Callback.ReturningWithArgument<Boolean, List<Comparable>>() {
			@Override
			public Boolean invoke(List<Comparable> values) {
				List<Comparable> sortedValues = new ArrayList<Comparable>(values);
				Collections.sort(sortedValues, Collections.reverseOrder());
				return sortedValues.equals(values);
			}
		});

		private Callback.ReturningWithArgument<Boolean, List<Comparable>> callback;

		private Type(Callback.ReturningWithArgument<Boolean, List<Comparable>> callback) {
			this.callback = callback;
		}

		public boolean validateOrder(List<Comparable> values) {
			return callback.invoke(values);
		}
	}

	private static final String DEFAULT_TYPE = Type.LT.name();
	private static final String DEFAULT_MESSAGE = "{0}: Please fill out the values of all those fields in order";
	private static final String ERROR_INVALID_TYPE = "Invalid type '%s'. Only 'lt', 'lte', 'gt' and 'gte' are allowed.";
	private static final String ERROR_VALUES_NOT_COMPARABLE = "All values must implement java.lang.Comparable.";

	private enum PropertyKeys {
		// Cannot be uppercased. They have to exactly match the attribute names.
		type;
	}

	// Constructors ---------------------------------------------------------------------------------------------------

	/**
	 * The default constructor sets the default message.
	 */
	public ValidateOrder() {
		super(DEFAULT_MESSAGE);
	}

	// Actions --------------------------------------------------------------------------------------------------------

	/**
	 * Validate if all values are in specified order.
	 */
	@Override
	protected boolean validateValues(FacesContext context, List<UIInput> components, List<Object> values) {
		try {
			Object tmp = values; // https://bugs.eclipse.org/bugs/show_bug.cgi?id=158870
			return Type.valueOf(getType().toUpperCase()).validateOrder((List<Comparable>) tmp);
		}
		catch (ClassCastException e) {
			throw new IllegalArgumentException(ERROR_VALUES_NOT_COMPARABLE, e);
		}
	}

	// Getters/setters ------------------------------------------------------------------------------------------------

	/**
	 * Returns the ordering type to be used.
	 * @return The ordering type to be used.
	 */
	public String getType() {
		return (String) getStateHelper().eval(PropertyKeys.type, DEFAULT_TYPE);
	}

	/**
	 * Sets the ordering type to be used.
	 * @param type The ordering type to be used.
	 */
	public void setType(String type) {
		try {
			Type.valueOf(type.toUpperCase());
		}
		catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(String.format(ERROR_INVALID_TYPE, type), e);
		}

		getStateHelper().put(PropertyKeys.type, type);
	}

}
/*
 * Copyright 2013 OmniFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.omnifaces.cdi.converter;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessManagedBean;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Collect all classes having {@link FacesConverter} annotation by their ID and/or for-class.
 *
 * @author Radu Creanga <rdcrng@gmail.com>
 * @author Bauke Scholtz
 * @see ConverterProvider
 * @since 1.6
 */
public class ConverterExtension implements Extension {

	// Constants ------------------------------------------------------------------------------------------------------

	private static final String ERROR_DUPLICATE_ID =
		"Registering converter '%s' failed, duplicates converter ID '%s' of other converter '%s'.";
	private static final String ERROR_DUPLICATE_FORCLASS =
		"Registering converter '%s' failed, duplicates for-class '%s' of other converter '%s'.";

	// Properties -----------------------------------------------------------------------------------------------------

	private Map<String, Bean<Converter>> convertersByID = new HashMap<String, Bean<Converter>>();
	private Map<Class<?>, Bean<Converter>> convertersByForClass = new HashMap<Class<?>, Bean<Converter>>();

	// Actions --------------------------------------------------------------------------------------------------------

	/**
	 * Check if the processed {@link Converter} instance has the {@link FacesConverter} annotation and if so, then
	 * collect it by its ID and/or for-class.
	 * @param converter The processed {@link Converter} instance.
	 */
	protected void processConverters(@Observes ProcessManagedBean<Converter> converter) {
		FacesConverter annotation = converter.getAnnotatedBeanClass().getAnnotation(FacesConverter.class);

		if (annotation == null) {
			return;
		}

		Bean<Converter> bean = converter.getBean();
		String converterId = annotation.value();

		if (!"".equals(converterId)) {
			Bean<Converter> previousBean = convertersByID.put(converterId, bean);

			if (previousBean != null) {
				converter.addDefinitionError(new IllegalArgumentException(String.format(
					ERROR_DUPLICATE_ID, bean.getBeanClass(), converterId, previousBean.getBeanClass())));
			}
		}

		Class<?> converterForClass = annotation.forClass();

		if (converterForClass != Object.class) {
			Bean<Converter> previousBean = convertersByForClass.put(converterForClass, bean);

			if (previousBean != null) {
				converter.addDefinitionError(new IllegalArgumentException(String.format(
					ERROR_DUPLICATE_FORCLASS, bean.getBeanClass(), converterForClass, previousBean.getBeanClass())));
			}
		}
	}

	// Getters --------------------------------------------------------------------------------------------------------

	/**
	 * Returns a mapping of all registered {@link FacesConverter}s by their converter ID.
	 * @return A mapping of all registered {@link FacesConverter}s by their converter ID.
	 */
	public Map<String, Bean<Converter>> getConvertersByID() {
		return convertersByID;
	}

	/**
	 * Returns a mapping of all registered {@link FacesConverter}s by their for-class.
	 * @return A mapping of all registered {@link FacesConverter}s by their for-class.
	 */
	public Map<Class<?>, Bean<Converter>> getConvertersByForClass() {
		return convertersByForClass;
	}

}
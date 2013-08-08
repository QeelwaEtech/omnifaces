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
package org.omnifaces.cdi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.UUID;

import javax.annotation.PreDestroy;
import javax.enterprise.context.NormalScope;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.omnifaces.cdi.viewscope.ViewScopeManager;

/**
 * The CDI view scope annotation, intented for use in JSF 2.0/2.1. Just use it the usual way as all other CDI scopes.
 * Watch out with IDE autocomplete on import that you don't accidentally import JSF's own one.
 * <pre>
 * import javax.inject.Named;
 * import org.omnifaces.cdi.ViewScoped;
 *
 * {@literal @}Named
 * {@literal @}ViewScoped
 * public class OmniCDIViewScopedBean implements Serializable {}
 * </pre>
 * In JSF 2.2, you're supposed to use JSF's own new CDI compatible <code>javax.faces.view.ViewScoped</code> instead;
 * not because this CDI view scope annotation is so bad, in contrary, but just because using the standard solutions
 * should be preferred over alternative solutions.
 * <p>
 * Under the covers, CDI managed beans with this scope are via {@link ViewScopeManager} stored in the session scope by
 * an {@link UUID} based key which is referenced in JSF's own view map as available by {@link UIViewRoot#getViewMap()}.
 * They are not stored in the JSF view map itself as that would be rather expensive in case of client side state saving.
 * <p>
 * In effects, this CDI view scope annotation has exactly the same lifecycle as JSF's own view scope. One important
 * thing to know and understand is that any {@link PreDestroy} annotated method on a CDI view scoped bean isn't
 * <em>immediately</em> invoked in all cases when the view scope ends. This is not specific to CDI, but to JSF itself.
 * For detail, see the following JSF issues related to the matter:
 * <ul>
 * <li><a href="https://java.net/jira/browse/JAVASERVERFACES-1351">Mojarra issue 1351</a>
 * <li><a href="https://java.net/jira/browse/JAVASERVERFACES-1839">Mojarra issue 1839</a>
 * <li><a href="https://java.net/jira/browse/JAVASERVERFACES_SPEC_PUBLIC-905">JSF spec issue 905</a>
 * </ul>
 * <p>
 * Summarized, it's only invoked when the view is either explicitly changed by a non-null/void navigation on a postback,
 * or when the view is explicitly rebuilt by {@link FacesContext#setViewRoot(UIViewRoot)}. This CDI view scope
 * annotation however guarantees that the {@link PreDestroy} annotated method is also invoked on session expire, while
 * JSF 2.0/2.1 doesn't do that (JSF 2.2 does).
 *
 * @author Radu Creanga <rdcrng@gmail.com>
 * @author Bauke Scholtz
 * @since 1.6
 */
@Inherited
@Documented
@NormalScope(passivating = true)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface ViewScoped {
	//
}
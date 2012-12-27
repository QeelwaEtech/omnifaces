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

package org.omnifaces.facesviews;

import static java.util.Collections.unmodifiableMap;
import static javax.faces.view.facelets.ResourceResolver.FACELETS_RESOURCE_RESOLVER_PARAM_NAME;
import static org.omnifaces.facesviews.FacesViewsResolver.FACES_VIEWS_RESOURCES_PARAM_NAME;
import static org.omnifaces.facesviews.FacesViewsUtils.getFacesServletRegistration;
import static org.omnifaces.facesviews.FacesViewsUtils.scanViewsFromRootPaths;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;

/**
 * Convenience class for Servlet 3.0 users, which will auto-register all artifacts
 * required for auto-mapping and extensionless view to work.
 * <p>
 * For a guide on FacesViews, please see the <a href="package-summary.html">package summary</a>.
 *
 * @author Arjan Tijms
 *
 */
@WebListener
public class FacesViewsInitializerListener implements ServletContextListener {

    // Web context parameter to switch auto-scanning completely off for Servlet 3.0 containers.
    public static final String FACES_VIEWS_ENABLED_PARAM_NAME = "org.omnifaces.FACES_VIEWS_ENABLED";

    @Override
    public void contextInitialized(ServletContextEvent context) {

        ServletContext servletContext = context.getServletContext();

        if (!"false".equals(servletContext.getInitParameter(FACES_VIEWS_ENABLED_PARAM_NAME))) {

            // Scan our dedicated directory for Faces resources that need to be mapped
            Map<String, String> collectedViews = new HashMap<String, String>();
            Set<String> collectedExtentions = new HashSet<String>();
            scanViewsFromRootPaths(servletContext, collectedViews, collectedExtentions);

            if (!collectedViews.isEmpty()) {

                // Store the resources that were found in application scope, where others can find it.
                servletContext.setAttribute(FACES_VIEWS_RESOURCES_PARAM_NAME, unmodifiableMap(collectedViews));

                // Register 3 artifacts with the Servlet container and JSF that help implement this feature:

                // 1. A Filter that forwards extensionless requests to an extension mapped request, e.g. /index to /index.xhtml
                //    (The FacesServlet doesn't work well with the exact mapping that we use for extensionless URLs).
                FilterRegistration facesViewsRegistration = servletContext.addFilter(FacesViewsForwardingFilter.class.getName(), FacesViewsForwardingFilter.class);

                // 2. A Facelets resource resolver that resolves requests like /index.xhtml to /WEB-INF/faces-views/index.xhtml
                servletContext.setInitParameter(FACELETS_RESOURCE_RESOLVER_PARAM_NAME, FacesViewsResolver.class.getName());

                // 3. A ViewHandler that transforms the forwarded extension based URL back to an extensionless one, e.g. /index.xhtml to /index
                // See FacesViewsForwardingFilter#init

                // Map the forwarding filter to all the resources we found.
                for (String resource : collectedViews.keySet()) {
                    facesViewsRegistration.addMappingForUrlPatterns(null, false, resource);
                }

                // Map the Facelets Servlet to the extension we found
                ServletRegistration facesServletRegistration = getFacesServletRegistration(servletContext);
                if (facesServletRegistration != null) {
                    Collection<String> mappings = facesServletRegistration.getMappings();
                    for (String extension : collectedExtentions) {
                        if (!mappings.contains(extension)) {
                            facesServletRegistration.addMapping(extension);
                        }
                    }
                }

            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    	// NOOP.
    }

}
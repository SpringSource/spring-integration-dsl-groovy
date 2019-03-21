/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.springframework.integration.dsl.groovy.builder.dom


import org.apache.commons.logging.LogFactory
import org.apache.commons.logging.Log
import org.springframework.context.ApplicationContext
import org.springframework.integration.dsl.groovy.IntegrationComponent
import groovy.lang.Closure
import java.util.Map

/**
 * The base class for all DOM Builders
 * 
 * @author David Turanski
 *
 */
abstract class IntegrationComponentDomBuilder {
	protected Log logger = LogFactory.getLog(this.class)
	protected IntegrationDomSupport integrationDomSupport

	/** 
	 * @param builder StreamingMarkupBuilder
	 * @param applicationContext the Spring ApplicationContext
	 * @param component the IntegrationComponent
	 * @param an optional closure containing additional XML markup used to generate child elements if necessary
	 */
	final void build(builder, ApplicationContext applicationContext, IntegrationComponent component, Closure closure){
 		
		if (!component.id){
			component.id = component.name
		}

		doBuild(builder,applicationContext,component,closure)
	}

	/**
	 * 
	 * @param builder StreamingMarkupBuilder
	 * @param applicationContext the Spring ApplicationContext
	 * @param component the IntegrationComponent
	 */
	final void build(builder, ApplicationContext applicationContext, component) {
		build(builder, applicationContext, component, null)
	}
	
    /**
     * Convenience method to get the assigned SI core namespace prefix
     * @return
     */
	protected String getSiPrefix() {
		integrationDomSupport.namespaceSupport.integrationNamespacePrefix
	}

	/** 
	 * @param builder StreamingMarkupBuilder
	 * @param applicationContext the Spring ApplicationContext
	 * @param component the IntegrationComponent
	 * @param an optional closure containing additional XML markup used to generate child elements if necessary
	 */
	protected abstract void doBuild(Object builder, ApplicationContext applicationContext, IntegrationComponent component, Closure closure);
}

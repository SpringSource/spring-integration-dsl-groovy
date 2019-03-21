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
package org.springframework.integration.dsl.groovy
import static org.junit.Assert.*
import org.junit.Test

/**
 * @author David Turanski
 *
 */
class IntegrationComponentTests {
	@Test
	void testValidateAttributes() {
		def test = new TestComponent()
		def validationContext = test.validateAttributes(['bar':'valbar','baz':'valbaz','bag':'valbag','foo':'valfoo'])
		assert !validationContext.hasErrors ,'should be valid'

		validationContext = test.validateAttributes(['bar':'valbar'])
		assert validationContext.hasErrors, 'should not be valid'
		assert validationContext.errorMessage == "'test' is missing the following required attributes:baz, bag"

		validationContext = test.validateAttributes(['bar':'valbar','barx':'valbarx'])
		assert validationContext.errorMessage == "'test' is missing the following required attributes:baz, bag"
	}

	@Test
	void testAttributesRequiresOneOf() {
		def test = new TestComponent2()
		def validationContext = test.validateAttributes(['bar':'valbar'])
		assert !validationContext.hasErrors ,'should be valid'

		validationContext = test.validateAttributes(['bar':'valbar','baz':'valbaz','bazx':'valbazx'])
		assert !validationContext.hasErrors ,'should be valid'
	}

	@Test
	void testMutuallyExclusiveAttributes(){
		def test = new TestComponent2()
		def validationContext = test.validateAttributes(['bar':'barval','foo1':'valfoo1','foo4':'valfoo4'])
		assert !validationContext.hasErrors ,'should be valid'

		validationContext = test.validateAttributes(['bar':'barval','foo1':'valfoo1','foo2':'valfoo2'])
		assert validationContext.errorMessage == "'test2' contains mutually exclusive attributes [foo1, foo2]"
	}

	@Test
	void testNullOrEmptyAttributes() {
		def test = new TestComponent2()
		def validationContext = test.validateAttributes(null)
		assert validationContext.errorMessage == "'test2' must include at least one of [bar, baz, bag]"

		validationContext = test.validateAttributes([:])
		assert validationContext.errorMessage == "'test2' must include at least one of [bar, baz, bag]"
	}

	@Test
	void testPropertyToAttributeWithInvalidProperty() {
		def attributeHelper = new AttributeHelper()
		def shouldBeInvalid = {property->
			try {
				attributeHelper.propertyNameToAttributeName(property)
				fail("should throw exception on property $property")
			} catch (AssertionError e) {
				if (e.message.startsWith('should throw exception')){
					throw e
				}
			}
		}

		shouldBeInvalid('FooBar')
		shouldBeInvalid ('foo1Bar')
		shouldBeInvalid ('foo-bar-')
	}

	@Test
	void testPropertyToAttribute() {
		String property
		def attributeHelper = new AttributeHelper()
		assert 'foo-bar' == attributeHelper.propertyNameToAttributeName('fooBar')
		assert 'foo-bar-car' == attributeHelper.propertyNameToAttributeName('foo-bar-car')
		assert 'foo-car' == attributeHelper.propertyNameToAttributeName('foo-car')
		assert 'foo-bar-car' == attributeHelper.propertyNameToAttributeName('fooBarCar')
		assert 'foo-bbbar-car' == attributeHelper.propertyNameToAttributeName('fooBBBarCar')
	}
}

class TestComponent extends IntegrationComponent {
	static requiredAttributes = ['bar', 'baz', 'bag']
	TestComponent() {
		builderName = 'test'
	}
}

class TestComponent2 extends IntegrationComponent {
	static attributesRequiresAnyOf = ['bar', 'baz', 'bag']
	static mutuallyExclusiveAttributes = [
		['foo1', 'foo2', 'foo3'],
		['foo4', 'foo5']
	]
	TestComponent2() {
		builderName = 'test2'
	}
}

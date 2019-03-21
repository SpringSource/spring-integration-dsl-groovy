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
package org.springframework.integration.dsl.groovy.builder

import static org.junit.Assert.*
import org.junit.Test

import org.springframework.integration.dsl.groovy.RouterComposition
import org.springframework.integration.dsl.groovy.RouterCondition
import org.springframework.integration.dsl.groovy.ServiceActivator


import org.springframework.integration.support.MessageBuilder
/**
 * @author David Turanski
 *
 */

class RouterUsageTests {
	IntegrationBuilder builder = new IntegrationBuilder()

	@Test
	void testSimpleRouter() {
		def integrationContext = builder.doWithSpringIntegration {
			//Must return String, String[] etc...
			route(inputChannel:'inputChannel',{ it == "Hello" ? 'upper.inputChannel' : 'lower.inputChannel' } )
			handle('upper', {payload -> payload.toUpperCase()})
			handle('lower', {payload -> payload.toLowerCase()})
		}

		assert integrationContext.sendAndReceive('inputChannel','Hello') == 'HELLO'
		assert integrationContext.sendAndReceive('inputChannel','GoodBye') == 'goodbye'
	}


	@Test
	void testRecipientListRouter() {
		def count = 0
		def integrationContext = builder.doWithSpringIntegration {
			route('myRouter', { [
					'upper.inputChannel' ,
					'lower.inputChannel']
			} )
			handle('upper', {count ++; null})
			handle('lower', {count ++; null})
		}

		integrationContext.send('myRouter.inputChannel',"Hello")
		assert count == 2
	}

	@Test
	void testRouterWhen() {
		def flow = builder.messageFlow {

			route('myRouter', { it == 'Hello' ? 'foo' : 'bar' } )
			{
				when('foo') {
					handle {payload -> payload.toUpperCase()}
				}

				when('bar') {
					handle {payload -> payload.toLowerCase()}
				}
			}
		}

		assert builder.messageFlows.size() == 1

		def router = flow.components[0]
		assert router instanceof RouterComposition
		assert(router.components.size == 2)

		def routerCondition = router.components[0]
		assert(routerCondition instanceof RouterCondition)
		assert(routerCondition.components.size == 1)

		def sa = routerCondition.components[0]
		assert (sa instanceof ServiceActivator)

		assert flow.sendAndReceive('Hello') == 'HELLO'
		assert flow.sendAndReceive('SOMETHING') == 'something'
	}

	@Test
	void testRouterWithOtherwise() {
		def flow = builder.messageFlow {
			route('myRouter', { if (it == 'Hello' ) 'foo' } )
			{
				when('foo') {
					handle {payload -> payload.toUpperCase()}
				}

				otherwise {
					handle {payload -> payload.toLowerCase()}
				}
			}
		}
		assert flow.sendAndReceive('Hello') == 'HELLO'
		assert flow.sendAndReceive('SOMETHING') == 'something'
	}

	@Test
	void testMidstreamRouter() {
		def flow = builder.messageFlow {
			route('myRouter', { if (it == 'Hello' ) 'foo' } )
			{
				when('foo') {
					handle(outputChannel:'foo.out', {payload -> payload.toUpperCase()})
				}

				otherwise {
					handle(outputChannel:'default.out', {payload -> payload.toLowerCase()})
				}
			}
			messageFlow(inputChannel:'foo.out') {
				transform {it[0..1]}
			}
			messageFlow(inputChannel:'default.out') { transform {it*2} }
		}

		assert flow.sendAndReceive('Hello') == 'HE'
		assert flow.sendAndReceive('SOMETHING') == 'somethingsomething'
	}

	@Test
	void testRouterWithInvalidOtherwise () {
		try {
			builder.messageFlow {
				route('myRouter', { val -> println "route function: $val" }) {
					otherwise {}
					when('bar') { handle {} }
				}
			}
			fail('should throw assertion error')
		} catch (AssertionError e) {
		}
	}

	@Test
	void testChannelMappedRouter() {
		def flow = builder.messageFlow {
			route('myRouter', { Map headers -> headers.foo }) {
				map(bar:'barChannel',baz:'bazChannel')
			}
			transform(inputChannel:'barChannel',{it[0..1]},linkToNext:false)

			transform(inputChannel:'bazChannel',{it*2})
		}

		def router = flow.components[0]
		assert router instanceof RouterComposition
		assert(router.components.size == 0)

		assert router.channelMap
		assert router.channelMap == [bar:'barChannel',baz:'bazChannel']


		def message = MessageBuilder.withPayload('Hello').copyHeaders([foo:'bar']).build()
		assert flow.sendAndReceive(message).payload == 'He'

		message = MessageBuilder.withPayload('SOMETHING').copyHeaders([foo:'baz']).build()
		assert flow.sendAndReceive(message).payload == 'SOMETHINGSOMETHING'
	}
}

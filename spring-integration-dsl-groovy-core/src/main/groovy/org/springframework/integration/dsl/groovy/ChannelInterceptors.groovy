/*
 * Copyright 2002-2015 the original author or authors.
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

/**
 * @author David Turanski
 * @author Gary Russell
 *
 *
 */
class ChannelInterceptor extends IntegrationComponent {
	static attributesRequiresAnyOf = [
		'preSend',
		'postSend',
		'preReceive',
		'postReceive',
		'afterSendCompletion',
		'afterReceiveCompletion',
		'ref'
	]
	static mutuallyExclusiveAttributes = [
		['preSend', 'ref'],
		['postSend', 'ref'],
		['preReceive', 'ref'],
		['postReceive', 'ref'],
		['afterSendCompletion', 'ref'],
		['afterReceiveCompletion', 'ref']
	]
	Closure preSend
	Closure postSend
	Closure preReceive
	Closure postReceive
	Closure afterSendCompletion
	Closure afterReceiveCompletion
	boolean global = true

	ChannelInterceptor() {
		name =  name = defaultName('$ci')
	}
}

class Wiretap extends IntegrationComponent {
	static requiredAttributes = ['channel']
	boolean global = true
	Wiretap() {
		name = defaultName('$wt')
	}
}

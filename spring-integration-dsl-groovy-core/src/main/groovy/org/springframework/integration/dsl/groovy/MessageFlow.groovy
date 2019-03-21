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

/**
 * @author David Turanski
 *
 */
class MessageFlow extends BaseIntegrationComposition {
	def inputChannel
	def outputChannel
	def name
	IntegrationContext integrationContext

	MessageFlow(){
		name = defaultName('$mfl')
	}
	//For Java compatibility
	String getOutputChannel() {
		outputChannel
	}

	String getInputChannel() {
		inputChannel
	}

	String getName() {
		name
	}

	void send(msgOrPayload){
		integrationContext.send(inputChannel,msgOrPayload)
	}

	def sendAndReceive(msgOrPayload,long timeout=1000) {
		integrationContext.sendAndReceive(inputChannel,msgOrPayload,timeout)
	}
	
	def receive(long timeout=0) {
		integrationContext.receive(outputChannel,timeout)
	}

	def start() {
		integrationContext.applicationContext.start()
	}

	def stop() {
		integrationContext.applicationContext.start()
	}
}

class FlowExecution extends MessageProducingEndpoint {
	def messageFlow
	FlowExecution(MessageFlow messageFlow) {
		this.messageFlow = messageFlow
		inputChannel = messageFlow.inputChannel
		outputChannel = messageFlow.outputChannel
		name = messageFlow.name
	}
	String defaultNamePrefix() {
		"$mflw"
	}
}

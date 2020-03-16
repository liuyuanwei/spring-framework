/*
 * Copyright 2002-2016 the original author or authors.
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

package org.springframework.context;

/**
 * @since 1.1.1
 * 应用事件发布
 * 用于封装事件发布功能的接口，向事件监听器（Listener）发送事件消息。
 */
/*
	该接口提供了一个 #publishEvent(Object event, ...) 方法，用于通知在此应用程序中注册的所有的监听器。
	该方法在 AbstractApplicationContext 中实现。
 */
@FunctionalInterface
public interface ApplicationEventPublisher {

	//发布应用事件
	default void publishEvent(ApplicationEvent event) {
		publishEvent((Object) event);
	}

	//发布事件
	void publishEvent(Object event);

}

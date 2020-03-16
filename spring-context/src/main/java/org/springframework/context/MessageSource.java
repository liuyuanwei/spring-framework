/*
 * Copyright 2002-2017 the original author or authors.
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

import java.util.Locale;

import org.springframework.lang.Nullable;

/**
 * 管理 message ，实现国际化等功能
 * MessageSource 定义了获取 message 的策略方法
 */
/*
	MessageSource 定义了获取 message 的策略方法 #getMessage(...) 。
	在 ApplicationContext 体系中，该方法由 AbstractApplicationContext 实现。
	在 AbstractApplicationContext 中，它持有一个 MessageSource 实例，
	将 #getMessage(...) 方法委托给该实例(messageSource)来实现
 */
public interface MessageSource {

	/*
		获取消息信息
	 */
	@Nullable
	String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale);
	String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException;
	String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException;

}

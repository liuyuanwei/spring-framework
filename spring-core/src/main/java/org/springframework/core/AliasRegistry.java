/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Common interface for managing aliases. Serves as super-interface for
 * {@link org.springframework.beans.factory.support.BeanDefinitionRegistry}.
 *
 * @author Juergen Hoeller
 * @since 2.5.2
 * 用于别名管理的通用型接口，作为 BeanDefinitionRegistry 的顶层接口。 AliasRegistry 定义了一些别名管理的方法。
 */
public interface AliasRegistry {

	/*
		key: alias
		value: beanName
		private final Map<String, String> aliasMap = new ConcurrentHashMap<>(16);
	*/
	// 注册别名 alias 和 beanName 的映射
	void registerAlias(String name, String alias);

	// 移除别名
	void removeAlias(String alias);

	// 是否存在别名
	boolean isAlias(String name);

	// 获取所有别名
	String[] getAliases(String name);

}

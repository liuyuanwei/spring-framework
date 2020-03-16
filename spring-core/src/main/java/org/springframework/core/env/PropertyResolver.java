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

package org.springframework.core.env;

import org.springframework.lang.Nullable;

/**
 * Interface for resolving properties against any underlying source.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see Environment
 * @see PropertySourcesPropertyResolver
 * 属性解析器，用于解析属性配置
 * 属性解析器，用于解析任何基础源的属性的接口
 */
public interface PropertyResolver {
	/*
		从 API 上面我们就知道属性解析器 PropertyResolver 的作用了。下面是一个简单的运用。
			PropertyResolver propertyResolver = new PropertySourcesPropertyResolver(propertySources);

			System.out.println(propertyResolver.getProperty("name"));
			System.out.println(propertyResolver.getProperty("name", "chenssy"));
			System.out.println(propertyResolver.resolvePlaceholders("my name is  ${name}"));
	 */

	// 是否包含某个属性
	boolean containsProperty(String key);

	// 获取属性值 如果找不到返回null
	@Nullable
	String getProperty(String key);

	// 获取属性值，如果找不到返回默认值
	String getProperty(String key, String defaultValue);

	// 获取指定类型的属性值，找不到返回null
	@Nullable
	<T> T getProperty(String key, Class<T> targetType);

	// 获取指定类型的属性值，找不到返回默认值
	<T> T getProperty(String key, Class<T> targetType, T defaultValue);

	// 获取属性值，找不到抛出异常IllegalStateException
	String getRequiredProperty(String key) throws IllegalStateException;

	// 获取指定类型的属性值，找不到抛出异常IllegalStateException
	<T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException;

	// 替换文本中的占位符（${key}）到属性值，找不到不解析
	String resolvePlaceholders(String text);

	// 替换文本中的占位符（${key}）到属性值，找不到抛出异常IllegalArgumentException
	String resolveRequiredPlaceholders(String text) throws IllegalArgumentException;

}

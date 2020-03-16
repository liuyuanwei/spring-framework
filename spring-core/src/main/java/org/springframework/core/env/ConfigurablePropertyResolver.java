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

import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.lang.Nullable;

/**
 * Configuration interface to be implemented by most if not all {@link PropertyResolver}
 * types. Provides facilities for accessing and customizing the
 * {@link org.springframework.core.convert.ConversionService ConversionService}
 * used when converting property values from one type to another.
 *
 * @author Chris Beams
 * @since 3.1
 * 供属性类型转换的功能
 * 通俗点说就是 ConfigurablePropertyResolver 提供属性值类型转换所需要的 ConversionService。
 */
public interface ConfigurablePropertyResolver extends PropertyResolver {
	/*
		从 ConfigurablePropertyResolver 所提供的方法来看，除了访问和设置 ConversionService 外，
		主要还提供了一些解析规则之类的方法。

		就 Properties 体系而言，PropertyResolver 定义了访问 Properties 属性值的方法，
		而 ConfigurablePropertyResolver 则定义了解析 Properties 一些相关的规则和值进行类型转换所需要的 Service。

		该体系有两个实现者：
		AbstractPropertyResolver 和 PropertySourcesPropertyResolver，
		其中 AbstractPropertyResolver 为实现的抽象基类，PropertySourcesPropertyResolver 为真正的实现者。
	 */

	// 返回执行类型转换时使用的 ConfigurableConversionService
	ConfigurableConversionService getConversionService();

	// 设置 ConfigurableConversionService
	void setConversionService(ConfigurableConversionService conversionService);

	// 设置占位符前缀
	void setPlaceholderPrefix(String placeholderPrefix);

	// 设置占位符后缀
	void setPlaceholderSuffix(String placeholderSuffix);

	// 设置占位符与默认值之间的分隔符
	void setValueSeparator(@Nullable String valueSeparator);

	// 设置当遇到嵌套在给定属性值内的不可解析的占位符时是否抛出异常
	// 当属性值包含不可解析的占位符时，getProperty(String)及其变体的实现必须检查此处设置的值以确定正确的行为。
	void setIgnoreUnresolvableNestedPlaceholders(boolean ignoreUnresolvableNestedPlaceholders);

	// 指定必须存在哪些属性，以便由validateRequiredProperties（）验证
	void setRequiredProperties(String... requiredProperties);

	// 验证setRequiredProperties指定的每个属性是否存在并解析为非null值
	void validateRequiredProperties() throws MissingRequiredPropertiesException;

}

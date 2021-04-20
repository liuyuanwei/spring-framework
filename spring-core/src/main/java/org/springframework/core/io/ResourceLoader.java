/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.core.io;

import org.springframework.lang.Nullable;
import org.springframework.util.ResourceUtils;

/**
 * Spring 资源加载的统一抽象，具体的资源加载则由相应的实现类来完成，
 * 所以我们可以将 ResourceLoader 称作为统一资源定位器。
 *
 * From 《Spring 源码深度解析》：定义资源加载器，主要应用于根据给定的资源文件地址，返回对应的 Resource 。
 *
 * Strategy interface for loading resources (e.. class path or file system
 * resources). An {@link org.springframework.context.ApplicationContext}
 * is required to provide this functionality, plus extended
 * {@link org.springframework.core.io.support.ResourcePatternResolver} support.
 *
 * <p>{@link DefaultResourceLoader} is a standalone implementation that is
 * usable outside an ApplicationContext, also used by {@link ResourceEditor}.
 *
 * <p>Bean properties of type Resource and Resource array can be populated
 * from Strings when running in an ApplicationContext, using the particular
 * context's resource loading strategy.
 *
 * @author Juergen Hoeller
 * @since 10.03.2004
 * @see Resource
 * @see org.springframework.core.io.support.ResourcePatternResolver
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.context.ResourceLoaderAware
 * 】】】统一资源加载
 * Spring 将资源的定义和资源的加载区分开了，Resource 定义了统一的资源，【那资源的加载则由 ResourceLoader 来统一定义】
 * 为 Spring 【资源加载的统一抽象】，【具体的资源加载则由相应的实现类来完成】，所以我们可以将 ResourceLoader 称作为统一资源定位器
 * ResourceLoader，定义资源加载器，【主要应用于根据给定的资源文件地址，返回对应的 Resource 】。
 *
 * Resource getResource(String location);
 *
 * DefaultResourceLoader：与 AbstractResource 相似，DefaultResourceLoader 是 ResourceLoader 的默认实现。
 */
public interface ResourceLoader {

	/** Pseudo URL prefix for loading from the class path: "classpath:". */
	String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX; // CLASSPATH URL 前缀。默认为："classpath:"

	/**
     * 根据所提供资源的路径 location 返回 Resource 实例，
	 * 【但是它不确保该 Resource 一定存在，需要调用 `Resource#exist()` 方法来判断】。
     *
     *     * 该方法支持以下模式的资源加载：
     *         * URL位置资源，如 `"file:C:/test.dat"`
     *         * ClassPath位置资源，如”classpath:test.dat”
     *         * 相对路径资源，如 `"WEB-INF/test.dat`" ，此时返回的Resource实 例根据实现不同而不同。
     *  】】】该方法的主要实现是在其子类 DefaultResourceLoader 中实现，具体过程我们在分析 DefaultResourceLoader 时做详细说明。
     */
	Resource getResource(String location);

	/**
     * 返回 ClassLoader 实例，【对于想要获取 ResourceLoader 使用的 ClassLoader 用户来说，可以直接调用该方法来获取】。
     * 在分析 Resource 时，提到了一个类 ClassPathResource ，这个类是可以根据指定的 ClassLoader 来加载资源的。
	 * Expose the ClassLoader used by this ResourceLoader.
	 * <p>Clients which need to access the ClassLoader directly can do so
	 * in a uniform manner with the ResourceLoader, rather than relying
	 * on the thread context ClassLoader.
	 */
	@Nullable
	ClassLoader getClassLoader();

}

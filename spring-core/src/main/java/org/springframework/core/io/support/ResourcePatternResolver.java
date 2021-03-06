/*
 * Copyright 2002-2007 the original author or authors.
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

package org.springframework.core.io.support;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * ResourceLoader 的扩展，它支持根据指定的资源路径匹配模式每次返回**多个** Resource 实例
 *
 * Strategy interface for resolving a location pattern (for example,
 * an Ant-style path pattern) into Resource objects.
 *
 * <p>This is an extension to the {@link org.springframework.core.io.ResourceLoader}
 * interface. A passed-in ResourceLoader (for example, an
 * {@link org.springframework.context.ApplicationContext} passed in via
 * {@link org.springframework.context.ResourceLoaderAware} when running in a context)
 * can be checked whether it implements this extended interface too.
 *
 * <p>{@link PathMatchingResourcePatternResolver} is a standalone implementation
 * that is usable outside an ApplicationContext, also used by
 * {@link ResourceArrayPropertyEditor} for populating Resource array bean properties.
 *
 * <p>Can be used with any sort of location pattern (e.g. "/WEB-INF/*-context.xml"):
 * Input patterns have to match the strategy implementation. This interface just
 * specifies the conversion method rather than a specific pattern format.
 *
 * <p>This interface also suggests a new resource prefix "classpath*:" for all
 * matching resources from the class path. Note that the resource location is
 * expected to be a path without placeholders in this case (e.g. "/beans.xml");
 * JAR files or classes directories can contain multiple files of the same name.
 *
 * @author Juergen Hoeller
 * @since 1.0.2
 * @see org.springframework.core.io.Resource
 * @see org.springframework.core.io.ResourceLoader
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.context.ResourceLoaderAware
 *
 * ResourceLoader 的 Resource getResource(String location) 方法，每次只能根据 location 返回一个 Resource 。
 * 【当需要加载多个资源时】，我们除了多次调用 #getResource(String location) 方法外，别无他法。
 * ResourcePatternResolver 【是 ResourceLoader 的扩展，它支持根据指定的资源路径匹配模式【每次返回多个 Resource 实例】】
 * ResourcePatternResolver 在 ResourceLoader 的基础上增加了 #getResources(String locationPattern) 方法，以支持根据路径匹配模式返回多个 Resource 实例。
 * Resource[] getResources(String locationPattern) throws IOException;
 * 同时，【也新增了一种新的协议前缀 "classpath*:"，该协议前缀由其子类负责实现。】
 *
 * PathMatchingResourcePatternResolver 中实现，该类是 ResourcePatternResolver 接口的实现者。
 */
/*
	在 AbstractApplicationContext 中实现，在 AbstractApplicationContext 中他持有一个 ResourcePatternResolver 的实例对象。
	你会发现最终是在 PathMatchingResourcePatternResolver 中实现，该类是 ResourcePatternResolver 接口的实现者
 */
public interface ResourcePatternResolver extends ResourceLoader {

	// CLASSPATH URL 前缀。默认为："classpath*:" ，和 ResourceLoader 的 "classpath:" 不同。
	String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

	/**
	 * 根据路径, 获取资源列表
     * 返回多个 Resource 对象
	 */
	Resource[] getResources(String locationPattern) throws IOException;

}

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

package org.springframework.beans.factory;

/**
 * InitializingBean 为 Bean 提供了初始化方法的方式，它提供的 #afterPropertiesSet() 方法，用于执行初始化动作。
 * @see DisposableBean
 * @see org.springframework.beans.factory.config.BeanDefinition#getPropertyValues()
 * @see org.springframework.beans.factory.support.AbstractBeanDefinition#getInitMethodName()
 */
/*
	在 ApplicationContext 体系中，该方法由 AbstractRefreshableConfigApplicationContext 实现
 */
public interface InitializingBean {

	/**
     * 该方法在 BeanFactory 设置完了所有属性之后被调用
     * 该方法【允许 bean 实例设置了所有 bean 属性时】执行初始化工作，如果该过程出现了错误则需要抛出异常
	 */
	void afterPropertiesSet() throws Exception;

}

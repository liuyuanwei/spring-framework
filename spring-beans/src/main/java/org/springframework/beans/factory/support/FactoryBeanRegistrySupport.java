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

package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.lang.Nullable;

import java.security.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Support base class for singleton registries which need to handle
 * {@link org.springframework.beans.factory.FactoryBean} instances,
 * integrated with {@link DefaultSingletonBeanRegistry}'s singleton management.
 *
 * <p>Serves as base class for {@link AbstractBeanFactory}.
 *
 * @author Juergen Hoeller
 * @since 2.5.1
 * 学习
 * 		https://blog.csdn.net/qq_25179481/article/details/99618098
 */
public abstract class FactoryBeanRegistrySupport extends DefaultSingletonBeanRegistry {

	/**
     * Cache of singleton objects created by FactoryBeans: FactoryBean name to object.
     * 缓存 FactoryBean 创建的单例 Bean 对象的映射
     * beanName ===> Bean 对象
     * @see #getCachedObjectForFactoryBean(String)
     */
	private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>(16);

	/**
	 * Determine the type for the given FactoryBean.
	 * @param factoryBean the FactoryBean instance to check
	 * @return the FactoryBean's object type,
	 * or {@code null} if the type cannot be determined yet
	 */
	@Nullable
	protected Class<?> getTypeForFactoryBean(final FactoryBean<?> factoryBean) {
		try {
			if (System.getSecurityManager() != null) {
				return AccessController.doPrivileged((PrivilegedAction<Class<?>>)
						factoryBean::getObjectType, getAccessControlContext());
			}
			else {
				return factoryBean.getObjectType();
			}
		}
		catch (Throwable ex) {
			// Thrown from the FactoryBean's getObjectType implementation.
			logger.info("FactoryBean threw exception from getObjectType, despite the contract saying " +
					"that it should return null if the type of its object cannot be determined yet", ex);
			return null;
		}
	}

	/**
	 * Obtain an object to expose from the given FactoryBean, if available
	 * in cached form. Quick check for minimal synchronization.
	 * @param beanName the name of the bean
	 * @return the object obtained from the FactoryBean,
	 * or {@code null} if not available
	 */
	@Nullable
	protected Object getCachedObjectForFactoryBean(String beanName) {
		return this.factoryBeanObjectCache.get(beanName);
	}

	/**
	 * Obtain an object to expose from the given FactoryBean.
	 * @param factory the FactoryBean instance
	 * @param beanName the name of the bean
	 * @param shouldPostProcess whether the bean is subject to post-processing
	 * @return the object obtained from the FactoryBean
	 * @throws BeanCreationException if FactoryBean object creation failed
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	protected Object getObjectFromFactoryBean(FactoryBean<?> factory, String beanName, boolean shouldPostProcess) {
		/*
			若为单例且单例 Bean 缓存中存在 beanName ，则 <1> 进行后续处理（跳转到下一步），
			否则，则 <2> 从 FactoryBean 中获取 Bean 实例对象。
		 */
        // <1> 为单例模式且缓存中存在
		if (factory.isSingleton() && containsSingleton(beanName)) {
			// 其实我们在前面篇幅中发现了大量的同步锁，锁住的对象都是 this.singletonObjects，主要是因为在单例模式中必须要保证全局唯一。
			synchronized (getSingletonMutex()) { // <1.1> 单例锁

				/*
					从 factoryBeanObjectCache 缓存中获取实例对象 object 。
					若 object 为空，则调用 #doGetObjectFromFactoryBean(FactoryBean<?> factory, String beanName) 方法，从 FactoryBean 获取对象，
					其实内部就是调用 FactoryBean#getObject() 方法
				 */
                // <1.2> 从缓存中获取指定的 factoryBean
				Object object = this.factoryBeanObjectCache.get(beanName);
				if (object == null) {
                    // 为空，则从 FactoryBean 中获取对象
                    object = doGetObjectFromFactoryBean(factory, beanName);
                    // 从缓存中获取
                    // TODO 芋艿，具体原因
                    // Only post-process and store if not put there already during getObject() call above
					// (e.g. because of circular reference processing triggered by custom getBean calls)
					Object alreadyThere = this.factoryBeanObjectCache.get(beanName);
					if (alreadyThere != null) {
						object = alreadyThere;
					} else {

						/*
							 isSingletonCurrentlyInCreation
							这两方法是非常重要的操作，因为他们记录着 Bean 的加载状态，是检测当前 Bean 是否处于创建中的关键之处，对解决 Bean 循环依赖起着关键作用。
							#beforeSingletonCreation(String beanName) 方法，用于添加标志，当前 bean 正处于创建中
							#afterSingletonCreation(String beanName) 方法，用于移除标记，当前 Bean 不处于创建中。
						 */

                        // <1.3> 需要后续处理
						// 如果需要后续处理( shouldPostProcess = true )，则进行进一步处理
                        if (shouldPostProcess) {
                            // 若该 Bean 处于创建中，则返回非处理对象，而不是存储它
                            if (isSingletonCurrentlyInCreation(beanName)) {
								// Temporarily return non-post-processed object, not storing it yet..
								return object;
							}
                            // 单例 Bean 的前置处理
							// 进行创建之前的处理。默认实现将该 Bean 标志为当前创建的。
							beforeSingletonCreation(beanName);
							try {
                                // 对从 FactoryBean 获取的对象进行后处理
                                // 生成的对象将暴露给 bean 引用
								// 对从 FactoryBean 处获取的 Bean 实例对象进行后置处理。
								// 【其默认实现是直接返回 object 对象，不做任何处理】。
								object = postProcessObjectFromFactoryBean(object, beanName);
							} catch (Throwable ex) {
								throw new BeanCreationException(beanName,
										"Post-processing of FactoryBean's singleton object failed", ex);
							} finally {
                                // 单例 Bean 的后置处理
								// 进行创建 Bean 之后的处理，默认实现是将该 bean 标记为不再在创建中。
                                afterSingletonCreation(beanName);
							}
						}
						// 添加到 factoryBeanObjectCache 中，进行缓存
						if (containsSingleton(beanName)) {
							this.factoryBeanObjectCache.put(beanName, object);
						}
					}
				}
				return object;
			}
		// <2>
		} else {
            // 为空，则从 FactoryBean 中获取对象
            Object object = doGetObjectFromFactoryBean(factory, beanName);
            // 需要后续处理
            if (shouldPostProcess) {
				try {
                    // 对从 FactoryBean 获取的对象进行后处理
                    // 生成的对象将暴露给 bean 引用
					// 对从 FactoryBean 处获取的 Bean 实例对象进行后置处理。
					// 【其默认实现是直接返回 object 对象，不做任何处理】。
					object = postProcessObjectFromFactoryBean(object, beanName);
				}
				catch (Throwable ex) {
					throw new BeanCreationException(beanName, "Post-processing of FactoryBean's object failed", ex);
				}
			}
			return object;
		}
	}

	/**
	 * Obtain an object to expose from the given FactoryBean.
	 * @param factory the FactoryBean instance
	 * @param beanName the name of the bean
	 * @return the object obtained from the FactoryBean
	 * @throws BeanCreationException if FactoryBean object creation failed
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	private Object doGetObjectFromFactoryBean(final FactoryBean<?> factory, final String beanName)
			throws BeanCreationException {
		Object object;
		try {
            // 需要权限验证
			if (System.getSecurityManager() != null) {
				AccessControlContext acc = getAccessControlContext();
				try {
                    // 从 FactoryBean 中，获得 Bean 对象
                    object = AccessController.doPrivileged((PrivilegedExceptionAction<Object>) factory::getObject, acc);
				} catch (PrivilegedActionException pae) {
					throw pae.getException();
				}
			} else {
			    // 从 FactoryBean 中，获得 Bean 对象
				object = factory.getObject();
			}
		} catch (FactoryBeanNotInitializedException ex) {
			throw new BeanCurrentlyInCreationException(beanName, ex.toString());
		} catch (Throwable ex) {
			throw new BeanCreationException(beanName, "FactoryBean threw exception on object creation", ex);
		}

		// Do not accept a null value for a FactoryBean that's not fully
		// initialized yet: Many FactoryBeans just return null then.
		if (object == null) {
			if (isSingletonCurrentlyInCreation(beanName)) {
				throw new BeanCurrentlyInCreationException(
						beanName, "FactoryBean which is currently in creation returned null from getObject");
			}
			object = new NullBean();
		}
		return object;
	}

	/**
	 * Post-process the given object that has been obtained from the FactoryBean.
	 * The resulting object will get exposed for bean references.
	 * <p>The default implementation simply returns the given object as-is.
	 * Subclasses may override this, for example, to apply post-processors.
	 * @param object the object obtained from the FactoryBean.
	 * @param beanName the name of the bean
	 * @return the object to expose
	 * @throws org.springframework.beans.BeansException if any post-processing failed
	 */
	protected Object postProcessObjectFromFactoryBean(Object object, String beanName) throws BeansException {
		return object;
	}

	/**
	 * Get a FactoryBean for the given bean if possible.
	 * @param beanName the name of the bean
	 * @param beanInstance the corresponding bean instance
	 * @return the bean instance as FactoryBean
	 * @throws BeansException if the given bean cannot be exposed as a FactoryBean
	 */
	protected FactoryBean<?> getFactoryBean(String beanName, Object beanInstance) throws BeansException {
		if (!(beanInstance instanceof FactoryBean)) {
			throw new BeanCreationException(beanName,
					"Bean instance of type [" + beanInstance.getClass() + "] is not a FactoryBean");
		}
		return (FactoryBean<?>) beanInstance;
	}

	/**
	 * Overridden to clear the FactoryBean object cache as well.
	 */
	@Override
	protected void removeSingleton(String beanName) {
		synchronized (getSingletonMutex()) {
			super.removeSingleton(beanName);
			this.factoryBeanObjectCache.remove(beanName);
		}
	}

	/**
	 * Overridden to clear the FactoryBean object cache as well.
	 */
	@Override
	protected void clearSingletonCache() {
		synchronized (getSingletonMutex()) {
			super.clearSingletonCache();
			this.factoryBeanObjectCache.clear();
		}
	}

	/**
	 * Return the security context for this bean factory. If a security manager
	 * is set, interaction with the user code will be executed using the privileged
	 * of the security context returned by this method.
	 * @see AccessController#getContext()
	 */
	protected AccessControlContext getAccessControlContext() {
		return AccessController.getContext();
	}

}

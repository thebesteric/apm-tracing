package io.github.thebesteric.framework.apm.agent.core.interceptor;

import io.github.thebesteric.framework.apm.agent.core.enhance.EnhancedInstance;

/**
 * 构造方法的拦截器必须实现这个接口
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-21 00:21:42
 */
public interface ConstructorInterceptor {

    /**
     * 在构造器执行后调用
     *
     * @param instance 对象实例
     * @param args     构造参数
     * @author wangweijun
     * @since 2023/9/21 00:22
     */
    void afterConstructor(EnhancedInstance instance, Object[] args);
}

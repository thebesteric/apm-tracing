package io.github.thebesteric.framework.apm.agent.core.interceptor;

import io.github.thebesteric.framework.apm.agent.core.enhance.EnhancedInstance;
import io.github.thebesteric.framework.apm.agent.core.interceptor.support.InstanceMethodLogSupport;

import java.lang.reflect.Method;

/**
 * 实例方法的的拦截器必须实现这个接口
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-21 00:34:22
 */
public interface InstanceMethodsAroundInterceptor extends InstanceMethodLogSupport {

    /**
     * 前置通知
     *
     * @param instance 实例
     * @param method   方法
     * @param args     参数
     * @param argTypes 参数类型
     * @author wangweijun
     * @since 2023/9/20 01:10
     */
    void beforeMethod(EnhancedInstance instance, Method method, Object[] args, Class<?>[] argTypes);

    /**
     * 后置通知，无论是否出现异常都会执行
     *
     * @param instance 实例
     * @param method   方法
     * @param args     参数
     * @param argTypes 参数类型
     * @param result   返回值
     * @return Object
     * @author wangweijun
     * @since 2023/9/20 01:10
     */
    Object afterMethod(EnhancedInstance instance, Method method, Object[] args, Class<?>[] argTypes, Object result);

    /**
     * 前置通知
     *
     * @param instance  实例
     * @param method    方法
     * @param args      参数
     * @param argTypes  参数类型
     * @param result    返回值
     * @param throwable 异常
     * @author wangweijun
     * @since 2023/9/20 01:10
     */
    void handleException(EnhancedInstance instance, Method method, Object[] args, Class<?>[] argTypes, Object result, Throwable throwable);
}
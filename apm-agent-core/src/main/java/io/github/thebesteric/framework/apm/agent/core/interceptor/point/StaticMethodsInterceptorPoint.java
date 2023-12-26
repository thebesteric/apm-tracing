package io.github.thebesteric.framework.apm.agent.core.interceptor.point;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * 静态方法拦截点
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-19 00:08:39
 */
public interface StaticMethodsInterceptorPoint {

    /**
     * 要拦截哪些方法
     *
     * @return ElementMatcher<MethodDescription> 作为 DynamicType.Builder 的 method 方法的参数
     * @author wangweijun
     * @since 2023/9/19 00:12
     */
    ElementMatcher<MethodDescription> getMethodMatcher();

    /**
     * 获取被增强的方法的拦截器，必须实现 StaticMethodsInterceptor
     *
     * @return String
     * @author wangweijun
     * @since 2023/9/19 00:15
     */
    String getMethodInterceptor();

}

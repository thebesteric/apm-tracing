package io.github.thebesteric.framework.apm.agent.core.interceptor;

import io.github.thebesteric.framework.apm.agent.commons.LoggerPrinter;
import io.github.thebesteric.framework.apm.agent.core.loader.InterceptorInstanceLoader;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * 静态方法拦截器
 *
 * @author wangweijun
 * @since 2023/9/20 01:05
 */
@Slf4j
public class StaticMethodsInterceptor {

    private StaticMethodsAroundInterceptor staticMethodsAroundInterceptor;

    /**
     * 构造方法
     *
     * @param interceptor StaticMethodsAroundInterceptor 的实现类
     * @param classLoader 类加载器
     * @author wangweijun
     * @since 2023/9/21 00:24
     */
    public StaticMethodsInterceptor(String interceptor, ClassLoader classLoader) {
        try {
            staticMethodsAroundInterceptor = InterceptorInstanceLoader.load(interceptor, classLoader);
        } catch (Exception e) {
            LoggerPrinter.error(log, "Cannot load interceptor: {}", interceptor, e);
        }
    }

    @RuntimeType
    public Object intercept(@Origin Class<?> clazz,
                            @Origin Method targetMethod,
                            @AllArguments Object[] targetMethodArgs,
                            @SuperCall Callable<?> call) throws Throwable {
        // 前置通知
        try {
            staticMethodsAroundInterceptor.beforeMethod(clazz, targetMethod, targetMethodArgs, targetMethod.getParameterTypes());
        } catch (Exception ex) {
            LoggerPrinter.error(log, "Class {} before static method {} interceptor failure", clazz, targetMethod.getName(), ex);
        }

        Object result = null;
        try {
            result = call.call();
        } catch (Exception e) {
            // 异常通知
            try {
                staticMethodsAroundInterceptor.handleException(clazz, targetMethod, targetMethodArgs, targetMethod.getParameterTypes(), result, e);
            } catch (Exception ex) {
                LoggerPrinter.error(log, "Class {} execute static method {} interceptor failure", clazz, targetMethod.getName(), ex);
            }
            throw e;
        } finally {
            // 最终通知
            try {
                result = staticMethodsAroundInterceptor.afterMethod(clazz, targetMethod, targetMethodArgs, targetMethod.getParameterTypes(), result);
            } catch (Exception ex) {
                LoggerPrinter.error(log, "Class {} after static method {} interceptor failure", clazz, targetMethod.getName(), ex);
            }
        }

        return result;
    }

}
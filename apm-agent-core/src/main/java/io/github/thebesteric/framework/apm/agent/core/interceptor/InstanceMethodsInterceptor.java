package io.github.thebesteric.framework.apm.agent.core.interceptor;

import io.github.thebesteric.framework.apm.agent.commons.LoggerPrinter;
import io.github.thebesteric.framework.apm.agent.core.enhance.CleanerContext;
import io.github.thebesteric.framework.apm.agent.core.enhance.EnhancedInstance;
import io.github.thebesteric.framework.apm.agent.commons.IdentifierContext;
import io.github.thebesteric.framework.apm.agent.core.loader.InterceptorInstanceLoader;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * 实例方法拦截器
 *
 * @author wangweijun
 * @since 2023/9/20 01:05
 */
@Slf4j
public class InstanceMethodsInterceptor {

    private InstanceMethodsAroundInterceptor instanceMethodsAroundInterceptor;

    /**
     * 构造方法
     *
     * @param interceptor InstanceMethodsAroundInterceptor 的实现类
     * @param classLoader 类加载器
     * @author wangweijun
     * @since 2023/9/21 00:34:24
     */
    public InstanceMethodsInterceptor(String interceptor, ClassLoader classLoader) {
        try {
            instanceMethodsAroundInterceptor = InterceptorInstanceLoader.load(interceptor, classLoader);
        } catch (Exception e) {
            LoggerPrinter.error(log, "Cannot load interceptor: {}", interceptor, e);
        }
    }

    @RuntimeType
    public Object intercept(@This Object target,
                            @Origin Method targetMethod,
                            @AllArguments Object[] targetMethodArgs,
                            @SuperCall Callable<?> call) throws Exception {
        // 强转
        EnhancedInstance enhancedInstance = (EnhancedInstance) target;

        // 初始化相关信息
        IdentifierContext.initialize(targetMethod);

        // 前置通知
        try {
            instanceMethodsAroundInterceptor.beforeMethod(enhancedInstance, targetMethod, targetMethodArgs, targetMethod.getParameterTypes());
        } catch (Exception ex) {
            LoggerPrinter.error(log, "class {} before instance method {} interceptor failure", target.getClass(), targetMethod.getName(), ex);
        }

        Object result = null;
        try {
            result = call.call();
        } catch (Exception ex) {
            // 异常通知
            try {
                instanceMethodsAroundInterceptor.handleException(enhancedInstance, targetMethod, targetMethodArgs, targetMethod.getParameterTypes(), result, ex);
            } catch (Exception e) {
                LoggerPrinter.error(log, "class {} execute instance method {} interceptor failure", target.getClass(), targetMethod.getName(), e);
            }
            throw ex;
        } finally {
            // 最终通知
            try {
                result = instanceMethodsAroundInterceptor.afterMethod(enhancedInstance, targetMethod, targetMethodArgs, targetMethod.getParameterTypes(), result);
            } catch (Exception ex) {
                LoggerPrinter.error(log, "class {} after instance method {} interceptor failure", target.getClass(), targetMethod.getName(), ex);
            }
            // 清理
            CleanerContext.cleanup();
        }

        return result;
    }

}
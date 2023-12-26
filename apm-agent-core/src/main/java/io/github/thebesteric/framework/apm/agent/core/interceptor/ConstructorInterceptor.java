package io.github.thebesteric.framework.apm.agent.core.interceptor;

import io.github.thebesteric.framework.apm.agent.commons.LoggerPrinter;
import io.github.thebesteric.framework.apm.agent.core.enhance.EnhancedInstance;
import io.github.thebesteric.framework.apm.agent.core.loader.InterceptorInstanceLoader;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

/**
 * 构造方法的拦截器
 *
 * @author wangweijun
 * @since 2023-09-26 15:20:29
 */
@Slf4j
public class ConstructorInterceptor {

    private ConstructorAfterInterceptor constructorAfterInterceptor;

    /**
     * 构造方法
     *
     * @param interceptor constructorInterceptor 的实现类
     * @param classLoader 类加载器
     * @author wangweijun
     * @since 2023/9/21 00:24
     */
    public ConstructorInterceptor(String interceptor, ClassLoader classLoader) {
        try {
            constructorAfterInterceptor = InterceptorInstanceLoader.load(interceptor, classLoader);
        } catch (Exception e) {
            LoggerPrinter.error(log, "Cannot load interceptor: {}", interceptor, e);
        }
    }

    @RuntimeType
    public void intercept(@This Object obj, @AllArguments Object[] targetMethodArgs) {
        try {
            // 强转
            EnhancedInstance enhancedInstance = (EnhancedInstance) obj;
            constructorAfterInterceptor.afterConstructor(enhancedInstance, targetMethodArgs);
        } catch (Exception e) {
            LoggerPrinter.error(log, "Constructor failed", e);
        }
    }

}

package io.github.thebesteric.framework.apm.agent.plugins;

import io.github.thebesteric.framework.apm.agent.core.enhance.ClassEnhancePluginDefine;
import io.github.thebesteric.framework.apm.agent.core.matcher.ClassMatcher;
import io.github.thebesteric.framework.apm.agent.core.matcher.MultiClassNameMatcher;
import io.github.thebesteric.framework.apm.agent.core.interceptor.point.ConstructorMethodsInterceptorPoint;
import io.github.thebesteric.framework.apm.agent.core.interceptor.point.InstanceMethodsInterceptorPoint;
import io.github.thebesteric.framework.apm.agent.core.interceptor.point.StaticMethodsInterceptorPoint;
import io.github.thebesteric.framework.apm.agent.plugins.interceptor.FeignInterceptor;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * FeignInstrument
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-10-11 19:28:45
 */
public class FeignInstrument extends ClassEnhancePluginDefine {

    private static final String FEIGN_REFLECTIVE_CLASS = "feign.ReflectiveFeign$FeignInvocationHandler";
    private static final String FEIGN_SYNCHRONOUS_METHOD_HANDLER_CLASS = "feign.SynchronousMethodHandler";

    @Override
    protected ClassMatcher enhanceClass() {
        return MultiClassNameMatcher.byClassNames(FEIGN_REFLECTIVE_CLASS, FEIGN_SYNCHRONOUS_METHOD_HANDLER_CLASS);
    }

    @Override
    protected InstanceMethodsInterceptorPoint[] getInstanceMethodsInterceptorPoints() {
        return new InstanceMethodsInterceptorPoint[] {
                new InstanceMethodsInterceptorPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodMatcher() {
                        return named("invoke");
                    }

                    @Override
                    public String getMethodInterceptor() {
                        return FeignInterceptor.class.getName();
                    }
                }
        };
    }

    @Override
    protected ConstructorMethodsInterceptorPoint[] getConstructorMethodsInterceptorPoints() {
        return new ConstructorMethodsInterceptorPoint[0];
    }

    @Override
    protected StaticMethodsInterceptorPoint[] getStaticMethodsInterceptorPoints() {
        return new StaticMethodsInterceptorPoint[0];
    }
}

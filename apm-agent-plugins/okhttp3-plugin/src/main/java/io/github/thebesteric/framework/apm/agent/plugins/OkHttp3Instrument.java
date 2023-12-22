package io.github.thebesteric.framework.apm.agent.plugins;

import io.github.thebesteric.framework.apm.agent.core.enhance.ClassEnhancePluginDefine;
import io.github.thebesteric.framework.apm.agent.core.matcher.ClassMatcher;
import io.github.thebesteric.framework.apm.agent.core.matcher.MultiClassNameMatcher;
import io.github.thebesteric.framework.apm.agent.core.point.ConstructorMethodsInterceptorPoint;
import io.github.thebesteric.framework.apm.agent.core.point.InstanceMethodsInterceptorPoint;
import io.github.thebesteric.framework.apm.agent.core.point.StaticMethodsInterceptorPoint;
import io.github.thebesteric.framework.apm.agent.plugins.interceptor.OkHttp3Interceptor;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * OkHttp3Instrument
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-10-11 13:53:28
 */
public class OkHttp3Instrument extends ClassEnhancePluginDefine {

    public static final String OK_HTTP3_CLASS = "okhttp3.RealCall";
    public static final String OK_HTTP3_INTERNAL_CLASS = "okhttp3.internal.connection.RealCall";

    @Override
    protected ClassMatcher enhanceClass() {
        return MultiClassNameMatcher.byClassNames(OK_HTTP3_CLASS, OK_HTTP3_INTERNAL_CLASS);
    }

    @Override
    protected InstanceMethodsInterceptorPoint[] getInstanceMethodsInterceptorPoints() {
        return new InstanceMethodsInterceptorPoint[] {
                new InstanceMethodsInterceptorPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodMatcher() {
                        return named("execute");
                    }

                    @Override
                    public String getMethodInterceptor() {
                        return OkHttp3Interceptor.class.getName();
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

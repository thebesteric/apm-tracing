package io.github.thebesteric.framework.apm.agent.plugins;

import io.github.thebesteric.framework.apm.agent.core.enhance.ClassEnhancePluginDefine;
import io.github.thebesteric.framework.apm.agent.core.matcher.ClassMatcher;
import io.github.thebesteric.framework.apm.agent.core.matcher.MultiClassNameMatcher;
import io.github.thebesteric.framework.apm.agent.core.point.ConstructorMethodsInterceptorPoint;
import io.github.thebesteric.framework.apm.agent.core.point.InstanceMethodsInterceptorPoint;
import io.github.thebesteric.framework.apm.agent.core.point.StaticMethodsInterceptorPoint;
import io.github.thebesteric.framework.apm.agent.plugins.interceptor.ApacheHttpClientInterceptor;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * ApacheHttpClientInstrument
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-10-11 10:53:34
 */
public class ApacheHttpClientInstrument extends ClassEnhancePluginDefine {

    public static final String CLOSEABLE_HTTP_CLIENT_CLASS = "org.apache.http.impl.client.InternalHttpClient";

    @Override
    protected ClassMatcher enhanceClass() {
        return MultiClassNameMatcher.byClassNames(CLOSEABLE_HTTP_CLIENT_CLASS);
    }

    @Override
    protected InstanceMethodsInterceptorPoint[] getInstanceMethodsInterceptorPoints() {
        return new InstanceMethodsInterceptorPoint[] {
                new InstanceMethodsInterceptorPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodMatcher() {
                        return named("doExecute");
                    }

                    @Override
                    public String getMethodInterceptor() {
                        return ApacheHttpClientInterceptor.class.getName();
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

package io.github.thebesteric.framework.apm.agent.plugins.spring;

import io.github.thebesteric.framework.apm.agent.core.enhance.ClassEnhancePluginDefine;
import io.github.thebesteric.framework.apm.agent.core.matcher.ClassMatcher;
import io.github.thebesteric.framework.apm.agent.core.matcher.MultiClassNameMatcher;
import io.github.thebesteric.framework.apm.agent.core.point.ConstructorMethodsInterceptorPoint;
import io.github.thebesteric.framework.apm.agent.core.point.InstanceMethodsInterceptorPoint;
import io.github.thebesteric.framework.apm.agent.core.point.StaticMethodsInterceptorPoint;
import io.github.thebesteric.framework.apm.agent.plugins.spring.interceptor.RestTemplateInterceptor;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * RestTemplateInstrumentation
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-10-10 15:04:39
 */
public class RestTemplateInstrumentation extends ClassEnhancePluginDefine {

    private static final String REST_TEMPLATE_CLASS = "org.springframework.web.client.RestTemplate";

    @Override
    protected ClassMatcher enhanceClass() {
        return MultiClassNameMatcher.byClassNames(REST_TEMPLATE_CLASS);
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
                        return RestTemplateInterceptor.class.getName();
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

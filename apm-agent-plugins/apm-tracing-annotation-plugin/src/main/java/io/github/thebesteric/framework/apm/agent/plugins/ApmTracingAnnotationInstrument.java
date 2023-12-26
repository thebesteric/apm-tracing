package io.github.thebesteric.framework.apm.agent.plugins;

import io.github.thebesteric.framework.apm.agent.core.enhance.ClassEnhancePluginDefine;
import io.github.thebesteric.framework.apm.agent.core.matcher.ClassMatcher;
import io.github.thebesteric.framework.apm.agent.core.matcher.MultiClassAnnotationNameMatcher;
import io.github.thebesteric.framework.apm.agent.core.interceptor.point.ConstructorMethodsInterceptorPoint;
import io.github.thebesteric.framework.apm.agent.core.interceptor.point.InstanceMethodsInterceptorPoint;
import io.github.thebesteric.framework.apm.agent.core.interceptor.point.StaticMethodsInterceptorPoint;
import io.github.thebesteric.framework.apm.agent.plugins.interceptor.ApmTracingAnnotationInterceptor;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * ApmTracingAnnotationInstrument
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-28 11:13:49
 */
public class ApmTracingAnnotationInstrument extends ClassEnhancePluginDefine {

    public static final String AMP_TRACING_ANNOTATION_NAME = "io.github.thebesteric.framework.apm.agent.extension.annotation.ApmTracing";

    @Override
    protected ClassMatcher enhanceClass() {
        return MultiClassAnnotationNameMatcher.byClassAnnotationNames(AMP_TRACING_ANNOTATION_NAME);
    }

    @Override
    protected InstanceMethodsInterceptorPoint[] getInstanceMethodsInterceptorPoints() {
        return new InstanceMethodsInterceptorPoint[] {
                new InstanceMethodsInterceptorPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodMatcher() {
                        return not(isStatic())
                                .and(isAnnotatedWith(named(AMP_TRACING_ANNOTATION_NAME)));
                    }

                    @Override
                    public String getMethodInterceptor() {
                        return ApmTracingAnnotationInterceptor.class.getName();
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

package io.github.thebesteric.framework.apm.agent.plugins.spring;


import io.github.thebesteric.framework.apm.agent.core.enhance.ClassEnhancePluginDefine;
import io.github.thebesteric.framework.apm.agent.core.point.ConstructorMethodsInterceptorPoint;
import io.github.thebesteric.framework.apm.agent.core.point.InstanceMethodsInterceptorPoint;
import io.github.thebesteric.framework.apm.agent.core.point.StaticMethodsInterceptorPoint;
import io.github.thebesteric.framework.apm.agent.plugins.spring.interceptor.SpringMVCInterceptor;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * Spring MVC 插件
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-19 00:21:39
 */
public abstract class AbstractSpringMVCInstrumentation extends ClassEnhancePluginDefine {

    private static final String MAPPING_PKG_PREFIX = "org.springframework.web.bind.annotation";
    private static final String MAPPING_SUFFIX = "Mapping";

    @Override
    protected InstanceMethodsInterceptorPoint[] getInstanceMethodsInterceptorPoints() {
        return new InstanceMethodsInterceptorPoint[] {
                new InstanceMethodsInterceptorPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodMatcher() {
                        return not(isStatic())
                                .and(isAnnotatedWith(nameStartsWith(MAPPING_PKG_PREFIX).and(nameEndsWith(MAPPING_SUFFIX))));
                    }

                    @Override
                    public String getMethodInterceptor() {
                        return SpringMVCInterceptor.class.getName();
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

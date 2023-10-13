package io.github.thebesteric.framework.apm.agent.plugins.spring;


import io.github.thebesteric.framework.apm.agent.core.matcher.ClassMatcher;
import io.github.thebesteric.framework.apm.agent.core.matcher.MultiClassAnnotationNameMatcher;

/**
 * 拦截带有 @Controller 注解的插件
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-19 11:53:51
 */
public class ControllerInstrumentation extends AbstractSpringMVCInstrumentation {
    private static final String CONTROLLER_NAME = "org.springframework.stereotype.Controller";

    @Override
    protected ClassMatcher enhanceClass() {
        return MultiClassAnnotationNameMatcher.byClassAnnotationNames(CONTROLLER_NAME);
    }
}

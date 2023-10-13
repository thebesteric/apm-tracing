package io.github.thebesteric.framework.apm.agent.plugins.spring;

import io.github.thebesteric.framework.apm.agent.core.matcher.ClassMatcher;
import io.github.thebesteric.framework.apm.agent.core.matcher.MultiClassAnnotationNameMatcher;

/**
 * 拦截带有 @RestController 注解的插件
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-19 11:53:51
 */
public class RestControllerInstrumentation extends AbstractSpringMVCInstrumentation {
    private static final String REST_CONTROLLER_NAME = "org.springframework.web.bind.annotation.RestController";

    @Override
    protected ClassMatcher enhanceClass() {
        return MultiClassAnnotationNameMatcher.byClassAnnotationNames(REST_CONTROLLER_NAME);
    }
}

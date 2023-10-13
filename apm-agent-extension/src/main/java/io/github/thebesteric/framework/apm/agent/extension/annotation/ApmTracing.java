package io.github.thebesteric.framework.apm.agent.extension.annotation;

import io.github.thebesteric.framework.apm.agent.commons.constant.ApmTracingConstants;
import io.github.thebesteric.framework.apm.agent.commons.constant.Level;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApmTracing {
    String tag() default ApmTracingConstants.DEFAULT_TAG;
    String extra() default ApmTracingConstants.DEFAULT_EXTRA;
    Level level() default Level.INFO;
}

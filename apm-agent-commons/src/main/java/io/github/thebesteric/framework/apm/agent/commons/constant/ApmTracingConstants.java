package io.github.thebesteric.framework.apm.agent.commons.constant;

import io.github.thebesteric.framework.apm.agent.commons.exception.PrivateConstructorException;

/**
 * ApmTracing 注解常量类
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-10-08 16:46:48
 */
public final class ApmTracingConstants {

    public static final String DEFAULT_TAG = "default";
    public static final String DEFAULT_EXTRA = "";
    public static final String DEFAULT_LEVEL = "info";

    private ApmTracingConstants() {
        throw new PrivateConstructorException();
    }
}

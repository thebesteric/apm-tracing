package io.github.thebesteric.framework.apm.agent.core.enhance;

import io.github.thebesteric.framework.apm.agent.commons.IdentifierContext;
import io.github.thebesteric.framework.apm.agent.commons.exception.PrivateConstructorException;

/**
 * CleanerContext
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-10-07 16:46:20
 */
public class CleanerContext {

    private CleanerContext() {
        throw new PrivateConstructorException();
    }

    public static void cleanup() {
        IdentifierContext.remove();
    }

}

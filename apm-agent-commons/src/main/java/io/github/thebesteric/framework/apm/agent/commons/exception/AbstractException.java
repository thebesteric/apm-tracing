package io.github.thebesteric.framework.apm.agent.commons.exception;

import io.github.thebesteric.framework.apm.agent.commons.util.PlaceholderUtils;

/**
 * AbstractException
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-10-07 17:59:43
 */
public abstract class AbstractException extends RuntimeException {
    private static final long serialVersionUID = 7816564581802666717L;

    protected AbstractException(String message, Object... params) {
        super(PlaceholderUtils.format(message, params));
    }
}

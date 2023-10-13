package io.github.thebesteric.framework.apm.agent.commons.exception;

/**
 * PrivateConstructorException
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-26 17:13:24
 */
public class PrivateConstructorException extends AbstractException {
    private static final long serialVersionUID = 469044132858957608L;

    public PrivateConstructorException() {
        super("Utility class");
    }
}

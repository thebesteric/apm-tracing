package io.github.thebesteric.framework.apm.agent.commons.exception;

/**
 * IllegalArgumentException
 *
 * @author Eric Joe
 * @version 1.0
 */
public class IllegalArgumentException extends AbstractException {
    private static final long serialVersionUID = 1439983552389717849L;

    public IllegalArgumentException() {
        super("Illegal argument exception");
    }
    public IllegalArgumentException(String message, Object... params) {
        super(message, params);
    }
}

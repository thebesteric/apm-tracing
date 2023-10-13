package io.github.thebesteric.framework.apm.agent.commons.util;

import io.github.thebesteric.framework.apm.agent.commons.IdentifierContext;
import io.github.thebesteric.framework.apm.agent.commons.exception.PrivateConstructorException;

/**
 * TransactionUtils
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-10-11 17:46:31
 */
public class TransactionUtils {

    private TransactionUtils() {
        throw new PrivateConstructorException();
    }

    public static String get() {
        return IdentifierContext.getTraceIdentifier();
    }

    public static void set(String traceId) {
        IdentifierContext.setTraceIdentifier(traceId);
    }

}

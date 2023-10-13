package io.github.thebesteric.framework.apm.agent.commons.util;

import io.github.thebesteric.framework.apm.agent.commons.exception.PrivateConstructorException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * ExceptionUtils
 *
 * @author Eric Joe
 * @since 1.0
 */
public class ExceptionUtils {

    private ExceptionUtils() {
        throw new PrivateConstructorException();
    }

    public static String getSimpleMessage(Throwable throwable) {
        if (throwable != null) {
            String exTitle = getTitle(throwable);
            StackTraceElement exCause = getMajorCause(throwable);
            return exTitle + (exCause == null ? "" : ": " + exCause);
        }
        return null;
    }

    public static String getTitle(Throwable throwable) {
        String className = throwable.getClass().getName();
        return StringUtils.isNotEmpty(throwable.getMessage()) ? className + ": " + throwable.getMessage() : throwable.toString();
    }

    public static StackTraceElement[] getCauses(Throwable throwable) {
        return throwable.getStackTrace();
    }

    public static StackTraceElement getMajorCause(Throwable throwable) {
        StackTraceElement[] causes = getCauses(throwable);
        return CollectionUtils.isEmpty(Arrays.asList(causes)) ? null : causes[0];
    }

}

package io.github.thebesteric.framework.apm.agent.commons.util;

import io.github.thebesteric.framework.apm.agent.commons.exception.PrivateConstructorException;

/**
 * ReflectUtils
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-28 12:13:32
 */
public class ReflectUtils {

    private ReflectUtils() {
        throw new PrivateConstructorException();
    }

    public static boolean isClassExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}

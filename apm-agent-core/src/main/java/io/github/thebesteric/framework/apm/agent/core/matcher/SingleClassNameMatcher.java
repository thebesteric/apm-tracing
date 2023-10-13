package io.github.thebesteric.framework.apm.agent.core.matcher;

import lombok.Getter;

/**
 * 类名匹配器：仅适用于 ElementMatchers.named(xxx) 的情况
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-19 00:30:16
 */
@Getter
public class SingleClassNameMatcher implements ClassMatcher {

    private final String className;

    private SingleClassNameMatcher(String className) {
        if (className == null || className.isEmpty()) {
            throw new IllegalArgumentException("class name cannot be null");
        }
        this.className = className;
    }

    public static SingleClassNameMatcher byClassName(String className) {
        return new SingleClassNameMatcher(className);
    }
}

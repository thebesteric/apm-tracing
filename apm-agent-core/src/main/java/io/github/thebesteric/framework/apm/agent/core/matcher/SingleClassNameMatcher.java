package io.github.thebesteric.framework.apm.agent.core.matcher;

import lombok.Getter;
import net.bytebuddy.description.type.TypeDescription;

/**
 * 类名匹配器：仅适用于 ElementMatchers.named(xxx) 的情况
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-19 00:30:16
 */
@Getter
public class SingleClassNameMatcher implements NameMatch {

    // 类的全限定名
    private final String needMatchClassName;

    private SingleClassNameMatcher(String needMatchClassName) {
        if (needMatchClassName == null || needMatchClassName.isEmpty()) {
            throw new IllegalArgumentException("Need match class name cannot be null");
        }
        this.needMatchClassName = needMatchClassName;
    }

    public static SingleClassNameMatcher byClassName(String needMatchClassName) {
        return new SingleClassNameMatcher(needMatchClassName);
    }

    @Override
    public boolean isMatch(TypeDescription typeDescription) {
        return needMatchClassName.equals(typeDescription.getTypeName());
    }

    @Override
    public String getClassName() {
        return this.needMatchClassName;
    }
}

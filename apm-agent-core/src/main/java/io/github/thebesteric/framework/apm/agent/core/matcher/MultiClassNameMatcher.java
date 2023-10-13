package io.github.thebesteric.framework.apm.agent.core.matcher;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.Arrays;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * 类名匹配器：同时匹配多个类名相等的情况
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-25 19:14:42
 */
public class MultiClassNameMatcher implements IndirectMatcher {

    private final List<String> classNames;

    private MultiClassNameMatcher(String[] classNames) {
        if (classNames == null) {
            throw new IllegalArgumentException("class names cannot be null");
        }
        this.classNames = Arrays.asList(classNames);
    }

    /**
     * 要求是 or 的关系
     * 比如：named("xxx").or(named("yyy"))
     *
     * @return Junction<TypeDescription>
     * @author wangweijun
     * @since 2023/9/19 11:39
     */
    @Override
    public ElementMatcher.Junction<? super TypeDescription> buildJunction() {
        ElementMatcher.Junction<TypeDescription> junction = null;
        for (String className : classNames) {
            if (junction == null) {
                junction = named(className);
            } else {
                junction = junction.or(named(className));
            }
        }
        return junction;
    }

    @Override
    public boolean isMatch(TypeDescription typeDescription) {
        return classNames.contains(typeDescription.getTypeName());
    }

    public static IndirectMatcher byClassNames(String... classNames) {
        return new MultiClassNameMatcher(classNames);
    }
}
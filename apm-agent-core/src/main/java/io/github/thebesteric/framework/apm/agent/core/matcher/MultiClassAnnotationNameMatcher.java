package io.github.thebesteric.framework.apm.agent.core.matcher;

import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * 类注解匹配器：某个类需要同时含有多个注解匹配器
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-19 11:17:11
 */
public class MultiClassAnnotationNameMatcher implements IndirectMatcher {

    private final List<String> needMatchAnnotationNames;

    private MultiClassAnnotationNameMatcher(String[] needMatchAnnotationNames) {
        if (needMatchAnnotationNames == null) {
            throw new IllegalArgumentException("Need match annotation names cannot be null");
        }
        this.needMatchAnnotationNames = Arrays.asList(needMatchAnnotationNames);
    }

    /**
     * 多个注解要求是 and 的关系
     * 比如：named("xxx").and(named("yyy"))
     *
     * @return Junction<TypeDescription>
     * @author wangweijun
     * @since 2023/9/19 11:39
     */
    @Override
    public ElementMatcher.Junction<? super TypeDescription> buildJunction() {
        ElementMatcher.Junction<TypeDescription> junction = null;
        for (String annotationName : needMatchAnnotationNames) {
            if (junction == null) {
                junction = isAnnotatedWith(named(annotationName));
            } else {
                junction = junction.and(isAnnotatedWith(named(annotationName)));
            }
        }
        return junction;
    }

    /**
     * 是否匹配
     * 如果：annotationNames["@Anno1", "@Anno2"]
     * typeDescription["@Anno1", "@Anno2", "@Anno3"]: 匹配
     * typeDescription["@Anno1", "@Anno3"]: 不匹配
     *
     * @param typeDescription 类
     * @return boolean annotationNames 是 typeDescription 上注解的子集
     * @author wangweijun
     * @since 2023/9/26 11:27
     */
    @Override
    public boolean isMatch(TypeDescription typeDescription) {
        List<String> annotationList = new ArrayList<>(needMatchAnnotationNames);
        // 获取类上的注解
        AnnotationList declaredAnnotations = typeDescription.getDeclaredAnnotations();
        for (AnnotationDescription declaredAnnotation : declaredAnnotations) {
            String actualName = declaredAnnotation.getAnnotationType().getActualName();
            annotationList.remove(actualName);
        }
        return annotationList.isEmpty();
    }

    public static IndirectMatcher byClassAnnotationNames(String... needMatchAnnotationNames) {
        return new MultiClassAnnotationNameMatcher(needMatchAnnotationNames);
    }
}

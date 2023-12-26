package io.github.thebesteric.framework.apm.agent.core.matcher;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * 所有非 NameMatch 的情况，都要实现 IndirectMatcher
 *
 * @author wangweijun
 * @since 2023/9/19 00:32
 */
public interface IndirectMatcher extends ClassMatcher {

    /**
     * 构造 type() 的参数
     *
     * @return ElementMatcher<TypeDescription>
     * @author wangweijun
     * @since 2023/9/19 00:36
     */
    ElementMatcher.Junction<? super TypeDescription> buildJunction();
}

package io.github.thebesteric.framework.apm.agent.core.matcher;

import net.bytebuddy.description.type.TypeDescription;

/**
 * 表示要匹配哪些类的最顶层接口
 * 1. NameMatch：类名匹配
 * 2. IndirectMatch：间接匹配
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-19 00:05:34
 */
public interface ClassMatcher {

    /**
     * 是否匹配类名
     *
     * @param typeDescription 需要判断的类
     * @return boolean
     * @author wangweijun
     * @since 2023/9/19 16:49
     */
    boolean isMatch(TypeDescription typeDescription);

}

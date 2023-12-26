package io.github.thebesteric.framework.apm.agent.core.matcher;

/**
 * 类名匹配器：只适用于匹配一个类名的情况
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023/12/26 00:16        
 */
public interface NameMatch extends ClassMatcher {

    /**
     * 获取匹配的类名
     *
     * @return String
     * @author wangweijun
     * @since 2023/12/26 10:33
     */
    String getClassName();
}

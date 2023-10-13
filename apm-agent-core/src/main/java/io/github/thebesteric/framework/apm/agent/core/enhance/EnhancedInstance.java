package io.github.thebesteric.framework.apm.agent.core.enhance;

/**
 * 所有需要增强构造或实例方法的字节码都会实现这个接口
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-21 01:01:33
 */
public interface EnhancedInstance {

    /**
     * 获取扩展的动态字段
     */
    Object getExtendDynamicField();

    /**
     * 设置扩展的动态字段
     */
    void setExtendDynamicField(Object obj);

}

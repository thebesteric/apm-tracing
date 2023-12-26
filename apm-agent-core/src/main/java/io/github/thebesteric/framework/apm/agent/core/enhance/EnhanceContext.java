package io.github.thebesteric.framework.apm.agent.core.enhance;

import lombok.Getter;

/**
 * 处理一个类的上下文状态
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-21 01:06:50
 */
@Getter
public class EnhanceContext {

    // 是否被增强了
    private boolean enhanced = false;

    // 是否扩展了自定义属性 CONTEXT_ATTR_FIELD
    private boolean objectExtended = false;

    // 初始化阶段完成
    public void initializationStageCompleted() {
        enhanced = true;
    }

    // 定义扩展字段完成
    public void objectExtendedCompleted() {
        objectExtended = true;
    }

}

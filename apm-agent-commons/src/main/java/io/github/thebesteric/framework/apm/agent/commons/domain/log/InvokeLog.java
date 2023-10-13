package io.github.thebesteric.framework.apm.agent.commons.domain.log;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * ExecuteLog
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-10-09 19:07:06
 */
@Getter
@Setter
@Slf4j
public class InvokeLog extends AbstractLog {

    // 扩展信息
    protected Object extra;

    public InvokeLog(String id, String parentId, String traceId) {
        super(id, parentId, traceId);
    }
}

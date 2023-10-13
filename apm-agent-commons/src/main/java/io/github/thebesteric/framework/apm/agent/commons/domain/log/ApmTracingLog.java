package io.github.thebesteric.framework.apm.agent.commons.domain.log;


import io.github.thebesteric.framework.apm.agent.commons.constant.ApmTracingConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 执行日志
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-27 18:06:48
 */
@Getter
@Setter
@Slf4j
public class ApmTracingLog extends HttpLog {

    // 标签
    protected String tag = ApmTracingConstants.DEFAULT_TAG;

    public ApmTracingLog(String id, String parentId, String traceId) {
        super(id, parentId, traceId);
    }
}

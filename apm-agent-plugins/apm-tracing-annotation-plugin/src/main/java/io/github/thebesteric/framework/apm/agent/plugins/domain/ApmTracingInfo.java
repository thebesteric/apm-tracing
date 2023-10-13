package io.github.thebesteric.framework.apm.agent.plugins.domain;

import io.github.thebesteric.framework.apm.agent.commons.constant.ApmTracingConstants;
import io.github.thebesteric.framework.apm.agent.commons.constant.Level;
import lombok.Data;

/**
 * ApmTracing 注解信息
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-10-08 15:46:17
 */
@Data
public class ApmTracingInfo {
    private String tag;
    private String extra;
    private Level level;

    public ApmTracingInfo(String tag, String extra, Level level) {
        this.tag = tag;
        this.extra = extra;
        this.level = level;
    }

    public ApmTracingInfo merge(ApmTracingInfo other) {
        if (!this.tag.equals(other.tag) && !ApmTracingConstants.DEFAULT_TAG.equals(other.tag)) {
            this.tag = other.tag;
        }
        if (!this.extra.equals(other.extra) && !ApmTracingConstants.DEFAULT_EXTRA.equals(other.extra)) {
            this.extra = other.extra;
        }
        if (this.level != other.level && Level.INFO != other.level) {
            this.level = other.level;
        }
        return this;
    }
}

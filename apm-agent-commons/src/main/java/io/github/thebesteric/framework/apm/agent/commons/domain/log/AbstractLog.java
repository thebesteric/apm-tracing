package io.github.thebesteric.framework.apm.agent.commons.domain.log;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.thebesteric.framework.apm.agent.commons.IdGenerator;
import io.github.thebesteric.framework.apm.agent.commons.LoggerPrinter;
import io.github.thebesteric.framework.apm.agent.commons.constant.Level;
import io.github.thebesteric.framework.apm.agent.commons.domain.ExecuteInfo;
import io.github.thebesteric.framework.apm.agent.commons.util.JsonUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * AbstractLog
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-10-10 16:38:20
 */
@Slf4j
@Getter
@Setter
public abstract class AbstractLog {

    // 日志 ID
    protected String id = IdGenerator.getInstance().generate();

    // 父日志 ID
    protected String parentId;

    // 日志链追踪 ID
    protected String traceId;

    // 日志级别
    protected Level level = Level.INFO;

    // 执行信息
    protected ExecuteInfo executeInfo;

    // 异常信息
    protected String exception;

    // 执行线程名称
    protected String threadName = Thread.currentThread().getName();

    // 创建时间
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date createdAt = new Date();

    protected AbstractLog(String id, String parentId, String traceId) {
        this.id = id;
        this.parentId = parentId;
        this.traceId = traceId;
    }

    public void setException(String exception) {
        if (StringUtils.isEmpty(exception.trim())) {
            this.level = Level.ERROR;
        }
    }

    protected String print() {
        try {
            return JsonUtils.mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            LoggerPrinter.warn(log, e);
        }
        return super.toString();
    }

    @Override
    public String toString() {
        return print();
    }
}

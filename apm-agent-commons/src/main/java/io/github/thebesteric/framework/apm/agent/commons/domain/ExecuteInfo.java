package io.github.thebesteric.framework.apm.agent.commons.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.thebesteric.framework.apm.agent.commons.DurationWatcher;
import io.github.thebesteric.framework.apm.agent.commons.util.ExceptionUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;

@Getter
@Setter
public class ExecuteInfo implements Serializable {

    // 所属类名
    private String className;
    // 方法信息
    private MethodInfo methodInfo;
    // 执行时间
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date executeTime;
    // 执行结果
    protected Object result;
    // 异常信息
    protected String exception;
    // 执行时长
    private long duration;

    private ExecuteInfo() {
        this.executeTime = new Date();
    }

    private ExecuteInfo(String className, MethodInfo methodInfo) {
        this();
        this.className = className;
        this.methodInfo = methodInfo;
    }

    private ExecuteInfo(Method method, Object[] args) {
        this(method.getDeclaringClass().getName(), new MethodInfo(method, args));
    }

    private ExecuteInfo(Method method, Object[] args, DurationWatcher.DurationInfo durationInfo) {
        this(method, args);
        if (durationInfo != null) {
            this.executeTime = new Date(durationInfo.getStartTime());
            this.duration = durationInfo.getDuration();
        }
    }

    public static Builder builder(Method method, Object[] args, DurationWatcher.DurationInfo durationInfo) {
        return new Builder(new ExecuteInfo(method, args, durationInfo));
    }

    public static class Builder {
        private final ExecuteInfo executeInfo;

        public Builder(ExecuteInfo executeInfo) {
            this.executeInfo = executeInfo;
        }

        public Builder result(Object result) {
            this.executeInfo.result = result;
            return this;
        }

        public Builder exception(Throwable throwable) {
            this.executeInfo.exception = ExceptionUtils.getSimpleMessage(throwable);
            return this;
        }

        public ExecuteInfo build() {
            return this.executeInfo;
        }
    }
}
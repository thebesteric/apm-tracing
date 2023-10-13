package io.github.thebesteric.framework.apm.agent.commons;

import io.github.thebesteric.framework.apm.agent.commons.exception.PrivateConstructorException;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 执行时间计算工具类
 *
 * @author Eric Joe
 * @version 1.0
 * @date 2022-07-25 15:23
 * @since 1.0
 */
public class DurationWatcher {

    private DurationWatcher() {
        throw new PrivateConstructorException();
    }

    private static final ThreadLocal<Map<String, DurationInfo>> DURATION_THREAD_LOCAL = new InheritableThreadLocal<>();

    public static synchronized String start() {
        return start(UUID.randomUUID().toString());
    }

    public static synchronized String start(String tag) {
        Map<String, DurationInfo> durations = DURATION_THREAD_LOCAL.get();
        if (durations == null) {
            durations = new HashMap<>();
        }
        DurationInfo durationInfo = DurationInfo.builder().tag(tag).startTime(System.currentTimeMillis()).build();
        durations.put(tag, durationInfo);
        DURATION_THREAD_LOCAL.set(durations);
        return tag;
    }

    public static DurationInfo stop(String tag) {
        Map<String, DurationInfo> durationInfos = DURATION_THREAD_LOCAL.get();
        DurationInfo durationInfo = durationInfos.get(tag);
        durationInfo.setEndTime(System.currentTimeMillis());
        remove(tag);
        return durationInfo;
    }

    public static void remove(String tag) {
        Map<String, DurationInfo> durationInfos = DURATION_THREAD_LOCAL.get();
        durationInfos.remove(tag);
    }

    public static DurationInfo get(String tag) {
        Map<String, DurationInfo> durationInfos = DURATION_THREAD_LOCAL.get();
        return durationInfos.get(tag);
    }

    public static void clear() {
        DURATION_THREAD_LOCAL.remove();
    }

    @Data
    public static class DurationInfo {

        private Thread thread;
        private String tag;
        private long startTime;
        private long endTime;
        private long duration;

        private DurationInfo() {
            super();
        }

        public static Builder builder() {
            return new Builder(new DurationInfo());
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
            this.duration = this.endTime - this.startTime;
        }

        public Date getStartTimeToDate() {
            return new Date(this.startTime);
        }

        public static class Builder {

            private final DurationInfo durationInfo;

            public Builder(DurationInfo durationInfo) {
                this.durationInfo = durationInfo;
                this.durationInfo.thread = Thread.currentThread();
            }

            public DurationInfo build() {
                return this.durationInfo;
            }

            public Builder tag(String tag) {
                this.durationInfo.tag = tag;
                return this;
            }

            public Builder startTime(long startTime) {
                this.durationInfo.startTime = startTime;
                return this;
            }

            public Builder endTime(long endTime) {
                this.durationInfo.endTime = endTime;
                this.durationInfo.duration = this.durationInfo.endTime - this.durationInfo.startTime;
                return this;
            }
        }
    }

}

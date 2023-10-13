package io.github.thebesteric.framework.apm.agent.core.interceptor.support;

import io.github.thebesteric.framework.apm.agent.commons.DurationWatcher;
import io.github.thebesteric.framework.apm.agent.commons.domain.ExecuteInfo;
import io.github.thebesteric.framework.apm.agent.commons.domain.log.AbstractLog;
import io.github.thebesteric.framework.apm.agent.commons.domain.log.ExtendDynamicLogField;
import io.github.thebesteric.framework.apm.agent.commons.util.ExceptionUtils;
import io.github.thebesteric.framework.apm.agent.core.enhance.EnhancedInstance;

import java.lang.reflect.Method;

public interface InstanceMethodLogSupport {

    /**
     * 日志封装
     *
     * @param instance 对象实例
     * @param method   执行方法
     * @param args     执行参数
     * @param result   返回结果
     * @return AbstractLog
     * @author wangweijun
     * @since 2023/10/10 17:05
     */
    default AbstractLog packageLog(EnhancedInstance instance, Method method, Object[] args, Object result) {
        return packageLog(instance, method, args, result, null);
    }

    /**
     * 日志封装
     *
     * @param instance  对象实例
     * @param method    执行方法
     * @param args      执行参数
     * @param result    返回结果
     * @param throwable 异常结果
     * @return AbstractLog
     * @author wangweijun
     * @since 2023/10/10 17:05
     */
    default AbstractLog packageLog(EnhancedInstance instance, Method method, Object[] args, Object result, Throwable throwable) {
        // 获取扩展字段
        ExtendDynamicLogField extendDynamicLogField = (ExtendDynamicLogField) instance.getExtendDynamicField();
        DurationWatcher.DurationInfo durationInfo = DurationWatcher.stop(extendDynamicLogField.getWatcherTag());

        // 封装日志
        AbstractLog abstractLog = extendDynamicLogField.getLog();
        ExecuteInfo executeInfo = ExecuteInfo.builder(method, args, durationInfo).result(result).build();
        abstractLog.setExecuteInfo(executeInfo);

        if (throwable != null) {
            abstractLog.setException(ExceptionUtils.getSimpleMessage(throwable));
        }

        return abstractLog;
    }

}

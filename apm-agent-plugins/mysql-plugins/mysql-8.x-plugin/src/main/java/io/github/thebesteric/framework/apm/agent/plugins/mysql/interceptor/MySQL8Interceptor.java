package io.github.thebesteric.framework.apm.agent.plugins.mysql.interceptor;

import io.github.thebesteric.framework.apm.agent.commons.DurationWatcher;
import io.github.thebesteric.framework.apm.agent.commons.LoggerPrinter;
import io.github.thebesteric.framework.apm.agent.commons.constant.Level;
import io.github.thebesteric.framework.apm.agent.commons.domain.log.ExtendDynamicLogField;
import io.github.thebesteric.framework.apm.agent.commons.domain.log.InvokeLog;
import io.github.thebesteric.framework.apm.agent.core.enhance.EnhancedInstance;
import io.github.thebesteric.framework.apm.agent.commons.IdentifierContext;
import io.github.thebesteric.framework.apm.agent.core.interceptor.InstanceMethodsAroundInterceptor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * MySQL 8.x 拦截器
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-25 17:37:17
 */
@Slf4j
public class MySQL8Interceptor implements InstanceMethodsAroundInterceptor {
    @Override
    public void beforeMethod(EnhancedInstance instance, Method method, Object[] args, Class<?>[] argTypes) {
        String watcherTag = DurationWatcher.start();
        String id = IdentifierContext.getMethodIdentifier(method);
        String parentId = IdentifierContext.getParentMethodIdentifier(method);
        String traceId = IdentifierContext.getTraceIdentifier();
        String sql = extractSql(instance, method);

        InvokeLog invokeLog = new InvokeLog(id, parentId, traceId);
        invokeLog.setExtra(sql);

        instance.setExtendDynamicField(new ExtendDynamicLogField(watcherTag, invokeLog));
    }

    @Override
    public Object afterMethod(EnhancedInstance instance, Method method, Object[] args, Class<?>[] argTypes, Object result) {
        InvokeLog invokeLog = (InvokeLog) packageLog(instance, method, args, result);
        LoggerPrinter.print(log, Level.INFO, invokeLog);
        return result;
    }

    @Override
    public void handleException(EnhancedInstance instance, Method method, Object[] args, Class<?>[] argTypes, Object result, Throwable throwable) {
        InvokeLog invokeLog = (InvokeLog) packageLog(instance, method, args, result, throwable);
        LoggerPrinter.print(log, invokeLog.getLevel(), invokeLog, throwable);
    }

    private String extractSql(EnhancedInstance instance, Method method) {
        String sql = null;
        try {
            Field query = method.getDeclaringClass().getSuperclass().getDeclaredField("query");
            query.setAccessible(true);
            Object obj = query.get(instance);
            Method asSql = obj.getClass().getMethod("asSql");
            asSql.setAccessible(true);
            sql = (String) asSql.invoke(obj);
            sql = sql.replace("\n", " ").replaceAll("( +)"," ").trim();
        } catch (Exception ex) {
            LoggerPrinter.error(log, "Extract SQL error", ex);
        }
        return sql;
    }

}

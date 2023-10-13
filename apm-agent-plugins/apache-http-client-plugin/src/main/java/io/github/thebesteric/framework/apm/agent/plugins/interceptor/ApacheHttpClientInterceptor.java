package io.github.thebesteric.framework.apm.agent.plugins.interceptor;

import io.github.thebesteric.framework.apm.agent.commons.DurationWatcher;
import io.github.thebesteric.framework.apm.agent.commons.LoggerPrinter;
import io.github.thebesteric.framework.apm.agent.commons.constant.Level;
import io.github.thebesteric.framework.apm.agent.commons.domain.ApmHttpServletRequest;
import io.github.thebesteric.framework.apm.agent.commons.domain.ApmHttpServletResponse;
import io.github.thebesteric.framework.apm.agent.commons.domain.HttpRequest;
import io.github.thebesteric.framework.apm.agent.commons.domain.HttpResponse;
import io.github.thebesteric.framework.apm.agent.commons.domain.log.ExtendDynamicLogField;
import io.github.thebesteric.framework.apm.agent.commons.domain.log.HttpLog;
import io.github.thebesteric.framework.apm.agent.commons.util.WebUtils;
import io.github.thebesteric.framework.apm.agent.core.enhance.EnhancedInstance;
import io.github.thebesteric.framework.apm.agent.commons.IdentifierContext;
import io.github.thebesteric.framework.apm.agent.core.interceptor.InstanceMethodsInterceptor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * ApacheHttpClientInterceptor
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-10-11 11:55:34
 */
@Slf4j
public class ApacheHttpClientInterceptor implements InstanceMethodsInterceptor {
    @Override
    public void beforeMethod(EnhancedInstance instance, Method method, Object[] args, Class<?>[] argTypes) {
        String watcherTag = DurationWatcher.start();
        String id = IdentifierContext.getMethodIdentifier(method);
        String parentId = IdentifierContext.getParentMethodIdentifier(method);
        String traceId = IdentifierContext.getTraceIdentifier();

        HttpLog httpLog = new HttpLog(id, parentId, traceId);

        ApmHttpServletRequest apmHttpServletRequest = WebUtils.getApmHttpServletRequest();
        if (apmHttpServletRequest != null) {
            httpLog.setRequest(new HttpRequest(apmHttpServletRequest));
        }

        instance.setExtendDynamicField(new ExtendDynamicLogField(watcherTag, httpLog));
    }

    @Override
    public Object afterMethod(EnhancedInstance instance, Method method, Object[] args, Class<?>[] argTypes, Object result) {
        HttpLog httpLog = (HttpLog) packageLog(instance, method, args, result);

        ApmHttpServletResponse apmHttpServletResponse = WebUtils.getApmHttpServletResponse();
        if (apmHttpServletResponse != null) {
            httpLog.setResponse(new HttpResponse(apmHttpServletResponse));
        }

        LoggerPrinter.print(log, Level.INFO, httpLog);
        return result;
    }

    @Override
    public void handleException(EnhancedInstance instance, Method method, Object[] args, Class<?>[] argTypes, Object result, Throwable throwable) {
        HttpLog httpLog = (HttpLog) packageLog(instance, method, args, result, throwable);
        LoggerPrinter.print(log, httpLog.getLevel(), httpLog, throwable);
    }
}

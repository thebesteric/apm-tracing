package io.github.thebesteric.framework.apm.agent.plugins.interceptor;

import io.github.thebesteric.framework.apm.agent.commons.DurationWatcher;
import io.github.thebesteric.framework.apm.agent.commons.LoggerPrinter;
import io.github.thebesteric.framework.apm.agent.commons.constant.Level;
import io.github.thebesteric.framework.apm.agent.commons.domain.*;
import io.github.thebesteric.framework.apm.agent.commons.domain.log.ApmTracingLog;
import io.github.thebesteric.framework.apm.agent.commons.domain.log.ExtendDynamicLogField;
import io.github.thebesteric.framework.apm.agent.commons.util.WebUtils;
import io.github.thebesteric.framework.apm.agent.core.enhance.EnhancedInstance;
import io.github.thebesteric.framework.apm.agent.commons.IdentifierContext;
import io.github.thebesteric.framework.apm.agent.core.interceptor.InstanceMethodsAroundInterceptor;
import io.github.thebesteric.framework.apm.agent.plugins.domain.ApmTracingInfo;
import io.github.thebesteric.framework.apm.agent.plugins.parser.AmpTracingAnnotationParser;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ApmTracingAnnotationInterceptor
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-28 11:22:45
 */
@Slf4j
public class ApmTracingAnnotationInterceptor implements InstanceMethodsAroundInterceptor {

    private static final String CONTROLLER_NAME = "org.springframework.stereotype.Controller";
    private static final String REST_CONTROLLER_NAME = "org.springframework.web.bind.annotation.RestController";
    private final Map<Class<?>, Boolean> controllerClassCache = new HashMap<>();
    private final Map<Method, ApmTracingInfo> apmTracingInfoCache = new HashMap<>();

    @Override
    public void beforeMethod(EnhancedInstance instance, Method method, Object[] args, Class<?>[] argTypes) {
        String watcherTag = DurationWatcher.start();
        String id = IdentifierContext.getMethodIdentifier(method);
        String parentId = IdentifierContext.getParentMethodIdentifier(method);
        String traceId = IdentifierContext.getTraceIdentifier();

        ApmTracingLog apmTracingLog = new ApmTracingLog(id, parentId, traceId);

        // 解析 @ApmTracing 注解
        ApmTracingInfo apmTracingInfo = apmTracingInfoCache.get(method);
        synchronized (this) {
            if (apmTracingInfo == null) {
                AmpTracingAnnotationParser parser = new AmpTracingAnnotationParser(method);
                apmTracingInfo = parser.parse();
                apmTracingInfoCache.put(method, apmTracingInfo);
            }
        }

        apmTracingLog.setTag(apmTracingInfo.getTag());
        apmTracingLog.setExtra(apmTracingInfo.getExtra());
        apmTracingLog.setLevel(apmTracingInfo.getLevel());

        // Web Request
        if (isSpringControllerClass(method.getDeclaringClass())) {
            ApmHttpServletRequest apmHttpServletRequest = WebUtils.getApmHttpServletRequest();
            if (apmHttpServletRequest != null) {
                List<WebUtils.WebPair> requestHeaders = WebUtils.getRequestHeaders(apmHttpServletRequest);
                WebUtils.WebPair traceHeader = requestHeaders.stream().filter(header -> IdentifierContext.TRACK_ID_NAMES.contains(header.getKey()))
                        .findFirst().orElse(null);
                if (traceHeader != null) {
                    apmTracingLog.setTraceId(traceHeader.getValue());
                    IdentifierContext.setTraceIdentifier(traceHeader.getValue());
                }
                apmTracingLog.setRequest(new HttpRequest(apmHttpServletRequest));
            }
        }
        instance.setExtendDynamicField(new ExtendDynamicLogField(watcherTag, apmTracingLog));
    }

    @Override
    public Object afterMethod(EnhancedInstance instance, Method method, Object[] args, Class<?>[] argTypes, Object result) {
        ApmTracingLog apmTracingLog = (ApmTracingLog) packageLog(instance, method, args, result);

        // Web Request
        if (isSpringControllerClass(method.getDeclaringClass())) {
            ApmHttpServletResponse apmHttpServletResponse = WebUtils.getApmHttpServletResponse();
            if (apmHttpServletResponse != null) {
                apmTracingLog.setResponse(new HttpResponse(apmHttpServletResponse));
            }
        }

        LoggerPrinter.print(log, apmTracingLog.getLevel(), apmTracingLog);
        return result;
    }

    @Override
    public void handleException(EnhancedInstance instance, Method method, Object[] args, Class<?>[] argTypes, Object result, Throwable throwable) {
        ApmTracingLog apmTracingLog = (ApmTracingLog) packageLog(instance, method, args, result, throwable);
        LoggerPrinter.print(log, Level.ERROR, apmTracingLog, throwable);
    }

    private boolean isSpringControllerClass(Class<?> clazz) {
        Boolean isSpringControllerClass = controllerClassCache.get(clazz);
        if (isSpringControllerClass != null) {
            return isSpringControllerClass;
        }
        for (Annotation annotation : clazz.getDeclaredAnnotations()) {
            String annotationClassName = annotation.annotationType().getName();
            if (CONTROLLER_NAME.equals(annotationClassName) || REST_CONTROLLER_NAME.equals(annotationClassName)) {
                controllerClassCache.put(clazz, true);
                return true;
            }
        }
        controllerClassCache.put(clazz, false);
        return false;
    }
}

package io.github.thebesteric.framework.apm.agent.plugins.interceptor;

import io.github.thebesteric.framework.apm.agent.commons.DurationWatcher;
import io.github.thebesteric.framework.apm.agent.commons.IdentifierContext;
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
import io.github.thebesteric.framework.apm.agent.core.interceptor.InstanceMethodsAroundInterceptor;
import io.github.thebesteric.framework.apm.agent.plugins.domain.Extra;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * FeignInterceptor
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-10-11 19:32:19
 */
@Slf4j
public class FeignInterceptor implements InstanceMethodsAroundInterceptor {
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

        // 解析 Proxy 为 HardCodedTarget 并封装为 Extra
        Extra extra = proxyToExtra(args[0]);
        httpLog.setExtra(extra);

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

    /**
     * 解析 Proxy 为 HardCodedTarget 并封装为 Extra
     *
     * @param proxy proxy
     * @return Extra
     * @author wangweijun
     * @since 2023/10/12 15:01
     */
    public Extra proxyToExtra(Object proxy) {
        try {
            // 获取 java.lang.reflect.InvocationHandler
            Field h = Proxy.class.getDeclaredField("h");
            h.setAccessible(true);
            Object o = h.get(proxy);

            // 获取 feign.Target.HardCodedTarget
            Field t = o.getClass().getDeclaredField("target").getDeclaringClass().getDeclaredField("target");
            t.setAccessible(true);
            Object hardCodedTarget = t.get(o);

            // 反射 feign.Target.HardCodedTarget 获取属性
            Method typeMethod = hardCodedTarget.getClass().getDeclaredMethod("type");
            Method nameMethod = hardCodedTarget.getClass().getDeclaredMethod("name");
            Method urlMethod = hardCodedTarget.getClass().getDeclaredMethod("url");
            Class<?> clazz = (Class<?>) typeMethod.invoke(hardCodedTarget);
            String name = (String) nameMethod.invoke(hardCodedTarget);
            String url = (String) urlMethod.invoke(hardCodedTarget);

            return new Extra(clazz, name, url);
        } catch (Exception e) {
            LoggerPrinter.warn("Cannot parse {} to HardCodedTarget", proxy, e);
        }
        return null;
    }

}

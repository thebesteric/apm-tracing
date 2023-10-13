package io.github.thebesteric.framework.apm.agent.plugins.parser;

import io.github.thebesteric.framework.apm.agent.commons.LoggerPrinter;
import io.github.thebesteric.framework.apm.agent.commons.constant.Level;
import io.github.thebesteric.framework.apm.agent.plugins.ApmTracingAnnotationInstrument;
import io.github.thebesteric.framework.apm.agent.plugins.domain.ApmTracingInfo;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * AmpTracingAnnotationParser
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-10-08 16:02:36
 */
@Slf4j
public class AmpTracingAnnotationParser {
    Class<?> clazz;
    Method method;

    public AmpTracingAnnotationParser(Method method) {
        this.clazz = method.getDeclaringClass();
        this.method = method;
    }

    public ApmTracingInfo parse() {
        try {
            Annotation apmTraceAnnoOnClass = Arrays.stream(clazz.getAnnotations())
                    .filter(annotation -> ApmTracingAnnotationInstrument.AMP_TRACING_ANNOTATION_NAME.equals(annotation.annotationType().getName()))
                    .findFirst().orElse(null);
            assert apmTraceAnnoOnClass != null;
            ApmTracingInfo apmTracingInfoOnClazz = parseAnnotationOnMethod(apmTraceAnnoOnClass);

            Annotation apmTraceAnnoOnMethod = Arrays.stream(method.getDeclaredAnnotations())
                    .filter(annotation -> ApmTracingAnnotationInstrument.AMP_TRACING_ANNOTATION_NAME.equals(annotation.annotationType().getName()))
                    .findFirst().orElse(null);
            assert apmTraceAnnoOnMethod != null;
            ApmTracingInfo apmTracingInfoOnMethod = parseAnnotationOnMethod(apmTraceAnnoOnMethod);

            return apmTracingInfoOnClazz.merge(apmTracingInfoOnMethod);
        } catch (Exception ex) {
            LoggerPrinter.error(log, "Cannot parse @ApmTracing annotation on Method {}", method.getName(), ex);
        }
        return null;
    }

    private ApmTracingInfo parseAnnotationOnMethod(Annotation apmTraceAnno) throws Exception {
        Method tagMethod = apmTraceAnno.getClass().getMethod("tag");
        String tag = (String) tagMethod.invoke(apmTraceAnno);
        Method extraMethod = apmTraceAnno.getClass().getMethod("extra");
        String extra = (String) extraMethod.invoke(apmTraceAnno);
        Method levelMethod = apmTraceAnno.getClass().getMethod("level");
        Level level = (Level) levelMethod.invoke(apmTraceAnno);
        return new ApmTracingInfo(tag, extra, level);
    }

}

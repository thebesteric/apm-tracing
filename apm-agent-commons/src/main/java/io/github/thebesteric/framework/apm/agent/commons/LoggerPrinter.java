package io.github.thebesteric.framework.apm.agent.commons;

import io.github.thebesteric.framework.apm.agent.commons.constant.Level;
import io.github.thebesteric.framework.apm.agent.commons.exception.PrivateConstructorException;
import io.github.thebesteric.framework.apm.agent.commons.util.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * LoggerUtils
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-25 14:10:07
 */
@Slf4j
public class LoggerPrinter {

    private static final String THREAD_PREFIX = "APM-TRACING";
    private static final String LOG_PREFIX = "[" + THREAD_PREFIX + "] ";
    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            1, Runtime.getRuntime().availableProcessors() * 2 + 1, 5 * 60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1024),
            new BasicThreadFactory.Builder().namingPattern(THREAD_PREFIX + "-%d").daemon(true).build(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    private LoggerPrinter() {
        throw new PrivateConstructorException();
    }

    public interface Executor<T> {
        void execute(Object[] args, T t);
    }

    private static boolean handleException(Object[] args, Executor<Throwable> executor) {
        if (args != null && args.length > 0) {
            Object throwableObj = args[args.length - 1];
            if (throwableObj instanceof Throwable) {
                Throwable throwable = (Throwable) throwableObj;
                Object[] newArgs = Arrays.copyOfRange(args, 0, args.length - 1);
                executor.execute(newArgs, throwable);
                return true;
            }
        }
        return false;
    }

    public static void trace(String message, Object... args) {
        trace(log, message, args);
    }

    public static void trace(Logger log, Throwable throwable) {
        trace(log, ExceptionUtils.getSimpleMessage(throwable), throwable);
    }

    public static void trace(Logger log, String message, Object... args) {
        THREAD_POOL_EXECUTOR.execute(() -> {
            if (log.isTraceEnabled()) {
                if (handleException(args, (newArgs, throwable) -> log.trace(LOG_PREFIX + message, newArgs, throwable))) {
                    return;
                }
                log.trace(LOG_PREFIX + message, args);
            }
        });

    }

    public static void debug(String message, Object... args) {
        debug(log, message, args);
    }

    public static void debug(Logger log, Throwable throwable) {
        debug(log, ExceptionUtils.getSimpleMessage(throwable), throwable);
    }

    public static void debug(Logger log, String message, Object... args) {
        THREAD_POOL_EXECUTOR.execute(() -> {
            if (log.isDebugEnabled()) {
                if (handleException(args, (newArgs, throwable) -> log.debug(LOG_PREFIX + message, newArgs, throwable))) {
                    return;
                }
                log.debug(LOG_PREFIX + message, args);
            }
        });
    }

    public static void info(String message, Object... args) {
        info(log, message, args);
    }

    public static void info(Logger log, Throwable throwable) {
        info(log, ExceptionUtils.getSimpleMessage(throwable), throwable);
    }

    public static void info(Logger log, String message, Object... args) {
        THREAD_POOL_EXECUTOR.execute(() -> {
            if (log.isInfoEnabled()) {
                if (handleException(args, (newArgs, throwable) -> log.info(LOG_PREFIX + message, newArgs, throwable))) {
                    return;
                }
                log.info(LOG_PREFIX + message, args);
            }
        });
    }

    public static void warn(String message, Object... args) {
        warn(log, message, args);
    }

    public static void warn(Logger log, Throwable throwable) {
        warn(log, ExceptionUtils.getSimpleMessage(throwable), throwable);
    }

    public static void warn(Logger log, String message, Object... args) {
        THREAD_POOL_EXECUTOR.execute(() -> {
            if (log.isWarnEnabled()) {
                if (handleException(args, (newArgs, throwable) -> log.warn(LOG_PREFIX + message, newArgs, throwable))) {
                    return;
                }
                log.warn(LOG_PREFIX + message, args);
            }
        });
    }

    public static void error(String message, Object... args) {
        error(log, message, args);
    }

    public static void error(Logger log, Throwable throwable) {
        error(log, ExceptionUtils.getSimpleMessage(throwable), throwable);
    }

    public static void error(Logger log, String message, Object... args) {
        THREAD_POOL_EXECUTOR.execute(() -> {
            if (log.isErrorEnabled()) {
                if (handleException(args, (newArgs, throwable) -> log.error(LOG_PREFIX + message, newArgs, throwable))) {
                    return;
                }
                log.error(LOG_PREFIX + message, args);
            }
        });
    }

    public static void print(Logger log, Level level, Object object, Object... args) {
        switch (level) {
            case TRACE:
                trace(log, object.toString(), args);
                break;
            case DEBUG:
                debug(log, object.toString(), args);
                break;
            case WARN:
                warn(log, object.toString(), args);
                break;
            case ERROR:
                error(log, object.toString(), args);
                break;
            case INFO:
            default:
                info(log, object.toString(), args);
        }
    }


}

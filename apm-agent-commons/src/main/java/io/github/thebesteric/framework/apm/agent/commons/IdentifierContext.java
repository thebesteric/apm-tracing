package io.github.thebesteric.framework.apm.agent.commons;

import io.github.thebesteric.framework.apm.agent.commons.exception.PrivateConstructorException;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.*;

/**
 * MethodIdentifier
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-10-07 11:30:13
 */
@Data
public class IdentifierContext {

    private IdentifierContext() {
        throw new PrivateConstructorException();
    }

    public static final List<String> TRACK_ID_NAMES = Arrays.asList("trace-id", "x-trace-id", "track-id", "x-track-id", "request-id", "x-request-id");

    private static final ThreadLocal<List<Method>> METHOD_CHAINS = new InheritableThreadLocal<>() {
        @Override
        protected List<Method> initialValue() {
            return new ArrayList<>();
        }
    };

    // 方法的唯一标识符
    private static final ThreadLocal<Map<Method, String>> METHOD_IDENTIFIERS = new InheritableThreadLocal<>() {
        @Override
        protected Map<Method, String> initialValue() {
            return new HashMap<>();
        }
    };

    // 当前方法对于的父方法的标识符
    private static final ThreadLocal<Map<Method, String>> PARENT_METHOD_IDENTIFIERS = new InheritableThreadLocal<>() {
        @Override
        protected Map<Method, String> initialValue() {
            return new HashMap<>();
        }
    };

    // 首次建立的 methodId 对应的 traceId
    private static final ThreadLocal<String> METHOD_TRACE_IDENTIFIER = new InheritableThreadLocal<>();

    /**
     * 初始化方法的相关关键信息
     *
     * @param method 方法
     * @author wangweijun
     * @since 2023/10/11 16:33
     */
    public static synchronized void initialize(Method method) {
        addMethodChain(method);
        initialMethodIdentifier(method);
        initialMethodTraceIdentifier();
        Method parentMethod = IdentifierContext.getParentMethod();
        if (parentMethod != null) {
            initialParentMethodIdentifier(method);
        }
    }

    /**
     * 添加方法调用
     *
     * @param method 当前方法
     * @author wangweijun
     * @since 2023/10/11 16:37
     */
    private static void addMethodChain(Method method) {
        List<Method> chains = METHOD_CHAINS.get();
        chains.add(method);
    }

    /**
     * 获取父方法
     *
     * @return Method
     * @author wangweijun
     * @since 2023/10/11 16:36
     */
    private static Method getParentMethod() {
        List<Method> chains = METHOD_CHAINS.get();
        if (chains.size() <= 1) {
            return null;
        }
        return chains.get(chains.size() - 1);
    }


    /**
     * 初始化方法
     *
     * @param method 当前方法
     * @author wangweijun
     * @since 2023/10/11 16:36
     */
    private static void initialMethodIdentifier(Method method) {
        Map<Method, String> map = METHOD_IDENTIFIERS.get();
        map.put(method, IdGenerator.getInstance().generate());
    }

    /**
     * 初始化父方法
     *
     * @param method 当前方法
     * @author wangweijun
     * @since 2023/10/11 16:34
     */
    private static void initialParentMethodIdentifier(Method method) {
        Map<Method, String> parentMap = PARENT_METHOD_IDENTIFIERS.get();
        Method parentMethod = getParentMethod();
        // 创建父方法 ID
        String id = getMethodIdentifier(parentMethod);
        parentMap.put(method, id);
    }

    private static void initialMethodTraceIdentifier() {
        if (METHOD_TRACE_IDENTIFIER.get() == null) {
            METHOD_TRACE_IDENTIFIER.set(IdGenerator.getInstance().generate());
        }
    }

    /**
     * 获取当前方法标识
     *
     * @param method 当前方法
     * @return String
     * @author wangweijun
     * @since 2023/10/11 16:35
     */
    public static String getMethodIdentifier(Method method) {
        Map<Method, String> map = METHOD_IDENTIFIERS.get();
        return map.get(method);
    }

    /**
     * 获取当前方法的父方法标识
     *
     * @param method 当前方法
     * @return String
     * @author wangweijun
     * @since 2023/10/11 16:35
     */
    public static String getParentMethodIdentifier(Method method) {
        Map<Method, String> parentMap = PARENT_METHOD_IDENTIFIERS.get();
        return parentMap.get(method);
    }

    /**
     * 获取方法调用追踪标识
     *
     * @return String
     * @author wangweijun
     * @since 2023/10/11 16:55
     */
    public static String getTraceIdentifier() {
        return METHOD_TRACE_IDENTIFIER.get();
    }

    /**
     * 设置方法调用追踪标识
     *
     * @param traceIdentifier 追踪标识
     * @author wangweijun
     * @since 2023/10/11 16:55
     */
    public static void setTraceIdentifier(String traceIdentifier) {
        METHOD_TRACE_IDENTIFIER.set(traceIdentifier);
    }

    /**
     * 清理
     *
     * @author wangweijun
     * @since 2023/10/11 16:36
     */
    public static void remove() {
        List<Method> methods = METHOD_CHAINS.get();
        methods.remove(methods.size() - 1);
        if (methods.isEmpty()) {
            METHOD_IDENTIFIERS.remove();
            PARENT_METHOD_IDENTIFIERS.remove();
            METHOD_CHAINS.remove();
            METHOD_TRACE_IDENTIFIER.remove();
        }
    }
}

package io.github.thebesteric.framework.apm.agent.core.plugin;

import io.github.thebesteric.framework.apm.agent.commons.LoggerPrinter;
import io.github.thebesteric.framework.apm.agent.core.enhance.EnhanceContext;
import io.github.thebesteric.framework.apm.agent.core.matcher.ClassMatcher;
import io.github.thebesteric.framework.apm.agent.core.interceptor.point.ConstructorMethodsInterceptorPoint;
import io.github.thebesteric.framework.apm.agent.core.interceptor.point.InstanceMethodsInterceptorPoint;
import io.github.thebesteric.framework.apm.agent.core.interceptor.point.StaticMethodsInterceptorPoint;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;

/**
 * 类的增强插件定义类，是所有插件的顶级类
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-19 00:02:36
 */
@Slf4j
public abstract class AbstractClassEnhancePluginDefine {

    /**
     * 为匹配到的字节码，新增的扩展属性名称
     */
    public static final String CONTEXT_EXTEND_ATTR_NAME = "_$EnhancedClassExtendField$_";

    /**
     * 获取当前插件要增强的类
     *
     * @return ClassMatch
     * @author wangweijun
     * @since 2023/9/19 00:05
     */
    protected abstract ClassMatcher enhanceClass();

    /**
     * 实例方法的拦截点
     *
     * @return InstanceMethodsInterceptorPoint[]
     * @author wangweijun
     * @since 2023/9/19 00:10
     */
    protected abstract InstanceMethodsInterceptorPoint[] getInstanceMethodsInterceptorPoints();

    /**
     * 构造方法的拦截点
     *
     * @return ConstructorMethodsInterceptorPoint[]
     * @author wangweijun
     * @since 2023/9/19 00:16
     */
    protected abstract ConstructorMethodsInterceptorPoint[] getConstructorMethodsInterceptorPoints();

    /**
     * 静态方法的拦截点
     *
     * @return StaticMethodsInterceptorPoint[]
     * @author wangweijun
     * @since 2023/9/19 00:16
     */
    protected abstract StaticMethodsInterceptorPoint[] getStaticMethodsInterceptorPoints();

    /**
     * 增强类的主入口
     *
     * @param builder
     * @param typeDescription
     * @param classLoader
     * @param enhanceContext
     * @return Builder<?>
     * @author wangweijun
     * @since 2023/9/19 17:24
     */
    public DynamicType.Builder<?> define(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, EnhanceContext enhanceContext) {
        // 定义的插件类：com.example.agent.plugin.springmvc.RestControllerInstrumentation
        String pluginDefineName = this.getClass().getName();
        // 要被增强的类：xxx.xxx.controller.UserController
        String typeName = typeDescription.getTypeName();

        LoggerPrinter.debug(log, "Begin use {} to enhance {}", pluginDefineName, typeName);

        // 开发增强方法
        DynamicType.Builder<?> newBuilder = this.enhance(builder, typeDescription, classLoader, enhanceContext);

        // 设置增强处理完成
        enhanceContext.initializationStageCompleted();

        LoggerPrinter.debug(log, "End {} enhanced {}", pluginDefineName, typeName);

        return newBuilder;
    }

    private DynamicType.Builder<?> enhance(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, EnhanceContext enhanceContext) {
        // 增强静态方法
        builder = this.enhanceClass(builder, typeDescription, classLoader);
        // 增强实例方法和构造方法
        builder = this.enhanceInstance(builder, typeDescription, classLoader, enhanceContext);
        return builder;
    }

    /**
     * 增强静态方法
     *
     * @param builder         builder
     * @param typeDescription typeDescription
     * @param classLoader     classLoader
     * @return Builder<?>
     * @author wangweijun
     * @since 2023/9/20 00:36
     */
    protected abstract DynamicType.Builder<?> enhanceClass(DynamicType.Builder<?> builder,
                                                           TypeDescription typeDescription, ClassLoader classLoader);

    /**
     * 增强实例方法和构造方法
     *
     * @param builder         builder
     * @param typeDescription typeDescription
     * @param classLoader     classLoader
     * @param enhanceContext  enhanceContext
     * @return Builder<?>
     * @author wangweijun
     * @since 2023/9/20 00:37
     */
    protected abstract DynamicType.Builder<?> enhanceInstance(DynamicType.Builder<?> builder,
                                                              TypeDescription typeDescription, ClassLoader classLoader,
                                                              EnhanceContext enhanceContext);
}

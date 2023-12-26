package io.github.thebesteric.framework.apm.agent.core.enhance;


import io.github.thebesteric.framework.apm.agent.core.interceptor.ConstructorInterceptor;
import io.github.thebesteric.framework.apm.agent.core.interceptor.InstanceMethodsInterceptor;
import io.github.thebesteric.framework.apm.agent.core.interceptor.StaticMethodsInterceptor;
import io.github.thebesteric.framework.apm.agent.core.interceptor.point.ConstructorMethodsInterceptorPoint;
import io.github.thebesteric.framework.apm.agent.core.interceptor.point.InstanceMethodsInterceptorPoint;
import io.github.thebesteric.framework.apm.agent.core.interceptor.point.StaticMethodsInterceptorPoint;
import io.github.thebesteric.framework.apm.agent.core.plugin.AbstractClassEnhancePluginDefine;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.matcher.ElementMatcher;

import java.text.MessageFormat;

import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * 所有的插件都必须继承直接或间接继承此类，此类完成类 transform 中指定的 method 和 intercept
 * ex: DynamicType.Builder<?> builder = builder.method(xx).intercept(MethodDelegation.to(yy))
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-20 00:39:52
 */
public abstract class ClassEnhancePluginDefine extends AbstractClassEnhancePluginDefine {

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
    @Override
    protected DynamicType.Builder<?> enhanceClass(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader) {
        // 获取静态方法拦截点
        StaticMethodsInterceptorPoint[] staticMethodsInterceptorPoints = getStaticMethodsInterceptorPoints();
        if (staticMethodsInterceptorPoints == null || staticMethodsInterceptorPoints.length == 0) {
            return builder;
        }
        // 增强静态方法
        String typeName = typeDescription.getTypeName();
        for (StaticMethodsInterceptorPoint staticMethodsInterceptorPoint : staticMethodsInterceptorPoints) {
            String methodInterceptor = staticMethodsInterceptorPoint.getMethodInterceptor();
            if (methodInterceptor == null || methodInterceptor.isEmpty()) {
                // 抛出异常
                throwIllegalArgumentException(typeName);
            }
            ElementMatcher<MethodDescription> methodMatcher = staticMethodsInterceptorPoint.getMethodMatcher();
            builder = builder.method(isStatic().and(methodMatcher))
                    .intercept(MethodDelegation.withDefaultConfiguration()
                            .to(new StaticMethodsInterceptor(methodInterceptor, classLoader)));
        }
        return builder;
    }

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
    @Override
    protected DynamicType.Builder<?> enhanceInstance(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, EnhanceContext enhanceContext) {
        // 获取构造方法或者实例方法拦截点
        ConstructorMethodsInterceptorPoint[] constructorMethodsInterceptorPoints = getConstructorMethodsInterceptorPoints();
        InstanceMethodsInterceptorPoint[] instanceMethodsInterceptorPoints = getInstanceMethodsInterceptorPoints();

        // 判断是否需要增强
        boolean existConstructorMethodsInterceptorPoints = constructorMethodsInterceptorPoints != null && constructorMethodsInterceptorPoints.length > 0;
        boolean existInstanceMethodsInterceptorPoints = instanceMethodsInterceptorPoints != null && instanceMethodsInterceptorPoints.length > 0;
        if (!existConstructorMethodsInterceptorPoints && !existInstanceMethodsInterceptorPoints) {
            return builder;
        }

        String typeName = typeDescription.getTypeName();

        // 为字节码增加扩展属性，对于同一个类，只需要执行一次
        // typeDescription 不是 EnhancedInstance 的子类或实现类，并且没有设置扩展字段，就让 typeDescription 成为 EnhancedInstance 的子类
        if (!typeDescription.isAssignableTo(EnhancedInstance.class) && !enhanceContext.isObjectExtended()) {
            builder = builder.defineField(CONTEXT_EXTEND_ATTR_NAME, Object.class, Opcodes.ACC_PRIVATE | Opcodes.ACC_VOLATILE)
                    // 成为 EnhancedInstance 的实现类，并提供 getter 和 setter 方法
                    .implement(EnhancedInstance.class)
                    .intercept(FieldAccessor.ofField(CONTEXT_EXTEND_ATTR_NAME));
            // 设置为已添加扩展字段
            enhanceContext.objectExtendedCompleted();
        }


        // 增强构造方法
        if (existConstructorMethodsInterceptorPoints) {
            for (ConstructorMethodsInterceptorPoint constructorMethodsInterceptorPoint : constructorMethodsInterceptorPoints) {
                String constructorInterceptor = constructorMethodsInterceptorPoint.getConstructorInterceptor();
                if (constructorInterceptor == null || constructorInterceptor.isEmpty()) {
                    // 抛出异常
                    throwIllegalArgumentException(typeName);
                }
                ElementMatcher<MethodDescription> constructorMatcher = constructorMethodsInterceptorPoint.getConstructorMatcher();
                builder = builder.constructor(constructorMatcher)
                        // SuperMethodCall.INSTANCE.andThen 表示：在构造方法执行之后调用
                        .intercept(SuperMethodCall.INSTANCE.andThen(
                                MethodDelegation.withDefaultConfiguration()
                                        .to(new ConstructorInterceptor(constructorInterceptor, classLoader)))
                        );
            }
        }

        // 增强实例方法
        if (existInstanceMethodsInterceptorPoints) {
            for (InstanceMethodsInterceptorPoint instanceMethodsInterceptorPoint : instanceMethodsInterceptorPoints) {
                String methodInterceptor = instanceMethodsInterceptorPoint.getMethodInterceptor();
                if (methodInterceptor == null || methodInterceptor.isEmpty()) {
                    // 抛出异常
                    throwIllegalArgumentException(typeName);
                }
                ElementMatcher<MethodDescription> methodMatcher = instanceMethodsInterceptorPoint.getMethodMatcher();
                builder = builder.method(not(isStatic()).and(methodMatcher))
                        .intercept(MethodDelegation.withDefaultConfiguration()
                                .to(new InstanceMethodsInterceptor(methodInterceptor, classLoader)));
            }
        }


        return builder;
    }

    private void throwIllegalArgumentException(String typeName) {
        String message = MessageFormat.format("{0} no interceptors were provided", typeName);
        throw new IllegalArgumentException(message);
    }
}

package io.github.thebesteric.framework.apm.agent.plugins.mysql;

import io.github.thebesteric.framework.apm.agent.core.enhance.ClassEnhancePluginDefine;
import io.github.thebesteric.framework.apm.agent.core.matcher.ClassMatcher;
import io.github.thebesteric.framework.apm.agent.core.matcher.MultiClassNameMatcher;
import io.github.thebesteric.framework.apm.agent.core.interceptor.point.ConstructorMethodsInterceptorPoint;
import io.github.thebesteric.framework.apm.agent.core.interceptor.point.InstanceMethodsInterceptorPoint;
import io.github.thebesteric.framework.apm.agent.core.interceptor.point.StaticMethodsInterceptorPoint;
import io.github.thebesteric.framework.apm.agent.plugins.mysql.interceptor.MySQL5Interceptor;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * MySQL 8.x 插件
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-25 17:30:19
 */
public class MySQL5Instrumentation extends ClassEnhancePluginDefine {

    private static final String PREPARED_STATEMENT_CLASS_NAME = "com.mysql.jdbc.PreparedStatement";

    @Override
    protected ClassMatcher enhanceClass() {
        return MultiClassNameMatcher.byClassNames(PREPARED_STATEMENT_CLASS_NAME);
    }

    @Override
    protected InstanceMethodsInterceptorPoint[] getInstanceMethodsInterceptorPoints() {
        return new InstanceMethodsInterceptorPoint[] {
                new InstanceMethodsInterceptorPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodMatcher() {
                        return named("execute")
                                .or(named("executeQuery"))
                                .or(named("executeUpdate"))
                                .or(named("executeLargeUpdate"));
                    }

                    @Override
                    public String getMethodInterceptor() {
                        return MySQL5Interceptor.class.getName();
                    }
                }
        };
    }

    @Override
    protected ConstructorMethodsInterceptorPoint[] getConstructorMethodsInterceptorPoints() {
        return new ConstructorMethodsInterceptorPoint[0];
    }

    @Override
    protected StaticMethodsInterceptorPoint[] getStaticMethodsInterceptorPoints() {
        return new StaticMethodsInterceptorPoint[0];
    }
}

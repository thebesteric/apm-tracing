package io.github.thebesteric.framework.apm.agent.core.loader;

import io.github.thebesteric.framework.apm.agent.commons.exception.PrivateConstructorException;

/**
 * 用于加载插件中的拦截器
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-22 13:25:39
 */
public class InterceptorInstanceLoader {

    private InterceptorInstanceLoader() {
        throw new PrivateConstructorException();
    }

    /**
     * load
     *
     * @param interceptorName   插件中拦截器的全类名
     * @param targetClassLoader 目标对象的类加载器，要想在插件拦截器中能够访问到被拦截的类，需要是同一个类加载器或子类类加载器
     * @return T 拦截器实例：ConstructorInterceptor，InstanceMethodsInterceptor，StaticMethodsInterceptor
     * @author wangweijun
     * @since 2023/9/22 13:27
     */
    @SuppressWarnings("unchecked")
    public static <T> T load(String interceptorName, ClassLoader targetClassLoader) throws Exception {
        if (targetClassLoader == null) {
            targetClassLoader = InterceptorInstanceLoader.class.getClassLoader();
        }
        ApmAgentClassLoader apmAgentClassLoader = new ApmAgentClassLoader(targetClassLoader);
        Class<?> aClass = Class.forName(interceptorName, true, apmAgentClassLoader);
        Object obj = aClass.getDeclaredConstructor().newInstance();
        return (T) obj;
    }
}

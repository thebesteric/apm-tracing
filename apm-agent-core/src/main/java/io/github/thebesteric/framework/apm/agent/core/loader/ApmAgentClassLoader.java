package io.github.thebesteric.framework.apm.agent.core.loader;


import io.github.thebesteric.framework.apm.agent.commons.LoggerPrinter;
import io.github.thebesteric.framework.apm.agent.core.boot.AgentPackagePath;
import io.github.thebesteric.framework.apm.agent.core.plugin.PluginBootstrap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 用于加载插件和插件的拦截器
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-21 13:34:43
 */
@Slf4j
public class ApmAgentClassLoader extends ClassLoader {

    private static final String JAR_FILE_PREFIX = "jar:file:";

    // 用于加载插件的定义相关的类，如：MySQLInstrumentation（不包括插件的 interceptor）
    private static ApmAgentClassLoader defaultClassLoader;

    // 自定义加载器加载的路径
    private final List<File> classpath;

    // 所有的 Jar 文件
    private List<Jar> allJars;

    private final ReentrantLock jarScanLock = new ReentrantLock();

    public ApmAgentClassLoader(ClassLoader parent) {
        super(parent);
        // 获取 apm-agent.jar 的目录
        File agentJarDir = AgentPackagePath.getPath();
        // 获取到完整的 plugins 的目录
        classpath = new LinkedList<>();
        classpath.add(new File(agentJarDir, "plugins"));
    }

    public static ApmAgentClassLoader getDefault() {
        initDefaultLoader();
        return defaultClassLoader;
    }

    public static void initDefaultLoader() {
        if (defaultClassLoader == null) {
            defaultClassLoader = new ApmAgentClassLoader(PluginBootstrap.class.getClassLoader());
        }
    }

    /**
     * loadClass -> 调用：findClass -> defineClass
     *
     * @param name aa.cc.cc.Xxx
     * @return Class<?>
     * @author wangweijun
     * @since 2023/9/22 00:22
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        List<Jar> jars = getAllJars();
        String path = name.replace(".", "/").concat(".class");
        for (Jar jar : jars) {
            JarEntry jarEntry = jar.jarFile.getJarEntry(path);
            if (jarEntry == null) {
                continue;
            }
            try {
                URL url = new URL(JAR_FILE_PREFIX + jar.sourceFile.getAbsolutePath() + "!/" + path);
                byte[] bytes = IOUtils.toByteArray(url);
                return defineClass(name, bytes, 0, bytes.length);
            } catch (Exception e) {
                LoggerPrinter.error(log, "Find class {} error", name, e);
            }
        }
        throw new ClassNotFoundException("Cannot find class: " + path);
    }

    @Override
    public URL getResource(String name) {
        try {
            Enumeration<URL> resources = getResources(name);
            if (resources.hasMoreElements()) {
                return resources.nextElement();
            }
        } catch (IOException e) {
            LoggerPrinter.error(log, "Get resource {} error", name, e);
        }
        return null;
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        List<URL> resources = new ArrayList<>();
        List<Jar> jars = getAllJars();
        for (Jar jar : jars) {
            JarEntry jarEntry = jar.jarFile.getJarEntry(name);
            if (jarEntry != null) {
                URL url = new URL(JAR_FILE_PREFIX + jar.sourceFile.getAbsolutePath() + "!/" + name);
                resources.add(url);
            }
        }
        Iterator<URL> iterator = resources.iterator();
        return new Enumeration<>() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public URL nextElement() {
                return iterator.next();
            }
        };
    }

    private List<Jar> getAllJars() {
        if (allJars == null) {
            jarScanLock.lock();
            try {
                if (allJars == null) {
                    allJars = doGetAllJars();
                }
            } catch (Exception ex) {
                LoggerPrinter.error(log, "Find jar files failed", ex);
            } finally {
                jarScanLock.unlock();
            }
        }
        return allJars;
    }

    private List<Jar> doGetAllJars() {
        List<Jar> jarList = new ArrayList<>();
        for (File path : classpath) {
            if (path.exists() && path.isDirectory()) {
                String[] jarFileNames = path.list((dir, name) -> name.endsWith(".jar"));
                if (jarFileNames == null || ArrayUtils.isEmpty(jarFileNames)) {
                    continue;
                }
                for (String jarFileName : jarFileNames) {
                    try {
                        File jarSourceFile = new File(path, jarFileName);
                        Jar jar = new Jar(new JarFile(jarSourceFile), jarSourceFile);
                        jarList.add(jar);
                        LoggerPrinter.info(log, "Jar {} loaded", jarFileName);
                    } catch (IOException e) {
                        LoggerPrinter.error(log, "Jar {} load failed", jarFileName, e);
                    }
                }
            }
        }
        return jarList;
    }


    @RequiredArgsConstructor
    private static class Jar {
        // Jar 文件对于的 JarFile 对象
        private final JarFile jarFile;
        // Jar 文件对象
        private final File sourceFile;
    }
}

package io.github.thebesteric.framework.apm.agent.core.boot;

import io.github.thebesteric.framework.apm.agent.commons.exception.PrivateConstructorException;
import io.github.thebesteric.framework.apm.agent.commons.LoggerPrinter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;

/**
 * AgentPackagePath
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-25 16:07:02
 */
@Slf4j
public class AgentPackagePath {

    // apm-agent.jar 所在的目录
    private static File agentPackageFile;

    private AgentPackagePath() {
        throw new PrivateConstructorException();
    }

    public static File getPath() {
        if (agentPackageFile == null) {
            agentPackageFile = doGetPath();
        }
        return agentPackageFile;
    }

    private static File doGetPath() {
        // xx/xx/xx/AgentPackagePath
        String classResourcePath = AgentPackagePath.class.getName().replace(".", "/") + ".class";
        // resource = file:/IdeaProjects/apm-agent-dist/target/classes/xx/xx/xx/AgentPackagePath.class
        // resource = jar:file:/IdeaProjects/apm-agent-dist/apm-agent-1.0-SNAPSHOT-jar-with-dependencies.jar!/xx/xx/xx/AgentPackagePath.class
        URL resource = ClassLoader.getSystemClassLoader().getResource(classResourcePath);
        if (resource != null) {
            String location = resource.toString();
            LoggerPrinter.info(log, "The beacon class location is {}", location);
            boolean isInJar = location.indexOf('!') > -1;
            if (isInJar) {
                // /IdeaProjects/apm-agent-dist/apm-agent-1.0-SNAPSHOT-jar-with-dependencies.jar
                location = StringUtils.substringBetween(location, "file:", "!");
                File agentJarFile = null;
                try {
                    agentJarFile = new File(location);
                } catch (Exception ex) {
                    LoggerPrinter.error(log, "Cannot locate agent jar file by url: {}", location, ex);
                }
                if (agentJarFile != null && agentJarFile.exists()) {
                    // /IdeaProjects/apm-agent-dist
                    return agentJarFile.getParentFile();
                }
            }
        }
        throw new IllegalStateException("Cannot locate agent jar file");
    }

}

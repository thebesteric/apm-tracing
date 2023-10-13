package io.github.thebesteric.framework.apm.agent.core.plugin;

import io.github.thebesteric.framework.apm.agent.commons.LoggerPrinter;
import io.github.thebesteric.framework.apm.agent.core.loader.ApmAgentClassLoader;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * PluginResourceResolver
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-22 00:41:42
 */
@Slf4j
public class PluginResourceResolver {

    public static final String APM_TRACING_PLUGIN_DEF_FILE_NAME = "apm-tracing-plugin.def";

    /**
     * 获取插件目录 plugins 下所有 jar 文件内的 apm-tracing-plugin.def 文件的 URL
     *
     * @return List<URL>
     * @author wangweijun
     * @since 2023/9/22 00:43
     */
    public List<URL> getResources() {
        List<URL> cfgUrlPath = new ArrayList<>();
        ApmAgentClassLoader apmAgentClassLoader = ApmAgentClassLoader.getDefault();
        try {
            Enumeration<URL> resources = apmAgentClassLoader.getResources(APM_TRACING_PLUGIN_DEF_FILE_NAME);
            while (resources.hasMoreElements()) {
                URL pluginDefUrl = resources.nextElement();
                cfgUrlPath.add(pluginDefUrl);
                LoggerPrinter.info(log, "Find apm plugin define url: {}", pluginDefUrl);
            }
            return cfgUrlPath;
        } catch (IOException e) {
            LoggerPrinter.error(log, "Read resource error", e);
        }
        return Collections.emptyList();
    }
}

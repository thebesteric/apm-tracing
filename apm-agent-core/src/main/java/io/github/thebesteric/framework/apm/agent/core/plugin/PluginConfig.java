package io.github.thebesteric.framework.apm.agent.core.plugin;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * PluginConfig
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-22 11:14:23
 */
@Getter
public enum PluginConfig {

    INSTANCE;

    // 存放所有插件中 apm-plugin.def 构造出来的所有 PluginDefine 实例
    private final List<PluginDefine> pluginDefines = new ArrayList<>();

    /**
     * 转换 apm-plugin.def 为 PluginDefine
     *
     * @param in
     * @author wangweijun
     * @since 2023/9/22 11:28
     */
    void load(InputStream in) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String pluginDefineStr;
            while ((pluginDefineStr = reader.readLine()) != null) {
                if (pluginDefineStr.trim().isEmpty() || pluginDefineStr.startsWith("#")) {
                    continue;
                }
                PluginDefine pluginDefine = PluginDefine.build(pluginDefineStr);
                pluginDefines.add(pluginDefine);
            }
        }
    }

}

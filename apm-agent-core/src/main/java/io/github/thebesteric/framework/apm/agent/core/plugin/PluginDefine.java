package io.github.thebesteric.framework.apm.agent.core.plugin;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;

/**
 * PluginDefine
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-22 10:16:39
 */
@Data
public class PluginDefine {

    // 插件的名字
    private String name;

    // 插件定义的全类名
    private String defineClass;

    private PluginDefine(String name, String defineClass) {
        this.name = name;
        this.defineClass = defineClass;
    }

    public static PluginDefine build(String define) {
        if (StringUtils.isEmpty(define)) {
            String message = MessageFormat.format("{0} file must not be empty", PluginResourceResolver.APM_TRACING_PLUGIN_DEF_FILE_NAME);
            throw new IllegalArgumentException(message);
        }
        String[] pluginDefine = define.split("=");
        if (pluginDefine.length != 2) {
            String message = MessageFormat.format("{0} file must like this: name=class", PluginResourceResolver.APM_TRACING_PLUGIN_DEF_FILE_NAME);
            throw new IllegalArgumentException(message);
        }
        String defineName = pluginDefine[0];
        String defineClass = pluginDefine[1];
        return new PluginDefine(defineName, defineClass);
    }
}

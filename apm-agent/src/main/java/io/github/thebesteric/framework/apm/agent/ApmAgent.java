package io.github.thebesteric.framework.apm.agent;

import io.github.thebesteric.framework.apm.agent.core.plugin.PluginBootstrap;
import io.github.thebesteric.framework.apm.agent.core.plugin.PluginFinder;
import io.github.thebesteric.framework.apm.agent.commons.exception.PrivateConstructorException;
import io.github.thebesteric.framework.apm.agent.commons.LoggerPrinter;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.dynamic.scaffold.TypeValidation;

import java.lang.instrument.Instrumentation;

/**
 * 主入口
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-25 12:06:23
 */
@Slf4j
public class ApmAgent {

    private ApmAgent() {
        throw new PrivateConstructorException();
    }

    public static void premain(String args, Instrumentation instrumentation) {

        LoggerPrinter.info(log, "Enter premain，args: {}", args);

        PluginFinder pluginFinder;
        try {
            pluginFinder = new PluginFinder(new PluginBootstrap().loadPlugins());
        } catch (Exception e) {
            LoggerPrinter.error(log, "Initialization failure", e);
            return;
        }

        final ByteBuddy byteBuddy = new ByteBuddy().with(TypeValidation.ENABLED);

        AgentBuilder builder = new AgentBuilder.Default(byteBuddy);
        builder.type(pluginFinder.buildTypeMatch())
                .transform(new ApmTransformer(pluginFinder))
                .with(new ApmAgentListener())
                .installOn(instrumentation);
    }
}

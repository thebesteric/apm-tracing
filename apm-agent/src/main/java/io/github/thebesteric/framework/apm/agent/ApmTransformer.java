package io.github.thebesteric.framework.apm.agent;

import io.github.thebesteric.framework.apm.agent.commons.LoggerPrinter;
import io.github.thebesteric.framework.apm.agent.core.enhance.EnhanceContext;
import io.github.thebesteric.framework.apm.agent.core.plugin.AbstractClassEnhancePluginDefine;
import io.github.thebesteric.framework.apm.agent.core.plugin.PluginFinder;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;
import net.bytebuddy.utility.nullability.MaybeNull;

import java.security.ProtectionDomain;
import java.util.List;

/**
 * ApmTransformer
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-18 23:51:45
 */
@Slf4j
public class ApmTransformer implements AgentBuilder.Transformer {

    private final PluginFinder pluginFinder;

    public ApmTransformer(PluginFinder pluginFinder) {
        this.pluginFinder = pluginFinder;
    }

    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder,
                                            TypeDescription typeDescription,
                                            ClassLoader classLoader,
                                            JavaModule javaModule) {
        return transform(builder, typeDescription, classLoader, javaModule, null);
    }

    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder,
                                            TypeDescription typeDescription,
                                            ClassLoader classLoader,
                                            JavaModule javaModule,
                                            @MaybeNull ProtectionDomain protectionDomain) {

        String actualClassName = typeDescription.getActualName();
        LoggerPrinter.info(log, "ActualClassName to transform: {}", actualClassName);

        List<AbstractClassEnhancePluginDefine> pluginDefiners = pluginFinder.find(typeDescription);
        if (!pluginDefiners.isEmpty()) {
            DynamicType.Builder<?> newBuilder = builder;

            // 定义上下文，一个 enhanceContext 对应一个 typeDescription
            EnhanceContext enhanceContext = new EnhanceContext();

            for (AbstractClassEnhancePluginDefine pluginDefiner : pluginDefiners) {
                DynamicType.Builder<?> possibleBuilder = pluginDefiner.define(newBuilder, typeDescription, classLoader, enhanceContext);
                if (possibleBuilder != null) {
                    newBuilder = possibleBuilder;
                }
            }

            // 增强完成
            if (enhanceContext.isEnhanced()) {
                LoggerPrinter.debug(log, "Finished enhance for {}", typeDescription.getTypeName());
            }
            return newBuilder;
        }

        LoggerPrinter.warn(log, "Matched class: {}, but can not find plugin", actualClassName);
        return builder;
    }
}

package io.github.thebesteric.framework.apm.agent.core.plugin;


import io.github.thebesteric.framework.apm.agent.core.matcher.ClassMatcher;
import io.github.thebesteric.framework.apm.agent.core.matcher.IndirectMatcher;
import io.github.thebesteric.framework.apm.agent.core.matcher.SingleClassNameMatcher;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.bytebuddy.matcher.ElementMatchers.isInterface;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * PluginFinder
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-19 14:14:08
 */
public class PluginFinder {

    /*
    用于存放 ClassMatcher 类型为 SingleClassNameMatcher 匹配器匹配到的插件
    key: 全类名
    value: 匹配到插件
     */
    private final Map<String, List<AbstractClassEnhancePluginDefine>> nameMatchDefine = new HashMap<>();

    /*
    用于存放 ClassMatcher 类型为 IndirectMatcher 匹配器匹配到的插件
     */
    private final List<AbstractClassEnhancePluginDefine> signatureMatchDefine = new ArrayList<>();

    /**
     * PluginFinder
     *
     * @param plugins 加载到的所有插件
     * @author wangweijun
     * @since 2023/9/19 14:16
     */
    public PluginFinder(List<AbstractClassEnhancePluginDefine> plugins) {
        for (AbstractClassEnhancePluginDefine plugin : plugins) {
            ClassMatcher classMatcher = plugin.enhanceClass();
            if (classMatcher == null) {
                continue;
            }
            if (classMatcher instanceof SingleClassNameMatcher) {
                SingleClassNameMatcher singleClassNameMatcher = (SingleClassNameMatcher) classMatcher;
                List<AbstractClassEnhancePluginDefine> list = nameMatchDefine.computeIfAbsent(singleClassNameMatcher.getClassName(), k -> new ArrayList<>());
                list.add(plugin);
            } else {
                signatureMatchDefine.add(plugin);
            }
        }
    }

    /**
     * 返回已加载插件最终拼接后的条件
     *
     * @return plugin1_junction.or(plugin2_junction).or(plugin3_junction)
     * @author wangweijun
     * @since 2023/9/19 16:11
     */
    public ElementMatcher<? super TypeDescription> buildTypeMatch() {
        ElementMatcher.Junction<? super TypeDescription> junction = new ElementMatcher.Junction.AbstractBase<NamedElement>() {
            // 当某个类第一次被加载，都会回调这个方法
            @Override
            public boolean matches(NamedElement target) {
                return nameMatchDefine.containsKey(target.getActualName());
            }
        };

        for (AbstractClassEnhancePluginDefine pluginDefine : signatureMatchDefine) {
            ClassMatcher classMatcher = pluginDefine.enhanceClass();
            if (classMatcher instanceof IndirectMatcher) {
                IndirectMatcher indirectMatcher = (IndirectMatcher) classMatcher;
                junction = junction.or(indirectMatcher.buildJunction());
            }
        }

        // 排除接口，这一步一定要放在最后
        return junction.and(not(isInterface()));
    }

    /**
     * 查找插件
     *
     * @param typeDescription 匹配到的类
     * @return List<AbstractClassEnhancePluginDefiner> 对应的插件集合
     * @author wangweijun
     * @since 2023/9/19 16:42
     */
    public List<AbstractClassEnhancePluginDefine> find(TypeDescription typeDescription) {
        // 匹配到到插件
        List<AbstractClassEnhancePluginDefine> matchedPlugins = new ArrayList<>();

        // 获取到全类名
        String typeName = typeDescription.getTypeName();

        // 处理 nameMatchDefine
        if (nameMatchDefine.containsKey(typeName)) {
            matchedPlugins.addAll(nameMatchDefine.get(typeName));
        }

        // 处理 signatureMatchDefine
        for (AbstractClassEnhancePluginDefine pluginDefiner : signatureMatchDefine) {
            ClassMatcher classMatcher = pluginDefiner.enhanceClass();
            if (classMatcher instanceof IndirectMatcher) {
                IndirectMatcher indirectMatcher = (IndirectMatcher) classMatcher;
                if (indirectMatcher.isMatch(typeDescription)) {
                    matchedPlugins.add(pluginDefiner);
                }
            }
        }

        return matchedPlugins;
    }
}

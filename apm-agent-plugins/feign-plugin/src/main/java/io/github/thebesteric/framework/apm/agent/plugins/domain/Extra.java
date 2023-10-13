package io.github.thebesteric.framework.apm.agent.plugins.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Extra
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-10-12 14:38:53
 */
@Data
@AllArgsConstructor
public class Extra {
    private Class<?> clazz;
    private String name;
    private String url;
}

package io.github.thebesteric.framework.apm.agent.commons.domain.log;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ExtendDynamicField
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-28 17:56:19
 */
@Data
@AllArgsConstructor
public class ExtendDynamicLogField {
    private String watcherTag;
    private AbstractLog log;
}

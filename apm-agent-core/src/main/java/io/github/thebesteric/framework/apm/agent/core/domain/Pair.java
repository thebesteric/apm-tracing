package io.github.thebesteric.framework.apm.agent.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 键值对
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-10-11 16:25:44
 */
@Data
@AllArgsConstructor
public class Pair<K, V> {
    K key;
    V value;
}

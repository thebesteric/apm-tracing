package io.github.thebesteric.framework.apm.agent.commons.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * HttpResponse
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-10-07 14:23:30
 */
@Getter
@Setter
public class HttpResponse {
    // 响应状态
    private int status;
    // 内容类型
    private String contentType;
    // 语言
    private String locale;
    // 响应头
    private Map<String, String> headers;

    public HttpResponse(ApmHttpServletResponse response) {
        this.status = response.getStatus();
        this.contentType = response.getContentType();
        this.locale = response.getLocale().toString();
        this.headers = response.getHeaderNames().stream()
                .collect(Collectors.toMap((key) -> key, response::getHeader, (v1, v2) -> v2));
    }
}

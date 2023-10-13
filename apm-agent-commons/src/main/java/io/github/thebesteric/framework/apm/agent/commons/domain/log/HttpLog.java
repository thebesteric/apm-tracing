package io.github.thebesteric.framework.apm.agent.commons.domain.log;

import io.github.thebesteric.framework.apm.agent.commons.domain.HttpRequest;
import io.github.thebesteric.framework.apm.agent.commons.domain.HttpResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * HttpLog
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-10-10 16:11:34
 */
@Getter
@Setter
@Slf4j
public class HttpLog extends InvokeLog {

    // HTTP 请求信息
    protected HttpRequest request;

    // HTTP 响应信息
    protected HttpResponse response;

    public HttpLog(String id, String parentId, String traceId) {
        super(id, parentId, traceId);
    }
}

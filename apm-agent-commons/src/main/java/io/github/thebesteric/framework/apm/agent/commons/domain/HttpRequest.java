package io.github.thebesteric.framework.apm.agent.commons.domain;

import io.github.thebesteric.framework.apm.agent.commons.util.CurlUtils;
import io.github.thebesteric.framework.apm.agent.commons.util.WebUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * HttpRequest
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-10-07 14:23:13
 */
@Getter
@Setter
public class HttpRequest {
    // Session ID
    private String sessionId;
    // URI
    private String uri;
    // URL
    private String url;
    // 请求方法
    private String method;
    // 内容类型
    private String contentType;
    // 请求协议
    private String protocol;
    // IP
    private String ip;
    // 域信息
    private String domain;
    // 服务器名
    private String serverName;
    // 本地地址
    private String localAddr;
    // 本地端口
    private Integer localPort;
    // 远程地址
    private String remoteAddr;
    // 远程地址
    private Integer remotePort;
    // 请求参数
    private String query;
    // COOKIES
    private List<WebUtils.WebCookie> cookies;
    // 请求头信息
    private List<WebUtils.WebPair> headers;
    // 请求参数
    private List<WebUtils.WebPair> params;
    // 请求体
    private Object body;
    // 原生请求体
    private String rawBody;
    // CURL
    private String curl;

    public HttpRequest(ApmHttpServletRequest request) {
        this.sessionId = request.getSession().getId();
        this.uri = request.getRequestURI();
        this.url = request.getRequestURL().toString();
        this.method = request.getMethod().toUpperCase();
        this.contentType = request.getContentType();
        this.protocol = request.getProtocol();
        this.ip = WebUtils.getIpAddress(request);
        this.domain = WebUtils.getDomain(request);
        this.serverName = request.getServerName();
        this.localAddr = request.getLocalAddr();
        this.localPort = request.getLocalPort();
        this.remoteAddr = request.getRemoteAddr();
        this.remotePort = request.getRemotePort();
        this.query = WebUtils.getQueryString(request);
        this.cookies = WebUtils.getRequestCookies(request);
        this.headers = WebUtils.getRequestHeaders(request);
        this.params = WebUtils.getRequestParams(request);
        this.body = request.getBody();
        this.rawBody = request.getRawBody();
        this.curl = CurlUtils.curl(request);
    }


}

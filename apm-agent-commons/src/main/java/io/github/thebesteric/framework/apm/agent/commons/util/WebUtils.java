package io.github.thebesteric.framework.apm.agent.commons.util;

import io.github.thebesteric.framework.apm.agent.commons.domain.ApmHttpServletRequest;
import io.github.thebesteric.framework.apm.agent.commons.domain.ApmHttpServletResponse;
import io.github.thebesteric.framework.apm.agent.commons.exception.IllegalArgumentException;
import io.github.thebesteric.framework.apm.agent.commons.exception.PrivateConstructorException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * WebUtils
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-09-28 13:32:24
 */
@Slf4j
public class WebUtils {

    public static final String[] IP_HEADERS = {"x-forwarded-for", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]");

    private WebUtils() {
        throw new PrivateConstructorException();
    }

    public static ApmHttpServletRequest getApmHttpServletRequest() {
        ServletRequestAttributes attributes = getServletRequestAttributes();
        return attributes != null ? new ApmHttpServletRequest(attributes.getRequest()) : null;
    }

    public static ApmHttpServletResponse getApmHttpServletResponse() {
        ServletRequestAttributes attributes = getServletRequestAttributes();
        return attributes != null ? new ApmHttpServletResponse(attributes.getResponse()) : null;
    }

    public static ServletRequestAttributes getServletRequestAttributes() {
        if (ReflectUtils.isClassExists(RequestContextHolder.class.getName())) {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                return (ServletRequestAttributes) requestAttributes;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static List<WebPair> getRequestHeaders(HttpServletRequest request) {
        List<WebPair> headers = new ArrayList<>();
        Iterator<String> iterator = request.getHeaderNames().asIterator();
        while (iterator.hasNext()) {
            String name = iterator.next();
            String value = request.getHeader(name);
            headers.add(new WebPair(name, value));
        }
        return headers;
    }

    @SuppressWarnings("unchecked")
    public static List<WebPair> getRequestParams(HttpServletRequest request) {
        List<WebPair> headers = new ArrayList<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            String value = request.getParameter(name);
            headers.add(new WebPair(name, value));
        }
        return headers;
    }

    public static List<WebCookie> getRequestCookies(HttpServletRequest request) {
        List<WebCookie> webCookies = new ArrayList<>();
        Cookie[] servletCookies = request.getCookies();
        if (servletCookies != null) {
            for (Cookie cookie : servletCookies) {
                webCookies.add(new WebCookie(cookie));
            }
        }
        return webCookies;
    }

    public static String getQueryString(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (StringUtils.isNotEmpty(queryString)) {
            return URLDecoder.decode(queryString, StandardCharsets.UTF_8);
        }
        return queryString;
    }

    public static Map<String, Object> queryStringToMap(String queryString) {
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotEmpty(queryString)) {
            if (queryString.startsWith("?")) {
                queryString = queryString.substring(1);
            }
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                int index = pair.indexOf("=");
                if (index != -1) {
                    String key = pair.substring(0, index);
                    String value = pair.substring(index + 1);
                    params.put(key, value);
                    continue;
                }
                throw new IllegalArgumentException("Invalid query string: %s", queryString);
            }
        }
        return params;
    }

    public static String mapToQueryString(Map<String, Object> params) {
        StringBuilder queryString = new StringBuilder();
        if (!params.isEmpty()) {
            params.forEach((k, v) -> queryString.append(k).append("=").append(v).append("&"));
            if (queryString.indexOf("&") != -1) {
                queryString.deleteCharAt(queryString.lastIndexOf("&"));
            }
        }
        return queryString.toString();
    }

    public static String mergeUrlParams(String url, Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        if (url.contains("?")) {
            String[] arr = url.split("\\?");
            url = arr[0];
            String[] pairs = arr[1].split("&");
            for (String pair : pairs) {
                String[] urlParam = pair.split("=");
                params.put(urlParam[0], urlEncode(urlParam[1]));
            }
        }
        String queryString = mapToQueryString(params);
        if (StringUtils.isNotEmpty(queryString)) {
            url += "?" + queryString;
        }
        return url;
    }

    public static String encodeChinese(String str) {
        Matcher matcher = CHINESE_PATTERN.matcher(str);
        while (matcher.find()) {
            String c = matcher.group();
            str = str.replaceAll(c, urlEncode(c));
        }
        return str;
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ip = null;
        for (String ipHeader : IP_HEADERS) {
            ip = request.getHeader(ipHeader);
            if (StringUtils.isNotEmpty(ip) && "unknown".equalsIgnoreCase(ip)) {
                break;
            }
        }
        return ip == null ? request.getRemoteAddr() : ip;
    }

    public static String getDomain(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int port = request.getLocalPort();
        return scheme + "://" + serverName + (port == 80 ? "" : ":" + port);
    }

    public static String urlDecode(String str, Charset charset) {
        return str == null ? null : URLDecoder.decode(str, charset);
    }

    public static String urlDecode(String str) {
        return urlDecode(str, StandardCharsets.UTF_8);
    }

    public static String urlEncode(String str, Charset charset) {
        return str == null ? null : URLEncoder.encode(str, charset);
    }

    public static String urlEncode(String str) {
        return urlEncode(str, StandardCharsets.UTF_8);
    }

    @Getter
    @Setter
    public static class WebCookie {

        private String name;
        private String value;
        private String domain;
        private int maxAge;
        private String path;
        private boolean secure;
        private String comment;
        private int version;

        public WebCookie(Cookie cookie) {
            this.name = cookie.getName();
            this.value = cookie.getValue();
            this.domain = cookie.getDomain();
            this.maxAge = cookie.getMaxAge();
            this.path = cookie.getPath();
            this.secure = cookie.getSecure();
            this.comment = cookie.getComment();
            this.version = cookie.getVersion();
        }
    }

    @Getter
    @Setter
    public static class WebPair {
        private String key;
        private String value;

        public WebPair(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }


}

package io.github.thebesteric.framework.apm.agent.commons.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.thebesteric.framework.apm.agent.commons.LoggerPrinter;
import io.github.thebesteric.framework.apm.agent.commons.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * ApmHttpServletRequest
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-10-07 23:50:40
 */
@Slf4j
public class ApmHttpServletRequest extends HttpServletRequestWrapper {

    private final byte[] body;

    public ApmHttpServletRequest(HttpServletRequest request) {
        super(request);
        byte[] temp = new byte[0];
        if (canBeConvert(request)) {
            temp = getRequestBody(request);
        }
        body = temp;
    }

    public String getRawBody() {
        if (body != null && body.length > 0) {
            return new String(body, StandardCharsets.UTF_8);
        }
        return null;
    }

    public Object getBody() {
        String rawBody = getRawBody();
        if (rawBody != null) {
            try {
                return JsonUtils.mapper.readValue(rawBody, Map.class);
            } catch (JsonProcessingException e) {
                return rawBody;
            }
        }
        return null;
    }

    public byte[] getRequestBody(HttpServletRequest request) {
        byte[] buffer = new byte[0];
        InputStream in = null;
        int len = request.getContentLength();
        if (len > 0) {
            try {
                buffer = new byte[len];
                in = request.getInputStream();
                int read = 1;
                int totalRead = 0;
                while (read > 0) {
                    read = in.read(buffer, totalRead, buffer.length - totalRead);
                    if (read > 0)
                        totalRead += read;
                }
            } catch (IOException e) {
                LoggerPrinter.error(log, e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        LoggerPrinter.error(log, e);
                    }
                }
            }
        }
        return buffer;
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // Nothing to do here
            }

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

    private boolean canBeConvert(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType != null) {
            contentType = contentType.toLowerCase();
            return !contentType.startsWith("multipart/")
                    && !contentType.startsWith("application/x-www-form-urlencoded")
                    && !contentType.startsWith("application/octet-stream");
        }
        return true;
    }
}

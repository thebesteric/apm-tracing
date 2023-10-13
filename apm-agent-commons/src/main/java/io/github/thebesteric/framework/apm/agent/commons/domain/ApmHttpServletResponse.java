package io.github.thebesteric.framework.apm.agent.commons.domain;

import lombok.Getter;
import lombok.Setter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * ApmHttpServletResponse
 *
 * @author wangweijun
 * @version v1.0
 * @since 2023-10-08 00:02:46
 */
public class ApmHttpServletResponse extends HttpServletResponseWrapper {
    private final ByteArrayOutputStream buffer;
    private final ServletOutputStream out;

    @Getter
    @Setter
    private Throwable exception;

    public ApmHttpServletResponse(HttpServletResponse response) {
        super(response);
        buffer = new ByteArrayOutputStream();
        out = new OutputStreamWrapper(buffer);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return out;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (out != null) out.flush();
    }

    public void setBuffer(String message) throws IOException {
        buffer.reset();
        buffer.write(message.getBytes());
    }

    public byte[] getByteArray() throws IOException {
        flushBuffer();
        return buffer.toByteArray();
    }

    private static class OutputStreamWrapper extends ServletOutputStream {

        private final ByteArrayOutputStream byteArrayOutputStream;

        public OutputStreamWrapper(ByteArrayOutputStream byteArrayOutputStream) {
            this.byteArrayOutputStream = byteArrayOutputStream;
        }

        @Override
        public void write(int b) throws IOException {
            byteArrayOutputStream.write(b);
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            // Nothing to do here
        }
    }
}

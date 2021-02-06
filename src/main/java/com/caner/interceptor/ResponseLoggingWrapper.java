package com.caner.interceptor;

import org.apache.commons.io.output.TeeOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ResponseLoggingWrapper extends HttpServletResponseWrapper {
    private final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    private long id;

    /**
     * @param requestId and id which gets logged to output file. It's used to bind response with
     *                  response (they will have same id, currenttimemilis is used)
     * @param response  response from which we want to extract stream data
     */
    ResponseLoggingWrapper(Long requestId, HttpServletResponse response) {
        super(response);
        this.id = requestId;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        final ServletOutputStream servletOutputStream = ResponseLoggingWrapper.super.getOutputStream();
        return new ServletOutputStream() {
            private TeeOutputStream tee = new TeeOutputStream(servletOutputStream, bos);

            @Override
            public void write(byte[] b) throws IOException {
                tee.write(b);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                tee.write(b, off, len);
            }

            @Override
            public void flush() throws IOException {
                tee.flush();
//                logRequest();
            }

            @Override
            public void write(int b) throws IOException {
                tee.write(b);
            }

            @Override
            public boolean isReady() {
                return servletOutputStream.isReady();
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
                servletOutputStream.setWriteListener(writeListener);
            }


            @Override
            public void close() throws IOException {
                super.close();
            }
        };
    }

    /**
     * this method will clear the buffer, so
     *
     * @return captured bytes from stream
     */
    private byte[] toByteArray() {
        byte[] ret = bos.toByteArray();
        bos.reset();
        return ret;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    String getResponseBody() {
        return new String(toByteArray(), StandardCharsets.UTF_8);
    }

}

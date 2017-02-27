package com.dakuupa.nebula;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author etwilliams
 */
public class HttpWrapper {

    private String url;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private String responseString = "";
    private boolean outputFinished;

    public HttpWrapper(String url, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        this.url = url;
        this.request = servletRequest;
        this.response = servletResponse;
    }

    public void writeInputStream(InputStream input, String contentType) throws IOException {

        response.setContentType(contentType);
        response.setContentLengthLong(input.available());

        byte[] buffer = new byte[10240];
        try (
                OutputStream output = response.getOutputStream();) {
            for (int length = 0; (length = input.read(buffer)) > 0;) {
                output.write(buffer, 0, length);
            }
        }
        outputFinished = true;
    }

    public void writeFinal() {
        if (!outputFinished) {
            try {
                response.getWriter().print(responseString);
            } catch (IOException ex) {
                Logger.getLogger(Activity.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        outputFinished = true;
    }

    public void write(String str) {
        responseString += str;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public boolean isOutputFinished() {
        return outputFinished;
    }

    public void setOutputFinished(boolean outputFinished) {
        this.outputFinished = outputFinished;
    }

}

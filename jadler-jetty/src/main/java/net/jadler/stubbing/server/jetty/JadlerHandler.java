/*
 * Copyright (c) 2013 Jadler contributors
 * This program is made available under the terms of the MIT License.
 */
package net.jadler.stubbing.server.jetty;

import net.jadler.stubbing.StubResponse;
import net.jadler.RequestManager;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import net.jadler.stubbing.Headers;
import org.apache.commons.lang.Validate;


/**
 * Jetty Handler which handles all http communication by returning an http response according to a stub response
 * obtained from a {@link RequestManager} instance.
 */
class JadlerHandler extends AbstractHandler {

    private final RequestManager requestManager;

    
    /**
     * @param requestManager request manager instance to retrieve stub responses
     */
    JadlerHandler(final RequestManager requestManager) {
        Validate.notNull(requestManager, "requestManager cannot be null");
        this.requestManager = requestManager;
    }


    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request,
            final HttpServletResponse response) throws IOException, ServletException {

        final net.jadler.Request req = RequestUtils.convert(request);
        final StubResponse stubResponse = this.requestManager.provideStubResponseFor(req);
        
        response.setStatus(stubResponse.getStatus());
        this.insertResponseHeaders(stubResponse.getHeaders(), response);        
        
        baseRequest.setHandled(true);
        
        this.processDelay(stubResponse.getDelay());
        this.insertResponseBody(stubResponse.getBody(), response);
    }

    
    private void insertResponseBody(final byte[] body, final HttpServletResponse response) throws IOException {
        if (body.length > 0) {
            final OutputStream os = response.getOutputStream();
            os.write(body);
        }
    }

    
    private void insertResponseHeaders(final Headers headers, final HttpServletResponse response) {
        for (final String name: headers.getNames()) {
            
            for (final String value: headers.getValues(name)) {
                response.addHeader(name, value);
            }
        }
    }


    private void processDelay(final long delay) {
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
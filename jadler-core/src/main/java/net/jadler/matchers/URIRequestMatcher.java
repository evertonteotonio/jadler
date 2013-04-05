/*
 * Copyright (c) 2013 Jadler contributors
 * This program is made available under the terms of the MIT License.
 */
package net.jadler.matchers;

import net.jadler.Request;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;


/**
 * Implementation of <tt>RequestMatcher</tt> used for matching uri path decoded from HTTP request.
 */
public class URIRequestMatcher extends RequestMatcher<String> {

    public URIRequestMatcher(final Matcher<? super String> pred) {
        super(pred);
    }


    @Override
    public String retrieveValue(final Request req) throws Exception {
        return req.getURI().getRawPath();
    }
    
    
    @Override
    protected String provideDescription() {
        return "URI is";
    }


    @Factory
    public static URIRequestMatcher requestURI(final Matcher<? super String> pred) {
        return new URIRequestMatcher(pred);
    }
}
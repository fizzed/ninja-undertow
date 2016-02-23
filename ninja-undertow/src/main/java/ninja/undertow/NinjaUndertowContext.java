/**
 * Copyright 2016 Fizzed, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ninja.undertow;

import ninja.undertow.util.UndertowCookieHelper;
import com.google.common.base.Optional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import ninja.Cookie;
import ninja.Result;
import ninja.bodyparser.BodyParserEngineManager;
import ninja.session.FlashScope;
import ninja.session.Session;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;
import ninja.utils.ResponseStreams;
import ninja.validation.Validation;

import org.apache.commons.fileupload.FileItemIterator;

import com.google.inject.Inject;
import com.google.inject.Injector;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormData.FormValue;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Deque;
import java.util.Iterator;
import ninja.undertow.util.UndertowFileItemIterator;
import ninja.undertow.util.UndertowFileItemStream;
import ninja.undertow.util.UndertowHelper;
import ninja.uploads.FileItem;
import ninja.utils.AbstractContext;
import static org.apache.commons.fileupload.FileUploadBase.MULTIPART;
import org.apache.commons.lang.StringUtils;

public class NinjaUndertowContext extends AbstractContext {
    //private final Logger logger = LoggerFactory.getLogger(NinjaUndertowContext.class);

    private final String[] STRING_ARRAY = new String[0];
    
    private final Map<String,Object> attributes;
    private HttpServerExchange exchange;
    private FormData formData;

    @Inject
    public NinjaUndertowContext(
            BodyParserEngineManager bodyParserEngineManager,
            FlashScope flashScope,
            NinjaProperties ninjaProperties,
            Session session,
            Validation validation,
            Injector injector) {
        
        super(
                bodyParserEngineManager,
                flashScope,
                ninjaProperties,
                session,
                validation,
                injector);
        
        this.attributes = new HashMap<>();
    }

    public void init(HttpServerExchange exchange, String contextPath) {
        this.exchange = exchange;

        //enforceCorrectEncodingOfRequest();
//        requestPath = performGetRequestPath();
        
        // any form data should have been eagerly parsed
        this.formData = exchange.getAttachment(FormDataParser.FORM_DATA);
        
        super.init(contextPath, exchange.getRequestPath());
    }
    
    @Override
    public String getRequestPath() {
        String contextPath = this.getContextPath();
        String requestPath = exchange.getRequestPath();
        
        // account for contextPath not being removed while in undertow
        if (StringUtils.isNotEmpty(contextPath)
                && requestPath.startsWith(contextPath)) {
            return requestPath.substring(contextPath.length());
        }
        
        return requestPath;
    }
    
    @Deprecated
    @Override
    public String getRequestUri() {
        return exchange.getRequestURI();
    }

    @Override
    public String getHostname() {
        return exchange.getHostName();
    }

    @Override
    public String getScheme() {
        return exchange.getRequestScheme();
    }

    @Override
    public String getRealRemoteAddr() {
        InetSocketAddress sourceAddress = exchange.getSourceAddress();
        if (sourceAddress != null) {
            InetAddress address = sourceAddress.getAddress();
            if (address != null) {
                return address.getHostAddress();
            }
        }
        return null;
    }
    
    @Override
    public String getMethod() {
        return exchange.getRequestMethod().toString();
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public <T> T getAttribute(String name, Class<T> clazz) {
        return clazz.cast(getAttribute(name));
    }

    @Override
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    @Override
    public Map<String,Object> getAttributes() {
        return attributes;
    }
    
    @Override
    public String getParameter(String name) {
        // Returns the value of a request parameter as a String, or null if the
        // parameter does not exist. Request parameters are extra information sent
        // with the request. For ninja (following servlet rule), parameters are contained in the
        // query string or posted form data.
        Deque<String> queryParameterValues = exchange.getQueryParameters().get(name);
        
        if (queryParameterValues != null && !queryParameterValues.isEmpty()) {
            return queryParameterValues.getFirst();
        } else {
            // fallback to form data
            if (this.formData != null) {
                FormData.FormValue value = this.formData.getFirst(name);
                if (value != null) {
                    return value.getValue();
                }
            }
        }
        
        return null;
    }

    @Override
    public List<String> getParameterValues(String name) {
        List<String> values = new ArrayList<>();
        
        Deque<String> queryParameterValues = exchange.getQueryParameters().get(name);
        
        // merge values from query parameters
        if (queryParameterValues != null) {
            values.addAll(queryParameterValues);
        }
        
        // merge values from form data
         if (this.formData != null) {
            Deque<FormValue> formValues = this.formData.get(name);
            if (formValues != null) {
                for (FormValue formValue : formValues) {
                    values.add(formValue.getValue());
                }
            }
        }
        
        if (values.isEmpty()) {
            return null;
        }
        
        return values;
    }

    @Override
    public Map<String, String[]> getParameters() {
        // build parameter map
        Map<String, String[]> parameters = new HashMap<>();
        
        // merge values from query parameters
        for (Map.Entry<String, Deque<String>> entry : exchange.getQueryParameters().entrySet()) {
            parameters.put(entry.getKey(), entry.getValue().toArray(STRING_ARRAY));
        }
        
        // merge values from form data
        if (this.formData != null) {
            Iterator<String> it = this.formData.iterator();
            while (it.hasNext()) {
                String formName = it.next();
                Deque<FormValue> formValues = this.formData.get(formName);
                UndertowHelper.createOrMerge(parameters, formName, formValues);
            }
        }
        
        return parameters;
    }

    @Override
    public String getHeader(String name) {
        return exchange.getRequestHeaders().getFirst(name);
    }

    @Override
    public List<String> getHeaders(String name) {
        return exchange.getRequestHeaders().get(name);
    }

    @Override
    public Map<String,List<String>> getHeaders() {
        // build map of headers
        Map<String, List<String>> headers = new HashMap<>();
        
        for (HeaderValues values : exchange.getRequestHeaders()) {
            headers.put(values.getHeaderName().toString(), values);
        }
        
        return headers;
    }

    @Override
    public Cookie getCookie(String cookieName) {
        io.undertow.server.handlers.Cookie undertowCookie
                = exchange.getRequestCookies().get(cookieName);
        
        if (undertowCookie == null) {
            return null;
        }
        
        return UndertowCookieHelper.convertUndertowCookieToNinjaCookie(undertowCookie);
    }

    @Override
    public String getCookieValue(String cookieName) {
        io.undertow.server.handlers.Cookie undertowCookie
                = exchange.getRequestCookies().get(cookieName);
        
        if (undertowCookie == null) {
            return null;
        }
        
        return undertowCookie.getValue();
    }
    
    @Override
    public boolean hasCookie(String cookieName) {
        return exchange.getRequestCookies().containsKey(cookieName);
    }

    @Override
    public List<Cookie> getCookies() {
        Map<String, io.undertow.server.handlers.Cookie> undertowCookies = exchange.getRequestCookies();
        
        if (undertowCookies == null) {
            return Collections.EMPTY_LIST;
        }
        
        List<Cookie> ninjaCookies = new ArrayList<>(undertowCookies.size());
        
        for (Map.Entry<String, io.undertow.server.handlers.Cookie> entry : undertowCookies.entrySet()) {
            Cookie ninjaCookie = UndertowCookieHelper.convertUndertowCookieToNinjaCookie(entry.getValue());
            ninjaCookies.add(ninjaCookie);
        }
        
        return ninjaCookies;
    }
    
    @Override
    public void addCookie(Cookie cookie) {
        io.undertow.server.handlers.Cookie undertowCookie
                = UndertowCookieHelper.convertNinjaCookieToUndertowCookie(cookie);
        
        exchange.getResponseCookies().put(undertowCookie.getName(), undertowCookie);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return exchange.getInputStream();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        // TODO: charset issues?
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    protected ResponseStreams finalizeHeaders(Result result, Boolean handleFlashAndSessionCookie) {
        // delegate cookie, session, and flash to parent
        super.finalizeHeaders(result, handleFlashAndSessionCookie);
        
        exchange.setStatusCode(result.getStatusCode());

        // copy headers
        for (Entry<String, String> header : result.getHeaders().entrySet()) {
            exchange.getResponseHeaders()
                .add(new HttpString(header.getKey()), header.getValue());
        }

        // charset in use
        final String charset = Optional.fromNullable(result.getCharset()).or(NinjaConstant.UTF_8);
        
        // build content-type header (but only if it does not yet exist)
        if (result.getContentType() != null) {
            String contentTypeHeader = new StringBuilder()
                .append(result.getContentType())
                .append("; charset=")
                .append(charset)
                .toString();

            exchange.getResponseHeaders().put(
                Headers.CONTENT_TYPE, contentTypeHeader);
        }

        return new ResponseStreams() {

            @Override
            public OutputStream getOutputStream() throws IOException {
                return exchange.getOutputStream();
            }

            @Override
            public Writer getWriter() throws IOException {
                return new OutputStreamWriter(exchange.getOutputStream(), charset);
            }
        };
    }

    @Override
    public String getRequestContentType() {
        return exchange.getRequestHeaders().getFirst("Content-Type");
    }

    @Override
    public boolean isMultipart() {
        // logic extracted from ServletFileUpload.isMultipartContent
        if (!"post".equalsIgnoreCase(getMethod())) {
            return false;
        }
        
        String contentTypeHeader = this.getRequestContentType();

        if (contentTypeHeader == null) {
            return false;
        } else if (contentTypeHeader.toLowerCase().startsWith(MULTIPART)) {
            return true;
        }
        return false;
    }

    @Override
    public FileItemIterator getFileItemIterator() {
        if (this.formData == null) {
            return null;
        }
        
        // create list of file items
        final List<UndertowFileItemStream> items = new ArrayList<>();

        Iterator<String> it = this.formData.iterator();
        while (it.hasNext()) {
            String name = it.next();
            FormValue value = this.formData.getFirst(name);
            items.add(new UndertowFileItemStream(name, value));
        }
        
        return new UndertowFileItemIterator(items);
    }
    
    @Override
    public void handleAsync() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void returnResultAsync(Result result) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Result controllerReturned() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
    private String performGetRequestPath() {
        // http://stackoverflow.com/questions/966077/java-reading-undecoded-url-from-servlet

        // this one is unencoded:
        String unencodedContextPath = httpServletRequest.getContextPath();
        
        // this one is unencoded, too, but may containt the context:
        String fullUnencodedUri = httpServletRequest.getRequestURI();

        String result = fullUnencodedUri.substring(unencodedContextPath
                .length());

        return result;
    }
    */
    
    /**
     * Get the underlying Undertow <code>HttpServerExchange</code> object.
     * @return The underlying Undertow <code>HttpServerExchange</code> object.
     */
    public HttpServerExchange getExchange() {
        return this.exchange;
    }

    
    
    
    
    @Override
    public FileItem getParameterAsFileItem(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<FileItem> getParameterAsFileItems(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, List<FileItem>> getParameterFileItems() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cleanup() {
        // do nothing for right now...
    }
    
}

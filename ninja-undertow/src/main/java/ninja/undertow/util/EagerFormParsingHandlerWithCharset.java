/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package ninja.undertow.util;

import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.server.handlers.form.EagerFormParsingHandler;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;

/**
 * This class is based on the original {@link EagerFormParsingHandler}, but uses a different {@link FormParserFactory} in order to set a custom charset for form parsing.
 *
 * @author Jens Fendler
 */
public class EagerFormParsingHandlerWithCharset implements HttpHandler {

    private volatile HttpHandler next = ResponseCodeHandler.HANDLE_404;
    private final FormParserFactory formParserFactory;
	private String defaultCharset;

    public EagerFormParsingHandlerWithCharset() {
    	this(null);
    }
    
    public EagerFormParsingHandlerWithCharset(final FormParserFactory formParserFactory) {
    	this(formParserFactory, null);
    }

    public EagerFormParsingHandlerWithCharset(final FormParserFactory formParserFactory, String defaultCharset) {
    	this.defaultCharset = defaultCharset;
    	if ( formParserFactory == null ) {
    		// build the factory if not provided
        	FormParserFactory.Builder builder = FormParserFactory.builder();
        	builder.setDefaultCharset(this.defaultCharset);
        	this.formParserFactory = builder.build();
    	} else {
    		this.formParserFactory = formParserFactory;
    	}
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        FormDataParser parser = formParserFactory.createParser(exchange);
        if (parser == null) {
            next.handleRequest(exchange);
            return;
        }
        if(exchange.isBlocking()) {
            exchange.putAttachment(FormDataParser.FORM_DATA, parser.parseBlocking());
            next.handleRequest(exchange);
        } else {
            parser.parse(next);
        }
    }

    public HttpHandler getNext() {
        return next;
    }

    public EagerFormParsingHandlerWithCharset setNext(final HttpHandler next) {
        Handlers.handlerNotNull(next);
        this.next = next;
        return this;
    }
}

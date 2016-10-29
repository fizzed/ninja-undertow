/**
 * Copyright 2016 Fizzed, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja.undertow;

import ch.qos.logback.classic.Level;
import com.google.inject.Injector;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.predicate.Predicate;
import io.undertow.predicate.Predicates;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.RequestDumpingHandler;
import io.undertow.server.handlers.form.EagerFormParsingHandler;
import io.undertow.server.handlers.form.FormParserFactory;
import javax.net.ssl.SSLContext;
import ninja.standalone.AbstractStandalone;
import ninja.utils.NinjaConstant;
import org.apache.commons.lang3.StringUtils;

/**
 * Ninja standalone based on Undertow.
 */
public class NinjaUndertow extends AbstractStandalone<NinjaUndertow> {
    
    protected final NinjaUndertowSettings settings;
    protected Undertow undertow;
    protected boolean undertowStarted;                      // undertow fails on stop() if start() never called
    protected HttpHandler undertowHandler;
    protected NinjaUndertowHandler ninjaUndertowHandler;
    protected NinjaUndertowBootstrap ninjaUndertowBootstrap;
    protected SSLContext sslContext;
    
    public NinjaUndertow() {
        super("NinjaUndertow");
        this.settings = new NinjaUndertowSettings();
    }
    
    public static void main(String [] args) {
        // create new instance and run it
        new NinjaUndertow().run();
    }
    
    @Override
    public Injector getInjector() {
        checkStarted();
        return this.ninjaUndertowBootstrap.getInjector();
    }
    
    @Override
    public void doConfigure() throws Exception {
        // apply properties to settings
        this.settings.apply(overlayedNinjaProperties);
        
        // pass along context (this mirrors what ninja-servlet does)
        this.ninjaProperties.setContextPath(getContextPath());
        
        // create new bootstrap to kickoff ninja
        this.ninjaUndertowBootstrap = new NinjaUndertowBootstrap(ninjaProperties);
        
        // create chain of undertow handlers
        this.undertowHandler = createHttpHandler();
        
        this.undertow = createUndertow();
    }
    
    @Override
    public void doStart() throws Exception {
        try {
            this.ninjaUndertowBootstrap.boot();
        } catch (Exception e) {
            throw tryToUnwrapInjectorException(e);
        }
        
        // slipstream injector into undertow handler BEFORE server starts
        this.ninjaUndertowHandler.init(ninjaUndertowBootstrap.getInjector(), getContextPath());

        String version = undertow.getClass().getPackage().getImplementationVersion();
        
        logger.info("Trying to start undertow v{} {}", version, this.getLoggableIdentifier());
        
        this.undertow.start();
        undertowStarted = true;
        
        logger.info("Started undertow v{} {}", version, this.getLoggableIdentifier());
    }
    
    @Override
    public void doJoin() throws Exception {
        // undertow doesn't let us join it, so we'll instead wait ourselves
        synchronized(this) {
            this.wait();
        }
    }

    @Override
    public void doShutdown() {
        if (this.undertow != null && undertowStarted) {
            logger.info("Trying to stop undertow {}", this.getLoggableIdentifier());
            this.undertow.stop();
            logger.info("Stopped undertow {}", this.getLoggableIdentifier());
            this.undertow = null;
        }
        
        if (this.ninjaUndertowBootstrap != null) {
            this.ninjaUndertowBootstrap.shutdown();
            this.ninjaUndertowBootstrap = null;
        }
    }
    
    // sub-classes may be interested in these
    
    protected HttpHandler createHttpHandler() {
        // root handler for ninja app
        this.ninjaUndertowHandler = new NinjaUndertowHandler();
        
        HttpHandler h = this.ninjaUndertowHandler;
        
        // wireshark enabled?
        if (this.settings.getTracing()) {
            logger.info("Undertow tracing of requests and responses activated ({} = true)", NinjaUndertowSettings.TRACING);
            // only activate request dumping on non-assets
            Predicate isAssets = Predicates.prefix("/assets");
            h = Handlers.predicate(isAssets, h, new RequestDumpingHandler(h));
        }
        
        // then eagerly parse form data (which is then included as an attachment)
        FormParserFactory.Builder formParserFactoryBuilder = FormParserFactory.builder();
        formParserFactoryBuilder.setDefaultCharset(NinjaConstant.UTF_8);
        h = new EagerFormParsingHandler(formParserFactoryBuilder.build()).setNext(h);
        
        // then requests MUST be blocking for IO to function
        h = new BlockingHandler(h);
        
        // then a context if one exists
        if (StringUtils.isNotEmpty(this.getContextPath())) {
            h = new PathHandler().addPrefixPath(this.getContextPath(), h);
        }
        
        return h;
    }
    
    protected Undertow.Builder createUndertowBuilder() throws Exception {
        Undertow.Builder undertowBuilder = Undertow.builder()
            .setHandler(this.undertowHandler) 
            // NOTE: should ninja not use equals chars within its cookie values?
            .setServerOption(UndertowOptions.ALLOW_EQUALS_IN_COOKIE_VALUE, true);
        
        if (isPortEnabled()) {
            undertowBuilder.addHttpListener(getPort(), getHost());
        }
        
        if (isSslPortEnabled()) {
            this.sslContext = this.createSSLContext();
 
            // workaround for chrome issue w/ JVM and self-signed certs triggering
            // an IOException that can safely be ignored
            ch.qos.logback.classic.Logger root
                     = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger("io.undertow.request.io");
            root.setLevel(Level.WARN);
            
            undertowBuilder.addHttpsListener(this.sslPort, this.host, this.sslContext);
        }
        
        logger.info("Undertow h2 protocol ({} = {})", NinjaUndertowSettings.HTTP2, this.settings.getHttp2());
        undertowBuilder.setServerOption(UndertowOptions.ENABLE_HTTP2, this.settings.getHttp2());
        
        return undertowBuilder;
    }

    protected Undertow createUndertow() throws Exception {
        return createUndertowBuilder().build();
    }

    public NinjaUndertowSettings getSettings() {
        return this.settings;
    }

}

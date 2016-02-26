package ninja;

/**
 * Copyright (C) 2012-2016 the original author or authors.
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

import ninja.utils.LoggerProvider;
import ninja.utils.NinjaProperties;
import ninja.utils.NinjaPropertiesImpl;

import org.slf4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import ninja.bodyparser.BodyParserEnginePost;
import ninja.template.TemplateEngineText;

public class Configuration extends AbstractModule {

    private final NinjaPropertiesImpl ninjaProperties;

    public Configuration(NinjaPropertiesImpl ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
    }

    @Override
    public void configure() {

        System.setProperty("file.encoding", "utf-8");
        
        bind(RouteBuilder.class).to(RouteBuilderImpl.class);

        bind(Router.class).to(RouterImpl.class).in(Singleton.class);

        // provide logging
        bind(Logger.class).toProvider(LoggerProvider.class);

        // Bind the configuration into Guice
        ninjaProperties.bindProperties(binder());
        bind(NinjaProperties.class).toInstance(ninjaProperties);

        // bind www-url-encoded body parsers
        bind(BodyParserEnginePost.class);
        
        // bind text content type
        bind(TemplateEngineText.class);
        
        
        
        
        // THESE SHOULD ALL BE OPTIONAL IMHO
        
        /**
        OptionalBinder.newOptionalBinder(binder(), ObjectMapper.class)
                .setDefault().toProvider(ObjectMapperProvider.class)
                .in(Singleton.class);

        OptionalBinder.newOptionalBinder(binder(), XmlMapper.class).setDefault()
                .toProvider(XmlMapperProvider.class).in(Singleton.class);
        
        // Postoffice
        bind(Postoffice.class).toProvider(PostofficeProvider.class);
        */
        
        /**
        // Cache
        OptionalBinder.newOptionalBinder(binder(), Cache.class)
                .setDefault().toProvider(CacheProvider.class).in(Singleton.class);
        */
        
        /**
        bind(MigrationInitializer.class).asEagerSingleton();
        
        install(new JpaModule(ninjaProperties));
        */
    }

}

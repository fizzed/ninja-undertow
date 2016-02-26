package conf;

import ninja.template.TemplateEngineFreemarker;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import ninja.template.TemplateEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class Module extends AbstractModule {
    static private final Logger log = LoggerFactory.getLogger(Module.class);
    
    @Override
    protected void configure() {
        
        //bind(TemplateEngine.class).to(TemplateEngineFreemarker.class);
        
        // disable annoying ehcache update checks
        // System.setProperty("net.sf.ehcache.skipUpdateCheck", "true");
    }

}
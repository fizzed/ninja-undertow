package conf;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class Module extends AbstractModule {
    static private final Logger log = LoggerFactory.getLogger(Module.class);
    
    @Override
    protected void configure() {
        // disable annoying ehcache update checks
        //System.setProperty("net.sf.ehcache.skipUpdateCheck", "true");
        
        //install(new TemplateEngineJsonModule());
        
        bind(TemplateEngineA.class);
        bind(TemplateEngineB.class);
    }

}
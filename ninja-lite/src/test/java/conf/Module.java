package conf;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import ninja.utils.NinjaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class Module extends AbstractModule {
    static private final Logger log = LoggerFactory.getLogger(Module.class);
    
    private final NinjaProperties ninjaProperties;
    
    public Module(NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
    }
    
    @Override
    protected void configure() {
        
        
        //install(new NinjaCacheDisabledModule());
        
        //bind(TemplateEngine.class).to(TemplateEngineFreemarker.class);
        
        // disable annoying ehcache update checks
        // System.setProperty("net.sf.ehcache.skipUpdateCheck", "true");
    }

}
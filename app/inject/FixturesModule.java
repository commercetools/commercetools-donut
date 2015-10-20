package inject;

import com.google.inject.AbstractModule;
import services.ImportService;
import services.ImportServiceImpl;

import javax.inject.Singleton;

public class FixturesModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ImportService.class).to(ImportServiceImpl.class).in(Singleton.class);
    }
}
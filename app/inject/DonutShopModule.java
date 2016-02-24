package inject;

import com.google.inject.AbstractModule;
import pactas.Pactas;
import pactas.PactasImpl;
import services.CartService;
import services.CartServiceImpl;
import services.OrderService;
import services.OrderServiceImpl;

import javax.inject.Singleton;

public class DonutShopModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CartService.class).to(CartServiceImpl.class).in(Singleton.class);
        bind(OrderService.class).to(OrderServiceImpl.class).in(Singleton.class);
        bind(Pactas.class).to(PactasImpl.class).in(Singleton.class);
    }
}

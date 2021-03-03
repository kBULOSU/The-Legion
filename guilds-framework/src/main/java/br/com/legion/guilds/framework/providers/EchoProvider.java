package br.com.legion.guilds.framework.providers;

import br.com.idea.api.shared.contracts.Provider;
import br.com.legion.guilds.framework.echo.api.Echo;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class EchoProvider implements Provider<Echo> {

    private final Supplier<RedisProvider> redisCacheProvide;

    private Echo echo;

    @Override
    public void prepare() {
        echo = new Echo(redisCacheProvide.get());
    }

    @Override
    public Echo provide() {
        return echo;
    }

    @Override
    public void shutdown() {
    }
}

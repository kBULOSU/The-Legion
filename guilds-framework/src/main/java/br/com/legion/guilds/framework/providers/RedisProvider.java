package br.com.legion.guilds.framework.providers;

import br.com.idea.api.shared.contracts.Provider;
import lombok.ToString;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.net.InetSocketAddress;

@ToString(of = {"address", "database"})
public class RedisProvider implements Provider<JedisPool> {

    protected final InetSocketAddress address;
    protected final String auth;
    protected final int database;

    private JedisPool pool;

    public RedisProvider(InetSocketAddress address, String auth) {
        this(address, auth, Protocol.DEFAULT_DATABASE);
    }

    public RedisProvider(InetSocketAddress address, String auth, int database) {
        this.address = address;
        this.auth = auth;
        this.database = database;
    }

    @Override
    public void prepare() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(8);

        pool = new JedisPool(config, address.getHostName(), address.getPort(), 2000, auth, database);

        try (Jedis jedis = provide().getResource()) {
            System.out.println("Testando redis...");
            System.out.println(this);
            System.out.println(jedis.ping());
            System.out.println("Redis testado!");
        }
    }

    @Override
    public JedisPool provide() {
        return pool;
    }

    @Override
    public void shutdown() {
        if (pool != null) {
            pool.close();
        }
    }
}

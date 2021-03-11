package br.com.legion.guilds.framework.echo.api;

import br.com.idea.api.shared.misc.utils.Printer;
import br.com.legion.guilds.framework.GuildsFrameworkProvider;
import br.com.legion.guilds.framework.providers.RedisProvider;
import br.com.legion.guilds.framework.server.ServerType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.util.SafeEncoder;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class Echo {

    public static final String CHANNEL_BASE_NAME = "minecraft/";

    private final RedisProvider redisProvider;

    /*

     */

    @Getter
    private final HashMap<UUID, Consumer<? extends Response>> responseCallbacks = Maps.newHashMap();

    private final List<EchoSubscriber> subscribers = Lists.newArrayList();

    @Setter
    private EchoSubscriber defaultSubscriber;

    /*

     */

    public EchoPacketHeader createHeader(UUID responseUUID) {
        UUID uuid = UUID.randomUUID();

        ServerType source = GuildsFrameworkProvider.getServerType();
        return new EchoPacketHeader(
                uuid,
                source.getId(),
                responseUUID
        );
    }

    /*

     */

    public <T extends EchoPacket> void publishToAll(T packet) {
        _publish(packet, createHeader(null));
    }

    /*

     */

    private <T extends EchoPacket> void _publish(T packet, EchoPacketHeader header) {
        Class clazz = packet.getClass();
        boolean debug = clazz.getAnnotation(DebugPacket.class) != null;

        if (debug) {
            Printer.INFO.print(String.format("Channel - %s - %s", clazz.getSimpleName(), Echo.CHANNEL_BASE_NAME));
        }

        EchoBufferOutput buffer = new EchoBufferOutput();

        buffer.writeString(packet.getClass().getName());

        header.write(buffer);
        packet.write(buffer);

        packet.setHeader(header);

        for (EchoSubscriber subscriber : subscribers) {
            if (subscriber.channels.contains(Echo.CHANNEL_BASE_NAME)) {
                if (debug) {
                    Printer.INFO.print(String.format("Local executor - %s", clazz.getSimpleName()));
                }

                subscriber.callPacket(Echo.CHANNEL_BASE_NAME, packet);
            }
        }

        try (Jedis jedis = redisProvider.provide().getResource()) {
            if (debug) {
                Printer.INFO.print(String.format("Publish - %s", clazz.getSimpleName()));
            }

            jedis.publish(Echo.CHANNEL_BASE_NAME.getBytes(), buffer.toByteArray());
        }
    }

    /*

     */

    public EchoSubscriber subscribe(@NonNull BiConsumer<EchoPacket, Runnable> dispatcher) {
        List<String> channels = Lists.newArrayList(CHANNEL_BASE_NAME);

        return subscribe(dispatcher, channels);
    }

    @Deprecated
    public EchoSubscriber subscribe(@NonNull BiConsumer<EchoPacket, Runnable> dispatcher, Collection<String> channels) {
        EchoSubscriber echoSubscriber = new EchoSubscriber(dispatcher, channels, Echo.this);

        new Thread(() -> {
            try (Jedis jedis = redisProvider.provide().getResource()) {
                jedis.subscribe(echoSubscriber, SafeEncoder.encodeMany(channels.stream().toArray(String[]::new)));
            }
        }, "Echo Subscriber Thread").start();

        subscribers.add(echoSubscriber);

        return echoSubscriber;
    }

    public void registerListener(EchoListener listener) {
        if (this.defaultSubscriber == null) {
            throw new RuntimeException("Erro ao registrar o listener do echo. O listener base não estava iniciado!");
        }

        this.defaultSubscriber.registerListener(listener);
    }
}

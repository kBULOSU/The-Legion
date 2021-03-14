package br.com.legion.guilds.framework.echo.api;

import br.com.idea.api.shared.misc.utils.Printer;
import br.com.legion.guilds.framework.GuildsFrameworkProvider;
import lombok.RequiredArgsConstructor;
import org.greenrobot.eventbus.EventBus;
import redis.clients.jedis.BinaryJedisPubSub;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class EchoSubscriber extends BinaryJedisPubSub {

    public static boolean DEBUG = false;

    private final BiConsumer<EchoPacket, Runnable> dispatcher;

    protected final Collection<String> channels;

    private final Echo echo;

    //

    private final EventBus eventBus = EventBus.builder()
            .logNoSubscriberMessages(false)
            .throwSubscriberException(true)
            .build();

    protected void callPacket(EchoPacket packet) {
        Class clazz = packet.getClass();
        boolean debug = clazz.getAnnotation(DebugPacket.class) != null;

        EchoPacketHeader header = packet.getHeader();

        /*
          Se o pacote for a resposta de outro pacote.
         */
        if (packet instanceof Response) {

            if (debug) {
                Printer.INFO.print(String.format("Response Packet - %s", clazz.getSimpleName()));
            }

            UUID responseUUID = header.getResponseId();
            Consumer responseConsumer = echo.getResponseCallbacks().remove(responseUUID);

            if (responseConsumer != null) {
                if (debug) {
                    Printer.INFO.print(String.format("Accept Response Consumer - %s", clazz.getSimpleName()));
                }

                responseConsumer.accept(packet);
                return;
            }

            if (debug) {
                Printer.INFO.print(String.format("Response Consumer is null - %s", clazz.getSimpleName()));
            }

            return;
        }

        dispatcher.accept(packet, () -> {
            if (eventBus.hasSubscriberForEvent(clazz)) {
                eventBus.post(packet);

                if (packet instanceof Respondable) {

                    UUID responseUUID = header.getResponseId();

                    try {
                        Respondable respondable = (Respondable) packet;
                        Response response = respondable.getResponse();

                        if (response != null) {
                            echo.publish(
                                    response,
                                    responseUUID
                            );
                        } else {
                            Class responseType = respondable.getClass().getMethod("getResponse").getReturnType();
                            echo.publish((Response) responseType.newInstance(), responseUUID);
                        }
                    } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onMessage(byte[] channel, byte[] message) {
        try {
            EchoBufferInput buffer = new EchoBufferInput(message);

            Class<? extends EchoPacket> clazz;

            String className = buffer.readString();

            try {
                clazz = (Class<? extends EchoPacket>) Class.forName(className);
            } catch (ClassNotFoundException ex) {

                if (DEBUG) {
                    Printer.ERROR.print("Este projeto não suporta o pacote " + className);
                    Printer.ERROR.print(Arrays.toString(message));
                    Printer.ERROR.print("==============================================");
                }

                return;
            } catch (ClassCastException ex) {
                if (DEBUG) {
                    Printer.ERROR.print("Pacote " + className + " inválido.");
                }

                ex.printStackTrace();
                return;
            }

            EchoPacket packet;

            try {
                EchoPacketHeader header = new EchoPacketHeader();
                header.read(buffer);

                boolean debug = clazz.getAnnotation(DebugPacket.class) != null;

                if (!header.getSenderAppId().equals(GuildsFrameworkProvider.getServerType().getId())) {
                    if (!isListening(clazz, header)) {
                        if (debug) {
                            Printer.INFO.print("Nenhum listener - " + clazz.getSimpleName());
                        }

                        return;
                    }

                    packet = clazz.getDeclaredConstructor().newInstance();
                    packet.read(buffer);

                    packet.setHeader(header);

                    if (debug) {
                        Printer.INFO.print("Aceitando - " + clazz.getSimpleName());
                    }

                    callPacket(packet);
                }
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerListener(EchoListener listener) {
        eventBus.register(listener);
    }

    public boolean isListening(Class<? extends EchoPacket> clazz, EchoPacketHeader header) {
        if (eventBus.hasSubscriberForEvent(clazz)) {
            return true;
        }

        if (Response.class.isAssignableFrom(clazz)) {
            return header.getResponseId() == null ? false : echo.getResponseCallbacks().containsKey(header.getResponseId());
        }

        return false;
    }
}

package fr.drogonistudio.spigot.packets.reflective;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import fr.drogonistudio.spigot.packets.RemoteClient;
import fr.drogonistudio.spigot.packets.RemoteClientChannelHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;

/**
 * Utility used to perform custom channel handler injection.
 * 
 * <p>
 * This tool allow to retreive active channels and to inject custom channel
 * handler. That's all.
 * <p>
 * 
 * @author DrogoniEntity
 */
public class InjectionUtils
{
    private static final String PLAYER_LISTENER_CLASS_NAME;
    private static final String SERVER_LISTENER_CLASS_NAME;
    private static final String CONNECTION_CLASS_NAME;

    public static final Channel getPlayerChannel(@Nonnull final Player player) throws ReflectiveOperationException
    {
        Object entity = player.getClass().getMethod("getHandle").invoke(player);

        Class<?> listenerClass = NmsReflection.getMinecraftClass(PLAYER_LISTENER_CLASS_NAME);
        Field listenerField = NmsReflection.getDeclaredFieldByIndex(entity.getClass(), listenerClass, 0);
        Object listener = listenerField.get(entity);

        Class<?> connectionClass = NmsReflection.getMinecraftClass(CONNECTION_CLASS_NAME);
        Field connectionField = NmsReflection.getDeclaredFieldByIndex(listenerClass, connectionClass, 0);
        Object connection = connectionField.get(listener);

        Field channelField = NmsReflection.getDeclaredFieldByIndex(connectionClass, Channel.class, 0);
        channelField.setAccessible(true);

        return (Channel) channelField.get(connection);
    }

    public static final List<Channel> getServerChannels(Server server) throws ReflectiveOperationException
    {
        Object dedicatedServer = server.getClass().getMethod("getServer").invoke(server);
        Class<?> mcServerClass = dedicatedServer.getClass().getSuperclass();

        Class<?> ServerConnectionListener = NmsReflection.getMinecraftClass(SERVER_LISTENER_CLASS_NAME);
        Field listenerField = NmsReflection.getDeclaredFieldByIndex(mcServerClass, ServerConnectionListener, 0);
        listenerField.setAccessible(true);
        Object listener = listenerField.get(dedicatedServer);

        Field connectionsField = NmsReflection.getDeclaredFieldByIndex(ServerConnectionListener, List.class, 1);
        connectionsField.setAccessible(true);
        Class<?> connectionType = (Class<?>) ((ParameterizedType) connectionsField.getGenericType()).getActualTypeArguments()[0];
        List<?> connections = (List<?>) connectionsField.get(listener);

        List<Channel> output = new LinkedList<>();
        Field channelField = NmsReflection.getDeclaredFieldByIndex(connectionType, Channel.class, 0);
        channelField.setAccessible(true);

        Iterator<?> connIterator = connections.iterator();
        while (connIterator.hasNext())
        {
            Object conn = connIterator.next();
            output.add((Channel) channelField.get(conn));
        }

        return Collections.unmodifiableList(output);
    }

    public static void injectCustomHandler(Player player, @Nonnull final Channel channel)
    {
        ChannelPipeline pipeline = channel.pipeline();
        List<String> registeredHandlers = pipeline.names();

        if (registeredHandlers.contains("packet_handler"))
        {
            removeCustomHandler(channel);
            
            RemoteClient client = new RemoteClient((InetSocketAddress) channel.remoteAddress(), player);
            RemoteClientChannelHandler handler = new RemoteClientChannelHandler(client);
            pipeline.addBefore("packet_handler", RemoteClientChannelHandler.HANDLER_NAME, handler);
        }
    }

    public static void removeCustomHandler(@Nonnull final Channel channel)
    {
        ChannelPipeline pipeline = channel.pipeline();
        List<String> registeredHandlers = pipeline.names();

        if (registeredHandlers.contains(RemoteClientChannelHandler.HANDLER_NAME))
            channel.pipeline().remove(RemoteClientChannelHandler.HANDLER_NAME);
    }

    static
    {
        // Depends on server JAR, class names are different...
        PLAYER_LISTENER_CLASS_NAME = NmsReflection.isRunningInDeveloppementJar() ? "server.network.ServerGamePacketListenerImpl" : "server.network.PlayerConnection";
        SERVER_LISTENER_CLASS_NAME = NmsReflection.isRunningInDeveloppementJar() ? "server.network.ServerConnectionListener" : "server.network.ServerConnection";
        CONNECTION_CLASS_NAME = NmsReflection.isRunningInDeveloppementJar() ? "network.Connection" : "network.NetworkManager";
    }

}

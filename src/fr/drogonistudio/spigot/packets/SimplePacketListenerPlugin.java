package fr.drogonistudio.spigot.packets;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

import fr.drogonistudio.spigot.packets.reflective.InjectionUtils;
import fr.drogonistudio.spigot.packets.reflective.NmsReflection;
import io.netty.channel.Channel;

/**
 * Core's main class.
 * 
 * @author DrogoniEntity
 */
public class SimplePacketListenerPlugin extends JavaPlugin
{
    @Override
    public void onLoad()
    {
        try
        {
            // Loading reflection toolkit.
            Class.forName(NmsReflection.class.getName());
        } catch (Throwable fatal)
        {
            getLogger().severe("Couldn't initialize reflective class ! Did the package schema has changed ?");
            fatal.printStackTrace();
        }
    }

    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(new ListenForInjection(), this);

        // Re-inject custom handler to all active channels.
        try
        {
            // Removing injected handler to any server's channels.
            List<Channel> channels = InjectionUtils.getServerChannels(getServer());
            channels.forEach((ch) -> {
                Player player = getPlayerFromAddress((InetSocketAddress) ch.remoteAddress());
                InjectionUtils.injectCustomHandler(player, ch);
            });
        } catch (ReflectiveOperationException ex)
        {
            getLogger().warning("Couldn't get active server's channels !");
            ex.printStackTrace();
        }

    }

    @Override
    public void onDisable()
    {
        try
        {
            // Removing injected handler to any server's channels.
            List<Channel> channels = InjectionUtils.getServerChannels(getServer());
            channels.forEach((ch) -> {
                InjectionUtils.removeCustomHandler(ch);
            });
        } catch (ReflectiveOperationException ex)
        {
            getLogger().warning("Couldn't get active server's channels !");
            ex.printStackTrace();
        }
    }
    
    private Player getPlayerFromAddress(InetSocketAddress addr)
    {
        Player find = null;
        Iterator<? extends Player> onlinePlayers = getServer().getOnlinePlayers().iterator();
        
        while (onlinePlayers.hasNext() && find == null)
        {
            Player player = onlinePlayers.next();
            if (player.getAddress().equals(addr))
                find = player;
        }
        
        return find;
    }

    /**
     * Listener class used to listen to all needed activity (like player join, quit,
     * server ping, etc.).
     * 
     * @author DrogoniEntity
     */
    private class ListenForInjection implements Listener
    {
        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerLogin(PlayerLoginEvent event)
        {
            Player player = event.getPlayer();
            InetAddress remoteAddress = event.getAddress();
            
            try
            {
                List<Channel> channels = InjectionUtils.getServerChannels(getServer());
                
                Iterator<Channel> iterator = channels.iterator();
                boolean injected = false;
                while (iterator.hasNext() && !injected)
                {
                    Channel channel = iterator.next();
                    if (((InetSocketAddress) channel.remoteAddress()).getAddress().equals(remoteAddress))
                    {
                        InjectionUtils.injectCustomHandler(player, channel);
                        injected = true;
                    }
                }
                
            } catch (ReflectiveOperationException ex)
            {
                getLogger().severe("Couldn't get active server's channels !");
                ex.printStackTrace();
            }
        }
        
        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerJoin(PlayerJoinEvent event)
        {
            Player player = event.getPlayer();

            try
            {
                Channel channel = InjectionUtils.getPlayerChannel(player);
                InjectionUtils.injectCustomHandler(player, channel);
            } catch (ReflectiveOperationException ex)
            {
                getLogger().severe("Couldn't inject packet handler to " + player.getName());
                ex.printStackTrace();
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onPlayerQuit(PlayerQuitEvent event)
        {
            Player player = event.getPlayer();

            try
            {
                Channel channel = InjectionUtils.getPlayerChannel(player);
                InjectionUtils.removeCustomHandler(channel);
            } catch (ReflectiveOperationException ex)
            {
                getLogger().severe("Couldn't remove packet handler from " + player.getName());
                ex.printStackTrace();
            }
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onServerListPing(ServerListPingEvent event)
        {
            InetAddress remoteAddress = event.getAddress();
            try
            {
                List<Channel> channels = InjectionUtils.getServerChannels(getServer());
                Iterator<Channel> iterator = channels.iterator();
                boolean injected = false;

                while (iterator.hasNext() && !injected)
                {
                    Channel channel = iterator.next();
                    
                    if (((InetSocketAddress) channel.remoteAddress()).getAddress().equals(remoteAddress))
                    {
                        InjectionUtils.injectCustomHandler(null, channel);
                        injected = true;
                    }
                }
            } catch (ReflectiveOperationException ex)
            {
                getLogger().warning("Couldn't get active server's channels !");
                ex.printStackTrace();
            }
        }
    }
}

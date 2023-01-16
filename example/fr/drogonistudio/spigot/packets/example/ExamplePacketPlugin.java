package fr.drogonistudio.spigot.packets.example;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import fr.drogonistudio.spigot.packets.event.PacketReceiveEvent;
import fr.drogonistudio.spigot.packets.event.PacketSendEvent;

/**
 * An example plugin to listen packets with Simple-Packet-Listener.
 * 
 * <p>
 * This example will simply log in-coming and out-coming packets to plugin's
 * logger.
 * </p>
 * 
 * @author DrogoniEntity
 */
public class ExamplePacketPlugin extends JavaPlugin implements Listener
{
    @Override
    public void onEnable()
    {
        // Checking for dependency
        if (this.getServer().getPluginManager().getPlugin("Simple-Packet-Listener") == null)
        {
            // Dependency not found, abort loading...
            this.getLogger().severe("Simple-Packet-Listener is not installed. Disabling...");
            this.getServer().getPluginManager().disablePlugin(this);

            return;
        }

        // To listen packets, we will use Bukkit API to listen packet
        // events like other Bukkit's events.
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable()
    {
    }

    @EventHandler
    public void onPacketSend(PacketSendEvent event)
    {
        // Simply log out-coming packet.
        //
        // We can use NMS or reflection to filter packet but in this
        // example, we will log any packets.
        StringBuilder message = new StringBuilder("Send packet ")
                // To get packet name, we use its class's name
                .append(event.getPacket().getClass().getSimpleName()) //
                .append(" to ");

        // Since player may not be logged in (maybe he pinging), we
        // ensure that he were logged in to get his name.
        if (event.getRemote().getPlayer() != null)
            message.append(event.getRemote().getPlayer().getName());
        // Otherwise, we use his remote address to identified him.
        else
            message.append(event.getRemote().getAddress().toString());

        this.getLogger().info(message.toString());
    }

    @EventHandler
    public void onPacketReceive(PacketReceiveEvent event)
    {
        // Simply log in-coming packet.
        //
        // We can use NMS or reflection to filter packet but in this
        // example, we will log any packets.
        StringBuilder message = new StringBuilder("Receive packet ")
                // To get packet name, we use its class's name
                .append(event.getPacket().getClass().getSimpleName()) //
                .append(" from ");

        // Since player may not be logged in (maybe he pinging), we
        // ensure that he were logged in to get his name.
        if (event.getRemote().getPlayer() != null)
            message.append(event.getRemote().getPlayer().getName());
        // Otherwise, we use his remote address to identified him.
        else
            message.append(event.getRemote().getAddress().toString());

        this.getLogger().info(message.toString());
    }
}

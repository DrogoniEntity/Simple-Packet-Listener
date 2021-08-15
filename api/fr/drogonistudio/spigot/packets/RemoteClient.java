package fr.drogonistudio.spigot.packets;

import java.net.InetSocketAddress;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;

/**
 * Bundle of client connection.
 * 
 * <p>
 * This bundle will keep a online player representation and client's address.
 * Please note that player representation can be {@code null} depend on handling
 * statement.
 * </p>
 * 
 * @author DrogoniEntity
 */
public final class RemoteClient
{
    /**
     * Client's address.
     */
    private final InetSocketAddress remoteAddress;

    /**
     * Player's representation.
     * 
     * <p>
     * Please note that this representation can be {@code null} depends on handling
     * statement.
     * </p>
     */
    @Nullable
    private final Player player;

    /**
     * Creating a new bundle.
     * 
     * @param addr   - client's address.
     * @param player - player's representation (or {@code null} if player not logged
     *               in).
     */
    public RemoteClient(InetSocketAddress addr, Player player)
    {
        this.remoteAddress = addr;
        this.player = player;
    }

    /**
     * Getting client's address.
     * 
     * @return client's address.
     */
    public InetSocketAddress getAddress()
    {
        return this.remoteAddress;
    }

    /**
     * Getting player instance.
     * 
     * <p>
     * This instance may not exist if client isn't logged in. You should use it only
     * if you sure that player is logged in.
     * </p>
     * 
     * @return player instance (or {@code null} if client isn't logged in).
     */
    @Nullable
    public Player getPlayer()
    {
        return this.player;
    }
}

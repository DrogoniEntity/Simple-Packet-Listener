package fr.drogonistudio.spigot.packets.reflective;

import java.lang.reflect.Field;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import fr.drogonistudio.spigot.packets.SimplePacketListenerPlugin;

public final class NmsReflection
{
	private static final String INTERNAL_VERSION;
	
	private static final boolean LEGACY_MODE;
	private static final boolean IS_DEV_MODE;
	
	private NmsReflection()
	{
	}

	public static Class<?> getMinecraftClass(String className) throws ClassNotFoundException
	{
		String fullName = "net.minecraft.";
		
		// Workaround to make old version to be compatible
		if (LEGACY_MODE)
		{
			int lastDot = className.lastIndexOf('.') + 1;
			className = className.substring(lastDot);
			fullName = "net.minecraft.server." + INTERNAL_VERSION + ".";
		}
		
		fullName = fullName.concat(className);
		return Class.forName(fullName);
	}
	
	public static Class<?> getCraftBukkitClass(String className) throws ClassNotFoundException
	{
		return Class.forName("org.bukkit.craftbukkit." + INTERNAL_VERSION + "." + className);
	}
	
	public static boolean isRunningInDeveloppementJar()
	{
		return IS_DEV_MODE;
	}
	
	public static Field getDeclaredFieldByIndex(Class<?> src, Class<?> fieldType, int index) throws NoSuchFieldException
	{
		return getFieldFromArrayAndIndex(src, src.getDeclaredFields(), fieldType, index);
	}
	
	public static Field getFieldByIndex(Class<?> src, Class<?> fieldType, int index) throws NoSuchFieldException
	{
		return getFieldFromArrayAndIndex(src, src.getFields(), fieldType, index);
	}
	
	private static Field getFieldFromArrayAndIndex(Class<?> src, Field fields[], Class<?> fieldType, int index) throws NoSuchFieldException
	{
		int counter = 0;
		String typeName = fieldType.getName();
		for (int i = 0; i < fields.length; i++)
		{
			Field field = fields[i];
			if (field.getType().getName().equals(typeName))
			{
				if (counter == index)
					return field;
				else
					counter++;
			}
		}
		
		throw new NoSuchFieldException("Couldn't find a field of " + typeName + " in " + src.getName() + " - index " + index);
	}
	
	static
	{
		// Getting internal version
		INTERNAL_VERSION = Bukkit.getServer().getClass().getName().split("\\.")[3];
		
		// Checking minor version to check how to access to Minecraft classes
		int minor = Integer.parseInt(
						INTERNAL_VERSION.substring(INTERNAL_VERSION.indexOf('_') + 1, INTERNAL_VERSION.lastIndexOf("_R"))
					);
		
		LEGACY_MODE = minor < 17;
		
		// Checking if we running in development environment
		boolean isDevJar;
		try
		{
			getMinecraftClass("server.DispenserRegistry");
			isDevJar = false;
		}
		catch (ClassNotFoundException isNotProdJar)
		{
			isDevJar = true;
		}
		
		IS_DEV_MODE = isDevJar;
		
		Logger pluginLogger = JavaPlugin.getPlugin(SimplePacketListenerPlugin.class).getLogger();
		pluginLogger.info("Server class summary :");
		pluginLogger.info("- Internal version : " + INTERNAL_VERSION);
		pluginLogger.info("- Is developpement server : " + (IS_DEV_MODE ? "Yes" : "No"));
	}
}

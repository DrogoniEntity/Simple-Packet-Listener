package fr.drogonistudio.spigot.packets.reflective;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import fr.drogonistudio.spigot.packets.SimplePacketListenerPlugin;

/**
 * Tiny utilities to help to perform some reflective action.
 * 
 * <p>
 * This tool can be used to access to Minecraft classes or CraftBukkit classes
 * and to access to fields by index (and without names).
 * </p>
 * 
 * @author DrogoniEntity
 */
public final class NmsReflection
{
    /**
     * Server's internal version.
     * 
     * <p>
     * This version is found by inspecting CraftBukkit package scheme.
     * </p>
     */
    private static final String INTERNAL_VERSION;

    /**
     * Legacy mode statement to get Minecraft classes.
     * 
     * <p>
     * Since 1.17, package scheme has been completely changed and the old
     * 'net.minecraft.server.<INTERNAL_VERSION>' isn't present. When this mode is
     * enabled, it will perform classes search with old package scheme.
     * </p>
     * 
     * <p>
     * This flag is enabled only if minor game version is lower or equals to 16.
     * </p>
     */
    private static final boolean LEGACY_MODE;

    /**
     * Development purpose JAR detection.
     * 
     * <p>
     * Development purpose JAR correspond to know if active server JAR is running
     * with Mojang-remapped JAR. This flag is used to notify if server is running in
     * a development environment and plugins may change their behavior.
     * </p>
     */
    private static final boolean IS_DEV_MODE;

    // Prevent class instantiation.
    private NmsReflection()
    {
    }

    /**
     * Finding a class located in {@code net.minecraft} package.
     * 
     * <p>
     * The finding class will be named as {@code "net.minecraft" + className}.
     * However, if we running in {@code LEGACY_MODE}, the finding class will be
     * named as {@code
     * "net.minecraft.server." + INTERNAL_VERSION + "." + className} and
     * {@code className} will only keep its' simple name.
     * </p>
     * 
     * <p>
     * This class will be initialized. If nothing is found,
     * {@code ClassNotFoundException} is thrown.
     * </p>
     * 
     * @param className - class to find from {"net.minecraft"} package.
     * @return a Minecraft server class.
     * @throws ClassNotFoundException - if class isn't found.
     */
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

    /**
     * Finding a class located in
     * {@code "org.bukkit.craftbukkit." + INTERNAL_VERSION}.
     * 
     * <p>
     * This class will be initialized. If nothing is found,
     * {@code ClassNotFoundException} is thrown.
     * </p>
     * 
     * @param className - class to find from
     *                  {@code "org.bukkit.craftbukkit." + INTERNAL_VERSION}.
     * @return a CraftBukkit class.
     * @throws ClassNotFoundException - if class isn't found.
     */
    public static Class<?> getCraftBukkitClass(String className) throws ClassNotFoundException
    {
        return Class.forName("org.bukkit.craftbukkit." + INTERNAL_VERSION + "." + className);
    }

    /**
     * Getting development purpose detection result.
     * 
     * <p>
     * Development purpose JAR correspond to know if active server JAR is running
     * with Mojang-remapped JAR. This flag is used to notify if server is running in
     * a development environment and plugins may change their behavior.
     * </p>
     * 
     * @return {@code true} if active server JAR is remapped with Mojang mappings.
     */
    public static boolean isRunningInDeveloppementJar()
    {
        return IS_DEV_MODE;
    }

    /**
     * Getting {@code index}-th declared field from {@code src}.
     * 
     * <p>
     * The field searching will iterate over all {@code src}'s declared fields and
     * when we find a field typed with {@code fieldType}, it will check if it was
     * the {@code index}-th field of {@code fieldType}. If it was the case, it will
     * return it. However, it will continue until we find this field.
     * </p>
     * 
     * <p>
     * In case of no field as been found, an {@code NoSuchFieldException} is thrown.
     * </p>
     * 
     * @param src       - class to scan.
     * @param fieldType - field type to check.
     * @param index     - field's place over all declared fields of
     *                  {@code fieldType}.
     * @return {@code index}-th field of {@code fieldType}.
     * @throws NoSuchFieldException - if the targeted field isn't found.
     * @see #getFieldByIndex(Class, Class, int) - no-declared field version.
     * @see java.lang.Class#getDeclaredFields()
     */
    public static Field getDeclaredFieldByIndex(Class<?> src, Class<?> fieldType, int index) throws NoSuchFieldException
    {
        return getFieldFromArrayAndIndex(src, src.getDeclaredFields(), fieldType, index);
    }

    /**
     * Getting {@code index}-th field from {@code src}.
     * 
     * <p>
     * The field searching will iterate over all {@code src}'s visible fields and
     * when we find a field typed with {@code fieldType}, it will check if it was
     * the {@code index}-th field of {@code fieldType}. If it was the case, it will
     * return it. However, it will continue until we find this field.
     * </p>
     * 
     * <p>
     * In case of no field as been found, an {@code NoSuchFieldException} is thrown.
     * </p>
     * 
     * @param src       - class to scan.
     * @param fieldType - field type to check.
     * @param index     - field's place over all visible fields of
     *                  {@code fieldType}.
     * @return {@code index}-th field of {@code fieldType}.
     * @throws NoSuchFieldException - if the targeted field isn't found.
     * @see #getDeclaredFieldByIndex(Class, Class, int) - declared field version.
     * @see java.lang.Class#getFields()
     */
    public static Field getFieldByIndex(Class<?> src, Class<?> fieldType, int index) throws NoSuchFieldException
    {
        return getFieldFromArrayAndIndex(src, src.getFields(), fieldType, index);
    }

    /**
     * Method utility used to perform finding fields job.
     * 
     * @param src       - targeted class.
     * @param fields    - fields to scan.
     * @param fieldType - field's type to search.
     * @param index     - field's place over all {@code fields} of
     *                  {@code fieldType}.
     * @return {@code index}-th field of {@code fieldType}.
     * @throws NoSuchFieldException - if the targeted field isn't found.
     * @see #getDeclaredFieldByIndex(Class, Class, int)
     * @see #getFieldByIndex(Class, Class, int)
     */
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

    /**
     * Getting {@code index}-th method from {@code src}.
     * 
     * <p>
     * The method searching will iterate over all {@code src}'s declared methods and
     * when we find a method who its return's type is {@code returnType} and its
     * parameters are typed with {@code parametersTypes}, it will check if it was
     * the {@code index}-th method from all method with same signature. If it was
     * the case, it will return it. However, it will continue until we find this
     * method.
     * </p>
     * 
     * <p>
     * In case of no method as been found, an {@code NoSuchMethodException} is
     * thrown.
     * </p>
     * 
     * @param src            - class to scan
     * @param returnType     - method's return type
     * @param parametersType - method's parameters type
     * @param index          - method's location over all method with similar
     *                       signature.
     * @return {@code index}-th method over {@code src}'s declared method with
     *         similar signature.
     * @throws NoSuchMethodException - if method isn't found.
     * @see #getMethodByIndex(Class, Class, Class[], int) - no-declared version
     * @see java.lang.Class#getDeclaredMethods()
     */
    public Method getDeclaredMethodByIndex(Class<?> src, Class<?> returnType, Class<?> parametersType[], int index) throws NoSuchMethodException
    {
        return getMethodFromArrayAndIndex(src.getDeclaredMethods(), returnType, parametersType, index);
    }

    /**
     * Getting {@code index}-th method from {@code src}.
     * 
     * <p>
     * The method searching will iterate over all {@code src}'s visible methods and
     * when we find a method who its return's type is {@code returnType} and its
     * parameters are typed with {@code parametersTypes}, it will check if it was
     * the {@code index}-th method from all method with same signature. If it was
     * the case, it will return it. However, it will continue until we find this
     * method.
     * </p>
     * 
     * <p>
     * In case of no method as been found, an {@code NoSuchMethodException} is
     * thrown.
     * </p>
     * 
     * @param src            - class to scan
     * @param returnType     - method's return type
     * @param parametersType - method's parameters type
     * @param index          - method's location over all method with similar
     *                       signature.
     * @return {@code index}-th method over {@code src}'s method with similar
     *         signature.
     * @throws NoSuchMethodException - if method isn't found.
     * @see #getDeclaredMethodByIndex(Class, Class, Class[], int) - declared version
     * @see java.lang.Class#getMethods()
     */
    public Method getMethodByIndex(Class<?> src, Class<?> returnType, Class<?> parametersType[], int index) throws NoSuchMethodException
    {
        return getMethodFromArrayAndIndex(src.getMethods(), returnType, parametersType, index);
    }

    /**
     * Method utility to perform method finding job.
     * 
     * @param methods        - methods to scan
     * @param returnType     - method's return type
     * @param parametersType - method's parameters type
     * @param index          - method location
     * @return {@code index}-th method over {@code src}'s method with similar
     *         signature.
     * @throws NoSuchMethodException - if targeted method isn't found.
     * @see #getDeclaredMethodByIndex(Class, Class, Class[], int)
     * @see #getMethodByIndex(Class, Class, Class[], int)
     */
    private static Method getMethodFromArrayAndIndex(Method methods[], Class<?> returnType, Class<?> parametersType[], int index) throws NoSuchMethodException
    {
        int counter = 0;
        for (int i = 0; i < methods.length; i++)
        {
            Method method = methods[i];
            if (method.getReturnType().getName().equals(returnType.getName()) && method.getParameterTypes().equals(parametersType))
            {
                if (counter == index)
                    return method;
                else
                    counter++;
            }
        }

        // Writing error message
        StringBuilder msgBuilder = new StringBuilder("Couldn't find a method with following signature: ");
        msgBuilder.append(returnType.getSimpleName()).append(' ');
        msgBuilder.append("(");
        for (int i = 0; i < parametersType.length; i++)
        {
            msgBuilder.append(parametersType[i].getSimpleName());
            if (i < (parametersType.length - 1))
                msgBuilder.append(", ");
        }
        msgBuilder.append(") at index ").append(index).append('.');

        throw new NoSuchMethodException(msgBuilder.toString());
    }

    static
    {
        // Getting internal version
        INTERNAL_VERSION = Bukkit.getServer().getClass().getName().split("\\.")[3];

        // Checking minor version to check how to access to Minecraft classes
        int minor = Integer.parseInt(INTERNAL_VERSION.substring(INTERNAL_VERSION.indexOf('_') + 1, INTERNAL_VERSION.lastIndexOf("_R")));

        LEGACY_MODE = minor < 17;

        // Checking if we running in development environment (attempt to perform same
        // job like server's startup).
        boolean isDevJar;
        try
        {
            getMinecraftClass("server.DispenserRegistry");
            isDevJar = false;
        } catch (ClassNotFoundException isNotProdJar)
        {
            isDevJar = true;
        }

        IS_DEV_MODE = isDevJar;

        Logger pluginLogger = JavaPlugin.getPlugin(SimplePacketListenerPlugin.class).getLogger();
        pluginLogger.info("*** Reflection toolkit initialisation sumary ***");
        pluginLogger.info("- Server internal version : " + INTERNAL_VERSION);
        pluginLogger.info("- Is developpement server : " + (IS_DEV_MODE ? "Yes" : "No"));
    }
}

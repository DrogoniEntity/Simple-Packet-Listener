package fr.drogonistudio.spigot.packets;

/**
 * A pseudo implementation of pointers like in C.
 * 
 * <p>
 * A pointer is used to warp an object. You can use it to pass wrapped object
 * into methods and handle their container between each methods.
 * </p>
 * 
 * <p>
 * However, when you modify {@code content}, it will not applied the change in
 * memory change like in C. To know pointer's content at any time, you can look
 * at {@code content} data.
 * </p>
 * 
 * @author DrogoniEntity
 * @param <Type> Type of {@code content}.
 */
public class Pointer<Type>
{
    /**
     * Pointer's content.
     * 
     * <p>
     * You're free to change this value to point to another object.
     * </p>
     */
    public Type content;

    /**
     * Initialize pointer with a pre-defined content.
     * 
     * @param content - pre-defined value.
     */
    public Pointer(Type content)
    {
        this.content = content;
    }
}

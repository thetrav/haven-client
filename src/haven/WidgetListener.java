package haven;

public interface WidgetListener
{
    /**
     * @return true if base client should continue to process message, false otherwise
     */
    boolean uimsg(int id, String msg, Object... args);

    /**
     * @return true if base client should continue to process message, false otherwise
     */
    boolean destroy(); 
}

package haven;

public interface ExtendoFactory
{
    /**
     * @return true if base client should continue to process message false otherwise
     */
    boolean newWidget(int id, String type, Coord c, int parent, Object... args);
}

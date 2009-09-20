package haven;

public interface ExtendoFactory
{
    boolean newWidget(int id, String type, Coord c, int parent, Object... args);
}

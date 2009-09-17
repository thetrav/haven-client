package haven;

public interface NewWidgetListener
{
    boolean newWidget(int id, String type, Coord c, int parent, Object... args);
}

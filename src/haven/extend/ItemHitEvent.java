package haven.extend;

import haven.Coord;
import haven.Gob;

public class ItemHitEvent
{
    public Coord c;
    public Coord mc;
    public int button;
    public int modflags;
    public Gob hit;

    public ItemHitEvent(Coord c, Coord mc, int button, int modflags, Gob hit)
    {
        this.c = c;
        this.mc = mc;
        this.button = button;
        this.modflags = modflags;
        this.hit = hit;
    }
}

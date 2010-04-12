package haven.trav;

import haven.Coord;
import haven.CustomConfig;
import haven.GOut;
import haven.Inventory;
import haven.ResCache;
import haven.Resource;

import java.awt.Color;
import java.awt.event.KeyEvent;

public class TravHudBelt
{
    public static final int BELT_SLOTS = 10;
    public static final Coord bc1 = new Coord(4, 4); // Belt 1 location start

    public static int activeBelt = 1;
    @SuppressWarnings("unchecked")
    Resource[][] belt = new Resource[BELT_SLOTS][BELT_SLOTS];

    private final TravHud hud;
    
    public TravHudBelt(TravHud travHud)
    {
        hud = travHud;
    }

    public void draw(final GOut g)
    {
        // Draws the belt
        for (int i = 0; i < BELT_SLOTS; i++)
        {
            Coord c = beltc(i);// xlate(beltc(i), true);
            Coord x = c.add(Inventory.invsq.sz().add(-10, 0));
            g.image(Inventory.invsq, c);
            g.chcolor(156, 180, 158, 255);
            g.atext(Integer.toString((i + 1) % 10), c.add(Inventory.invsq.sz()), 1, 1);
            g.chcolor();
            Resource res = null;
            if (belt[activeBelt][i] != null)
                res = belt[activeBelt][i];
            if (res != null && !res.loading)
                g.image(res.layer(Resource.imgc).tex(), c.add(1, 1));
            g.chcolor(Color.BLACK);
            g.atext(Integer.toString(activeBelt), x, 1, 1);
            g.chcolor();
        }
    }

    public void initBelt()
    {
        if (CustomConfig.noChars)
            return;
        activeBelt = CustomConfig.activeCharacter.hudActiveBelt;
        synchronized (belt)
        {
            for (int i = 0; i < belt.length; i++)
            {
                for (int j = 0; j < belt[i].length; j++)
                {
                    if (CustomConfig.activeCharacter.hudBelt[i][j] != null)
                    {
                        belt[i][j] = Resource.load(CustomConfig.activeCharacter.hudBelt[i][j]);
                    }
                }
            }
        }
    }

    public boolean globType(char ch, KeyEvent ev, TravHud travSlenHud)
    {
        if ((((ch >= '1') && (ch <= '9')) || (ch == '0')) && ev.isAltDown())
        {
            activeBelt = ch - 48;
            CustomConfig.activeCharacter.hudActiveBelt = activeBelt;
            for (int i = 0; i < belt[activeBelt].length; i++)
            {
                if (belt[activeBelt][i] == null)
                {
                    travSlenHud.wdgmsg("setbelt", i, 0);
                    continue;
                }

                travSlenHud.wdgmsg("setbelt", i, belt[activeBelt][i].name);
            }
            return true;
        }
        else if (ch == '0')
        {
            if (belt[activeBelt][9] != null)
                travSlenHud.wdgmsg("belt", 9, 1, 0);
            return (true);
        }
        else if ((ch >= '1') && (ch <= '9'))
        {
            if (belt[activeBelt][ch - '1'] != null)
                travSlenHud.wdgmsg("belt", ch - '1', 1, 0);
            return (true);
        }
        return false;
    }

    private Coord beltc(int i)
    {
        return (bc1.add(i * (Inventory.invsq.sz().x + 2), 0));
    }

    public boolean mouseDown(Coord c, int button, TravHud travSlenHud)
    {
        int slot = beltslot(c);
        if (slot != -1)
        {
            travSlenHud.wdgmsg("belt", slot, button, travSlenHud.ui.modflags());
            return true;
        }
        return false;
    }

    private int beltslot(Coord c)
    {
        // c = xlate(c, false);
        int sw = Inventory.invsq.sz().x + 2;
        if ((c.x >= bc1.x) && (c.y >= bc1.y) && (c.y < bc1.y + Inventory.invsq.sz().y))
        {
            if ((c.x - bc1.x) / sw < 10)
            {
                if ((c.x - bc1.x) % sw < Inventory.invsq.sz().x)
                    return ((c.x - bc1.x) / sw);
            }
        }
        return (-1);
    }

    public boolean dropthing(Coord c, Object thing, TravHud travSlenHud)
    {
        int slot = beltslot(c);
        if (slot != -1)
        {
            if (CustomConfig.noChars)
            {
//                travSlenHud.error("You must restart the client to set and save your hotkeys");
                return true;
            }
            if (thing instanceof Resource)
            {
                Resource res = (Resource) thing;
                if (res.layer(Resource.action) != null)
                {
                    belt[activeBelt][slot] = res;
                    CustomConfig.activeCharacter.hudBelt[activeBelt][slot] = belt[activeBelt][slot].name;
                    travSlenHud.wdgmsg("setbelt", slot, res.name);
                    if (ResCache.global != null)
                        CustomConfig.saveSettings();
                    return (true);
                }
            }
        }
        return (false);
    }

    public boolean drop(Coord cc, Coord ul, TravHud travSlenHud)
    {
        int slot = beltslot(cc);
        if (slot != -1)
        {
            if (CustomConfig.noChars)
                travSlenHud.error("You must restart the client to set and save your hotkeys");
            else
                travSlenHud.wdgmsg("setbelt", slot, 0);
            return (true);
        }
        return (false);
    }
}

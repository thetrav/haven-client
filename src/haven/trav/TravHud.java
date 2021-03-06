package haven.trav;

import haven.Bootstrap;
import haven.Console;
import haven.ConsoleHost;
import haven.Coord;
import haven.CustomConfig;
import haven.DTarget;
import haven.DropTarget;
import haven.GOut;
import haven.HWindow;
import haven.MiniMap;
import haven.Resource;
import haven.Tex;
import haven.Widget;
import haven.WidgetFactory;

import java.awt.event.KeyEvent;
import java.util.TreeMap;

public class TravHud extends ConsoleHost implements DTarget, DropTarget, Console.Directory
{

    private static final Tex bg = Resource.loadtex("jlay/hudPanels");
    private static final Coord HUD_WIDGET_SIZE = bg.sz();
    private static final Coord MINI_MAP_COORD = new Coord(540, 40);
    public static final Coord MENU_GRID_COORD = MINI_MAP_COORD;

    private final HudTextWindows textWindows;
    private final TravHudButtons hudButtons;
    private final TravMenuGrid menuGrid;
    private final java.util.Map<String, Console.Command> cmdmap = new TreeMap<String, Console.Command>();

    private final TravHudBelt belt;

    static
    {
        Widget.addtype("slen", new WidgetFactory()
        {
            public Widget create(Coord c, Widget parent, Object[] args)
            {
                return (new TravHud(c, parent));
            }
        });
    }

    public TravHud(final Coord c, final Widget parent)
    {
        super(coord(), HUD_WIDGET_SIZE, parent);

        textWindows = new HudTextWindows(this);
        hudButtons = new TravHudButtons(this);
        menuGrid = new TravMenuGrid(this);

        if (!Bootstrap.STUBBED)
        {
            new MiniMap(MINI_MAP_COORD, new Coord(125, 125), this, ui.mainview);
        }

        belt = new TravHudBelt(this);
        belt.initBelt();
        cmdmap.put("afk", new Console.Command()
        {
            public void run(Console cons, String[] args)
            {
                wdgmsg("afk");
            }
        });
    }

    void selectMenu(final String string)
    {
        menuGrid.selectMenu(string);
    }

    @Override
    public void draw(final GOut g)
    {
        g.image(bg, new Coord(0, 0));
        belt.draw(g);
        super.draw(g);
    }

    @Override
    public void wdgmsg(Widget sender, String msg, Object... args)
    {
        if (hudButtons.wdgmsg(sender, msg, args))
            return;
        super.wdgmsg(sender, msg, args);
    }

    public static Coord coord()
    {
        return new Coord(CustomConfig.windowSize.x - ((CustomConfig.windowSize.x - HUD_WIDGET_SIZE.x) / 2), CustomConfig.windowSize.y).add(HUD_WIDGET_SIZE.inv());
    }

    public void addwnd(final HWindow hWindow)
    {
        textWindows.addWnd(hWindow);
    }

    public void remwnd(HWindow window)
    {
        textWindows.remwnd(window);
    }

    public void setawnd(HWindow window)
    {
        textWindows.setawnd(window);
    }

    public void updurgency(HWindow hWindow, int level)
    {
        textWindows.updurgency(hWindow, level);
    }

    public void hideGridMenu()
    {
        menuGrid.hideGridMenu();
    }

    @Override
    public boolean globtype(char ch, KeyEvent ev)
    {
        return belt.globType(ch, ev, this) ? true : super.globtype(ch, ev);
    }

    public boolean mousedown(Coord c, int button)
    {
        return belt.mouseDown(c, button, this) ? true : super.mousedown(c, button);
    }

    public boolean dropthing(Coord c, Object thing)
    {
        return belt.dropthing(c, thing, this);
    }

    public boolean drop(Coord cc, Coord ul)
    {
        return belt.drop(cc, ul, this);
    }

    @Override
    public void error(String msg)
    {
        System.out.println("error:" + msg);
    }

    @Override
    public boolean iteminteract(Coord cc, Coord ul)
    {
        return (false);
    }

    @Override
    public java.util.Map<String, Console.Command> findcmds()
    {
        return (cmdmap);
    }

}

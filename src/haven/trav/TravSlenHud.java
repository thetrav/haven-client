package haven.trav;

import haven.Bootstrap;
import haven.Button;
import haven.Console;
import haven.ConsoleHost;
import haven.Coord;
import haven.CustomConfig;
import haven.DTarget;
import haven.DropTarget;
import haven.GOut;
import haven.HWindow;
import haven.IButton;
import haven.MapView;
import haven.MenuGrid;
import haven.MiniMap;
import haven.Resource;
import haven.SlenChat;
import haven.Tex;
import haven.Utils;
import haven.Widget;
import haven.WidgetFactory;
import haven.Resource.AButton;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TravSlenHud extends Widget //ConsoleHost implements DTarget, DropTarget, Console.Directory
{
    private static final int TAB_Y = 50;
    private static final int TAB_X = 5;
    private static final int TAB_HEIGHT = 20;
    private static final int BUTTON_LEFT = 540;
    private static final int BUTTON_TOP = 3;
    // private static final int buttonRight = 90;
    private static final int BUTTON_IMG_WIDTH = 15;
    private static final int BUTTON_MARGIN = 3;
    private static final int BUTTON_WIDTH = BUTTON_IMG_WIDTH + BUTTON_MARGIN;
    private static final int CLAIM_BUTTON_OFFSET = 3;

    private static final Tex bg = Resource.loadtex("jlay/hudPanels");
    private static final Coord HUD_WIDGET_SIZE = bg.sz();
    private static final Coord MINI_MAP_COORD = new Coord(540, 40);
    public static final Coord MENU_GRID_COORD = MINI_MAP_COORD;

    private final IButton inventoryButton;
    private final IButton equipmentButton;
    private final IButton characterButton;
    private final IButton kinListButton;

    private List<HWindow> windows = new ArrayList<HWindow>();
    private final Map<HWindow, Button> windowButtons = new HashMap<HWindow, Button>();
    private HWindow activeWindow;

    private Resource selectedMenu = null;
    
//    private static final boolean USE_BELT = false;
//    private final SlenBelt slenBelt;

    public static final Color urgcols[] = { null, new Color(0, 128, 255), new Color(255, 128, 0), new Color(255, 0, 0), };

    static
    {
        Widget.addtype("slen", new WidgetFactory()
        {
            public Widget create(Coord c, Widget parent, Object[] args)
            {
                return (new TravSlenHud(c, parent));
            }
        });
    }

    public TravSlenHud(final Coord c, final Widget parent)
    {
        super(coord(), HUD_WIDGET_SIZE, parent);
        // Inventory button

        inventoryButton = new IButton(buttonCoord(2, 0), this, Resource.loadimg("jlay/sackButtonLight"), Resource.loadimg("jlay/sackButtonDark"));
        equipmentButton = new IButton(buttonCoord(3, 0), this, Resource.loadimg("jlay/armorButtonLight"), Resource.loadimg("jlay/armorButtonDark"));
        characterButton = new IButton(buttonCoord(4, 0), this, Resource.loadimg("jlay/faceButtonLight"), Resource.loadimg("jlay/faceButtonDark"));
        kinListButton = new IButton(buttonCoord(5, 0), this, Resource.loadimg("jlay/heartButtonLight"), Resource.loadimg("jlay/heartButtonDark"));

        new IButton(buttonCoord(0, 1), this, Resource.loadimg("jlay/hatButtonLight"), Resource.loadimg("jlay/hatButtonDark"))
        {
            @Override
            public void click()
            {
                selectMenu("Adventure");
            }
        };
        new IButton(buttonCoord(1, 1), this, Resource.loadimg("jlay/sunButtonLight"), Resource.loadimg("jlay/sunButtonDark"))
        {
            @Override
            public void click()
            {
                selectMenu("Government");
            }
        };
        new IButton(buttonCoord(2, 1), this, Resource.loadimg("jlay/swordButtonLight"), Resource.loadimg("jlay/swordButtonDark"))
        {
            @Override
            public void click()
            {
                selectMenu("Attack");
            }
        };
        new IButton(buttonCoord(3, 1), this, Resource.loadimg("jlay/handButtonLight"), Resource.loadimg("jlay/handButtonDark"))
        {
            @Override
            public void click()
            {
                selectMenu("Craft");
            }
        };
        new IButton(buttonCoord(4, 1), this, Resource.loadimg("jlay/hammerButtonLight"), Resource.loadimg("jlay/hammerButtonDark"))
        {
            @Override
            public void click()
            {
                selectMenu("Build");
            }

        };
        new IButton(buttonCoord(5, 1), this, Resource.loadimg("jlay/pathButtonLight"), Resource.loadimg("jlay/pathButtonDark"))
        {
            @Override
            public void click()
            {
                //                selectMenu("Travel");
                try
                {
                ui.newwidget(100, "im", new Coord(0,0), 1, "gfx/hud/meter/hp", Color.GREEN, new Integer(30));
                ui.newwidget(101, "im", new Coord(0,0), 1, "gfx/hud/meter/hngr", Color.RED, new Integer(35));
                ui.newwidget(102, "im", new Coord(0,0), 1, "gfx/hud/meter/nrj", Color.BLUE, new Integer(40));
                ui.newwidget(103, "im", new Coord(0,0), 1, "gfx/hud/meter/auth", Color.YELLOW, new Integer(50));
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        };

        final Coord villageBaseCoord = buttonCoord(6, 0);
        final Coord villageButtonCoord = new Coord(villageBaseCoord.x + CLAIM_BUTTON_OFFSET, villageBaseCoord.y);
        // Village claims button
        new IButton(villageButtonCoord, this, Resource.loadimg("jlay/flagButtonLight"), Resource.loadimg("jlay/flagButtonDark"))
        {
            boolean v = false;

            public void click()
            {
                if (!Bootstrap.STUBBED)
                {
                    MapView mv = findMapView();
                    if (v)
                    {
                        mv.disol(2, 3);
                        v = false;
                    }
                    else
                    {
                        mv.enol(2, 3);
                        v = true;
                    }
                }
                else
                {
                    System.out.println("village claim button clicked");
                }
            }
        };

        final Coord totemBaseCoord = buttonCoord(6, 1);
        final Coord totemButtonCoord = new Coord(totemBaseCoord.x + CLAIM_BUTTON_OFFSET, totemBaseCoord.y);
        // Totem claim button
        new IButton(totemButtonCoord, this, Resource.loadimg("jlay/skullButtonLight"), Resource.loadimg("jlay/skullButtonDark"))
        {
            private boolean v = false;

            public void click()
            {
                if (!Bootstrap.STUBBED)
                {
                    MapView mv = findMapView();
                    if (v)
                    {
                        mv.disol(0, 1);
                        v = false;
                    }
                    else
                    {
                        mv.enol(0, 1);
                        v = true;
                    }
                }
                else
                {
                    System.out.println("Totem claim button clicked");
                }
            }
        };

        if (!Bootstrap.STUBBED)
        {
            new MiniMap(MINI_MAP_COORD, new Coord(125, 125), this, ui.mainview);
        }
//
//        // belt
//        if (USE_BELT)
//        {
//            slenBelt = new SlenBelt();
//            slenBelt.initBelt();
//        }
//        else
//        {
//            slenBelt = null;
//        }
    }

    private MapView findMapView()
    {
        return ui.root.findchild(MapView.class);
    }

    private void selectMenu(final String string)
    {
        final Resource menu = findMenu(string);
        if (menu != null)
        {
            final MenuGrid menuGrid = findMenuGrid();
            if (menuGrid.visible && menu == selectedMenu)
            {
                hideGridMenu();
            }
            else
            {
                useGridMenu(menu);
            }
        }
    }

    private MenuGrid findMenuGrid()
    {
        return ui.root.findchild(MenuGrid.class);
    }

    public void useGridMenu(Resource menu)
    {
        System.out.println("showing menu");
        final MenuGrid menuGrid = findMenuGrid();
        menuGrid.use(menu);
        menuGrid.show();
        selectedMenu = menu;
    }

    public void hideGridMenu()
    {
        System.out.println("hiding menu");
        findMenuGrid().hide();
        selectedMenu = null;
    }

    private Resource findMenu(String string)
    {
        for (Resource r : ui.sess.glob.paginae)
        {
            Resource r1 = checkMenu(r, string);
            if (r1 != null)
            {
                return r1;
            }
        }
        return null;
    }

    private Resource checkMenu(Resource r, String string)
    {
        final AButton button = r.layer(AButton.class);
        if (button.name.equals(string))
        {
            return r;
        }
        if (button.parent != null)
        {
            return checkMenu(button.parent, string);
        }
        return null;
    }

    private Coord buttonCoord(int column, int row)
    {
        int x = BUTTON_LEFT + column * BUTTON_WIDTH;
        int y = BUTTON_TOP + row * BUTTON_WIDTH;
        return new Coord(x, y);
    }

    @Override
    public void draw(final GOut g)
    {
        g.image(bg, new Coord(0, 0));
//        if (USE_BELT)
//            slenBelt.draw(g);
        super.draw(g);
    }

//    @Override
//    public boolean globtype(char ch, KeyEvent ev)
//    {
//        if (USE_BELT)
//        {
//            return slenBelt.globType(ch, ev, this) ? true : super.globtype(ch, ev);
//        }
//        else
//        {
//            return super.globtype(ch, ev);
//        }
//    }

    @Override
    public void wdgmsg(Widget sender, String msg, Object... args)
    {
        if (sender == inventoryButton)
        {
            wdgmsg("inv");
            return;
        }
        else if (sender == equipmentButton)
        {
            wdgmsg("equ");
            return;
        }
        else if (sender == characterButton)
        {
            wdgmsg("chr");
            return;
        }
        else if (sender == kinListButton)
        {
            wdgmsg("bud");
            return;
        }
        super.wdgmsg(sender, msg, args);
    }

    private static Coord coord()
    {
        return new Coord(CustomConfig.windowSize.x - ((CustomConfig.windowSize.x - HUD_WIDGET_SIZE.x) / 2), CustomConfig.windowSize.y).add(HUD_WIDGET_SIZE.inv());
    }

    public void addwnd(final HWindow hWindow)
    {
        windows.add(hWindow);
        final Button wndButton = new Button(new Coord(TAB_X, TAB_Y), 100, this, hWindow.title)
        {
            public void click()
            {
                setawnd(hWindow);
            }
        };
        windowButtons.put(hWindow, wndButton);
        refreshButtons();
    }

    private void refreshButtons()
    {
        if (windows.size() >= 5)
        {
            // scrolling required
        }
        else
        {
            // used to do an offset to put buttons at the bottom
        }

        // set scroll up and down buttons invisible
        // sub.visible = sdb.visible = false;
        for (Button b : windowButtons.values())
        {
            b.visible = false;
        }
        for (int i = 0; i < 5; i++)
        {
            if (windows.size() > i)
            {
                HWindow w = windows.get(i);
                Button b = windowButtons.get(w);
                b.visible = true;
                b.c = new Coord(b.c.x, TAB_Y + (i * TAB_HEIGHT));
            }
        }
    }

    public void remwnd(HWindow window)
    {
        if (window == activeWindow)
        {
            int i = windows.indexOf(window);
            if (windows.size() == 1)
                setawnd(null);
            else if (i < 0)
                setawnd(windows.get(0));
            else if (i >= windows.size() - 1)
                setawnd(windows.get(i - 1));
            else
                setawnd(windows.get(i + 1));
        }
        windows.remove(window);
        ui.destroy(windowButtons.get(window));
        windowButtons.remove(window);
        refreshButtons();
    }

    public void updurgency(HWindow wnd, int level)
    {
        if ((wnd == activeWindow))
            level = -1;
        if (level == -1)
        {
            if (wnd.urgent == 0)
                return;
            wnd.urgent = 0;
        }
        else
        {
            if (wnd.urgent >= level)
                return;
            wnd.urgent = level;
        }
        Button b = windowButtons.get(wnd);
        if (urgcols[wnd.urgent] != null)
            b.change(wnd.title, urgcols[wnd.urgent]);
        else
            b.change(wnd.title);
        int max = 0;
        for (HWindow w : windows)
        {
            if (w.urgent > max)
                max = w.urgent;
        }
    }

    public void setawnd(HWindow window)
    {
        // Hide the current active window
        if (activeWindow != null && activeWindow != window)
        {
            activeWindow.hide();
        }
        // Some windows have special toggles that act when the button is pressed
        // twice
        if (activeWindow == window)
        {
            // IRC SlenChat userlist toggle
            if (activeWindow.getClass().getName().equalsIgnoreCase(SlenChat.class.getName()))
            {
                if (((SlenChat) activeWindow).userList != null)
                    ((SlenChat) activeWindow).userList.toggle();
            }
            return;
        }
        // Make the specified window be the active window, set the appropriate
        // coloring, and show
        activeWindow = window;

        activeWindow.show();
        updurgency(window, -1);
    }

//    public boolean mousedown(Coord c, int button)
//    {
//        if (USE_BELT)
//            return slenBelt.mouseDown(c, button, this) ? true : super.mousedown(c, button);
//        else
//            return false;
//    }
//
//    public boolean dropthing(Coord c, Object thing)
//    {
//
//        if (USE_BELT)
//            return slenBelt.dropthing(c, thing, this);
//        else
//            return false;
//    }
//
//    public boolean drop(Coord cc, Coord ul)
//    {
//        if (USE_BELT)
//            return slenBelt.drop(cc, ul, this);
//        else
//            return false;
//    }
//
//    @Override
//    public void error(String msg)
//    {
//        System.out.println("error:" + msg);
//    }
//
//    @Override
//    public boolean iteminteract(Coord cc, Coord ul)
//    {
//        return (false);
//    }
//
//    private Map<String, Console.Command> cmdmap = new TreeMap<String, Console.Command>();
//    {
//        cmdmap.put("afk", new Console.Command()
//        {
//            public void run(Console cons, String[] args)
//            {
//                wdgmsg("afk");
//            }
//        });
//    }
//
//    @Override
//    public Map<String, Console.Command> findcmds()
//    {
//        return (cmdmap);
//    }

}

package haven.trav;

import haven.Button;
import haven.Coord;
import haven.HWindow;
import haven.Resource;
import haven.SlenChat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HudTextWindows
{
    private static final int TAB_Y = 40;
    private static final int TAB_X = 5;
    private static final int TAB_HEIGHT = 18;
    public static final Color urgcols[] = { null, new Color(0, 128, 255), new Color(255, 128, 0), new Color(255, 0, 0), };
    private static final int SCROLL_DOWN_Y_OFFSET = 108;

    private final TravHud parent;
    private List<HWindow> windows = new ArrayList<HWindow>();
    private final Map<HWindow, Button> windowButtons = new HashMap<HWindow, Button>();
    private HWindow activeWindow;
    private int scrollIndex = 0;
    private final Button scrollUpButton;
    private final Button scrollDownButton;
    

    public HudTextWindows(TravHud travHud)
    {
        parent = travHud;
        
        scrollUpButton = new Button(new Coord(TAB_X, TAB_Y), 100, parent, Resource.loadimg("gfx/hud/slen/sau")) {
            public void click() {
                scrollUp();
            }
            };
        scrollDownButton = new Button(new Coord(TAB_X, TAB_Y+SCROLL_DOWN_Y_OFFSET), 100, parent, Resource.loadimg("gfx/hud/slen/sad")) {
            public void click() {
                scrollDown();
            }
            };
    }
    
    public void addWnd(final HWindow hWindow)
    {
        windows.add(hWindow);
        final Button wndButton = new Button(new Coord(TAB_X, TAB_Y), 100, parent, hWindow.title)
        {
            public void click()
            {
                setawnd(hWindow);
            }
        };
        windowButtons.put(hWindow, wndButton);
        refreshButtons();
    }
    
    private void scrollUp()
    {
        scrollIndex--;
        refreshButtons();
    }
    
    private void scrollDown()
    {
        scrollIndex++;
        refreshButtons();
    }

    private void refreshScrollButtons()
    {
        if(scrollIndex == 0)
        {
            scrollUpButton.hide();
        }
        else
        {
            scrollUpButton.show();
        }
        if(scrollIndex == windowButtons.size()-6)
        {
            scrollDownButton.hide();
        }
        else
        {
            scrollDownButton.show();
        }
    }
    
    private void hideScrollButtons()
    {
        scrollUpButton.hide();
        scrollDownButton.hide();
        scrollIndex = 0;
    }
    
    private void refreshButtons()
    {
        if (windows.size() >= 5)
        {
            refreshScrollButtons();
        }
        else
        {
            hideScrollButtons();
        }

        for (Button b : windowButtons.values())
        {
            b.visible = false;
        }
        for (int i = scrollIndex; i < 5; i++)
        {
            if (windows.size() > i)
            {
                HWindow w = windows.get(i);
                Button b = windowButtons.get(w);
                b.visible = true;
                b.c = new Coord(b.c.x, TAB_Y + ((i+1) * TAB_HEIGHT));//+1 for the scroll button
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
        parent.ui.destroy(windowButtons.get(window));
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

}

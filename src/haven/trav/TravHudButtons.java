package haven.trav;

import java.awt.Color;

import haven.Bootstrap;
import haven.Coord;
import haven.IButton;
import haven.MapView;
import haven.Resource;
import haven.Widget;

public class TravHudButtons
{
    private static final int BUTTON_LEFT = 540;
    private static final int BUTTON_TOP = 3;
    // private static final int buttonRight = 90;
    private static final int BUTTON_IMG_WIDTH = 15;
    private static final int BUTTON_MARGIN = 3;
    private static final int BUTTON_WIDTH = BUTTON_IMG_WIDTH + BUTTON_MARGIN;
    private static final int CLAIM_BUTTON_OFFSET = 3;

    private final IButton inventoryButton;
    private final IButton equipmentButton;
    private final IButton characterButton;
    private final IButton kinListButton;


    private final TravHud hud;

    public TravHudButtons(TravHud aTravHud)
    {
        hud = aTravHud;
     // Inventory button
        inventoryButton = new IButton(buttonCoord(2, 0), hud, Resource.loadimg("jlay/sackButtonLight"), Resource.loadimg("jlay/sackButtonDark"));
        equipmentButton = new IButton(buttonCoord(3, 0), hud, Resource.loadimg("jlay/armorButtonLight"), Resource.loadimg("jlay/armorButtonDark"));
        characterButton = new IButton(buttonCoord(4, 0), hud, Resource.loadimg("jlay/faceButtonLight"), Resource.loadimg("jlay/faceButtonDark"));
        kinListButton = new IButton(buttonCoord(5, 0), hud, Resource.loadimg("jlay/heartButtonLight"), Resource.loadimg("jlay/heartButtonDark"));

        mkButton(buttonCoord(0, 1), hud, "hat", "Adventure");
        mkButton(buttonCoord(1, 1), hud, "sun", "Government");
        mkButton(buttonCoord(2, 1), hud, "sword", "Attack");
        mkButton(buttonCoord(3, 1), hud, "hand", "Craft");
        mkButton(buttonCoord(4, 1), hud, "hammer", "Build");
        mkButton(buttonCoord(5, 1), hud, "path", "Travel");

        mkOverlayButton(0, "flag", 2, 3);
        mkOverlayButton(1, "skull", 0, 1);
    }


    private void mkOverlayButton(final int row, final String imgName, final int enolParam1, final int enolParam2)
    {
        final Coord baseCoord = buttonCoord(6, row);
        final Coord buttonCoord = new Coord(baseCoord.x + CLAIM_BUTTON_OFFSET, baseCoord.y);
        // Village claims button
        new IButton(buttonCoord, hud, Resource.loadimg("jlay/"+imgName+"ButtonLight"), Resource.loadimg("jlay/"+imgName+"ButtonDark"))
        {
            boolean v = false;

            public void click()
            {
                if (!Bootstrap.STUBBED)
                {
                    MapView mv = findMapView();
                    if (v)
                    {
                        mv.disol(enolParam1, enolParam2);
                        v = false;
                    }
                    else
                    {
                        mv.enol(enolParam1, enolParam2);
                        v = true;
                    }
                }
            }
        };
        
    }


    private void mkButton(final Coord buttonCoord, final TravHud hud2, final String buttonImg, final String menuName)
    {
        new IButton(buttonCoord, hud2, Resource.loadimg("jlay/"+buttonImg+"ButtonLight"), Resource.loadimg("jlay/"+buttonImg+"ButtonDark"))
        {
            @Override
            public void click()
            {
                hud2.selectMenu(menuName);
            }
        };
    }


    private Coord buttonCoord(int column, int row)
    {
        int x = BUTTON_LEFT + column * BUTTON_WIDTH;
        int y = BUTTON_TOP + row * BUTTON_WIDTH;
        return new Coord(x, y);
    }


    public boolean wdgmsg(Widget sender, String msg, Object ... args)
    {
        if (sender == inventoryButton)
        {
            hud.wdgmsg("inv");
            return true;
        }
        else if (sender == equipmentButton)
        {
            hud.wdgmsg("equ");
            return true;
        }
        else if (sender == characterButton)
        {
            hud.wdgmsg("chr");
            return true;
        }
        else if (sender == kinListButton)
        {
            hud.wdgmsg("bud");
            return true;
        }
        return false;
    }

    private MapView findMapView()
    {
        return hud.ui.root.findchild(MapView.class);
    }
}

package haven.trav;

import haven.MenuGrid;
import haven.Resource;
import haven.Resource.AButton;

public class TravMenuGrid
{
    private final TravHud hud;
    private Resource selectedMenu = null;

    public TravMenuGrid(TravHud travHud)
    {
        hud = travHud;
    }

    public void selectMenu(String menuName)
    {
        final Resource menu = findMenu(menuName);
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
        return hud.ui.root.findchild(MenuGrid.class);
    }
    public void useGridMenu(Resource menu)
    {
        final MenuGrid menuGrid = findMenuGrid();
        menuGrid.use(menu);
        menuGrid.show();
        selectedMenu = menu;
    }

    public void hideGridMenu()
    {
        findMenuGrid().hide();
        selectedMenu = null;
    }

    private Resource findMenu(String string)
    {
        for (Resource r : hud.ui.sess.glob.paginae)
        {
            final Resource r1 = checkMenu(r, string);
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
}

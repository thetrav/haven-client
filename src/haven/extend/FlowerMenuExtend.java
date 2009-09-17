package haven.extend;

import org.apache.log4j.Logger;

import haven.Coord;
import haven.FlowerMenu;
import haven.IBox;
import haven.NewWidgetListener;
import haven.Widget;
import haven.WidgetFactory;

public class FlowerMenuExtend implements NewWidgetListener
{
    private boolean first = true;
    private static final Logger LOG = Logger.getLogger(FlowerMenuExtend.class);
    public static final String NEW_WIDGET_MESSAGE_CODE = "sm";

    @Override
    public boolean newWidget(int id, String type, Coord c, int parent, Object... args)
    {
        //hook into the widget construction factory
        if(first)
        {
            Widget.addtype("sm", new WidgetFactory() {
                public Widget create(Coord c, Widget parent, Object[] args) {
                    LOG.debug("creating flower menu");
                    if((c.x == -1) && (c.y == -1))
                    c = parent.ui.lcc;
                    String[] opts = new String[args.length];
                    for(int i = 0; i < args.length; i++)
                    {
                        opts[i] = (String)args[i];
                        LOG.debug("opt["+i+"]: "+opts[i]);
                    }
                    return(new FlowerMenu(c, parent, opts));
                }
                });
            //this should already be set, no need to repeat
//            FlowerMenu.pbox = new IBox("gfx/hud", "tl", "tr", "bl", "br", "extvl", "extvr", "extht", "exthb");
            first = false;
        }
        return false;
    }
}

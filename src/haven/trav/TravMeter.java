package haven.trav;

import haven.Coord;
import haven.GOut;
import haven.Resource;
import haven.Tex;
import haven.Widget;
import haven.WidgetFactory;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class TravMeter extends Widget
{
    static Coord OFFSET = new Coord(13, 7);
    static Coord FULL_SIZE = new Coord(63, 18);
    static Coord METER_SIZE = new Coord(49, 4);
    Resource background;
    List<Meter> meters;
    
    private final static java.util.Map<String, CustomMeterProps> CUSTOM_METERS = new HashMap<String, CustomMeterProps>();
    static
    {
        
        CUSTOM_METERS.put("gfx/hud/meter/auth", new CustomMeterProps(meterCoord(0,0), "jlay/handMeter"));
        CUSTOM_METERS.put("gfx/hud/meter/hp", new CustomMeterProps(meterCoord(1,0), "jlay/heartMeter"));
        CUSTOM_METERS.put("gfx/hud/meter/nrj", new CustomMeterProps(meterCoord(1,1), "jlay/bagMeter"));
        CUSTOM_METERS.put("gfx/hud/meter/hngr", new CustomMeterProps(meterCoord(1,2), "jlay/fishMeter"));
        
        //dirty hack way to hide the happy meter
        CUSTOM_METERS.put("gfx/hud/meter/happy", new CustomMeterProps(new Coord(-1000,-1000), "jlay/fishMeter"));
    }
    
    static Coord meterCoord(int row, int column)
    {
        final int meterY = TravHud.coord().y + 3;
        final int meterRowSize = 18;
        final int meterX = TravHud.coord().x + 340;
        final int meterColumnSize = 65;
        return new Coord(meterX + column*meterColumnSize, meterY + row*meterRowSize);
    }

    static
    {
        Widget.addtype("im", new WidgetFactory()
        {
            public Widget create(Coord c, Widget parent, Object[] args)
            {
                final String resource = (String) args[0];
                final CustomMeterProps customMeter = CUSTOM_METERS.get(resource);
                if(customMeter == null)
                {
                    return createMeter(c, parent, args, resource);
                }
                else
                {
                    return createMeter(customMeter.c, parent, args, customMeter.resource);
                }
            }

            private Widget createMeter(Coord c, Widget parent, Object[] args, final String resource)
            {
                System.out.println("creating meter"+resource+" at"+c);
                final Resource bg = Resource.load(resource);
                List<Meter> meters = new LinkedList<Meter>();
                for (int i = 1; i < args.length; i += 2)
                    meters.add(new Meter((Color) args[i], (Integer) args[i + 1]));
                return (new TravMeter(c, parent, bg, meters));
            }
        });
    }

    public TravMeter(Coord c, Widget parent, Resource bg, List<Meter> meters)
    {
        super(c, FULL_SIZE, parent);
        this.background = bg;
        this.meters = meters;
    }

    public static class Meter
    {
        Color c;
        int a;

        public Meter(Color c, int a)
        {
            this.c = c;
            this.a = a;
        }
    }

    public void draw(GOut g)
    {
        if (!background.loading)
        {
            Tex bg = this.background.layer(Resource.imgc).tex();
            g.chcolor(0, 0, 0, 255);
            g.frect(OFFSET, METER_SIZE);
            g.chcolor();
            for (Meter m : meters)
            {
                int w = METER_SIZE.x;
                w = (w * m.a) / 100;
                g.chcolor(m.c);
                g.frect(OFFSET, new Coord(w, METER_SIZE.y));
            }
            g.chcolor();
            g.image(bg, Coord.z);
        }
    }

    public void uimsg(String msg, Object... args)
    {
        if (msg == "set")
        {
            List<Meter> meters = new LinkedList<Meter>();
            for (int i = 0; i < args.length; i += 2)
                meters.add(new Meter((Color) args[i], (Integer) args[i + 1]));
            this.meters = meters;
        }
        else if (msg == "tt")
        {
            tooltip = args[0];
        }
        else
        {
            super.uimsg(msg, args);
        }
    }
}

class CustomMeterProps
{
    public CustomMeterProps(Coord c, String r)
    {
        this.c = c;
        this.resource = r;
    }
    public Coord c;
    public String resource;
}
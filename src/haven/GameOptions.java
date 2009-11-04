/**
 * @(#)Menu.java
 *
 *
 * @author 
 * @version 1.00 2009/10/23
 */
package haven;
import java.awt.Color;
public class GameOptions extends Window{
	
	static {
	Widget.addtype("gopts", new WidgetFactory() {
		public Widget create(Coord c, Widget parent, Object[] args) {
		    if(args.length < 2)
			return(new Window(c, (Coord)args[0], parent, null));
		    else
			return(new Window(c, (Coord)args[0], parent, (String)args[1]));
		}
	    });
	    wbox = new IBox("gfx/hud", "tl", "tr", "bl", "br", "extvl", "extvr", "extht", "exthb");
    }
    
	Text sfxVol = Text.render("SFX Vol:", Color.WHITE.darker());
	Text serverLabel = Text.render("Server:", Color.WHITE);
	Text chnlLabel = Text.render("Channels:", Color.WHITE);
	TextEntry serverAddress;
	TextEntry channelList;
	FillBox sfxVolBar;
	
    public GameOptions(Widget parent) {
    	super (CustomConfig.windowSize.div(2).add(-200,-200), Coord.z.add(200,200), parent, "Game Options", true);
    	
    	//	SFX volume bar
    	sfxVolBar = new FillBox(Coord.z.add(sfxVol.sz().x + 5, 0), Coord.z.add(120,20), CustomConfig.sfxVol, this);
    	
    	//	Server entry
    	serverAddress = new TextEntry(Coord.z.add(sfxVol.sz().x + 5, 30), Coord.z.add(120, 15),
    					 this, CustomConfig.ircServerAddress);
    	//	Channel list entry
    	channelList = new TextEntry(Coord.z.add(sfxVol.sz().x + 5, 50), Coord.z.add(120, 15),
    					 this, CustomConfig.ircChannelList);
    	
    	ui.bind(sfxVolBar, CustomConfig.wdgtID++);
    }
    public void draw(GOut g)
    {
    	super.draw(g);
    	g.image(sfxVol.img ,Coord.z.add(10,20));	//	SFX Volume
    	g.image(serverLabel.img, Coord.z.add(10,50));	//	Server Address
    	g.image(chnlLabel.img, Coord.z.add(10,70));	//	Channel List
    }
    public void wdgmsg(Widget sender, String msg, Object... args) {
		if(sender == cbtn)
		{
			toggle();
			return;
		} else if(sender == sfxVolBar && msg == "change")
		{
			CustomConfig.sfxVol = args[0] != null ? ((Integer)args[0]).intValue() : CustomConfig.sfxVol;
			return;
		}
		super.wdgmsg(sender, msg, args);
	}
	public void toggle()
	{
		CustomConfig.ircServerAddress = serverAddress.text;
		CustomConfig.ircChannelList = channelList.text;
		CustomConfig.saveSettings();
		super.toggle();
	}
}
class FillBox extends Widget
    {
    	IBox borders = new IBox("gfx/hud", "tl", "tr", "bl", "br", "extvl", "extvr", "extht", "exthb");
    	protected int value;
    	boolean mouseDown = false;
    	public FillBox(Coord loc, Coord size, int startValue, Widget parent)
    	{
    		super(loc, size, parent);
    		value = startValue;
    	}
    	public void draw(GOut g)
    	{
    		borders.draw(g, Coord.z, sz);
    		g.frect(Coord.z.add(10,6), new Coord(value, sz.y-12));
    	}
    	public boolean mousedown(Coord c, int button)
    	{
    		if(button == 1)
    		{
    			mouseDown = true;
    			ui.grabmouse(this);
    			return true;
    		}
    		return super.mousedown(c, button);
    	}
    	public boolean mouseup(Coord c, int button)
    	{
    		if(button == 1 && mouseDown)
    		{
    			mouseDown = false;
    			ui.grabmouse(null);
    			return true;
    		}
    		return super.mouseup(c, button);
    	}
    	public void mousemove(Coord c)
    	{
    		if(mouseDown)
    		{
    			if(c.x > 10 && c.x < 110)
    				value = (c.x-10)%100;
    			wdgmsg(this, "change", Integer.valueOf(value)); 
    			return;
    		}
    		super.mousemove(c);
    	}
    }
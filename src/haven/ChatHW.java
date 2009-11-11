/*
 *  This file is part of the Haven & Hearth game client.
 *  Copyright (C) 2009 Fredrik Tolf <fredrik@dolda2000.com>, and
 *                     Björn Johannessen <johannessen.bjorn@gmail.com>
 *
 *  Redistribution and/or modification of this file is subject to the
 *  terms of the GNU Lesser General Public License, version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Other parts of this source tree adhere to other copying
 *  rights. Please see the file `COPYING' in the root directory of the
 *  source tree for details.
 *
 *  A copy the GNU Lesser General Public License is distributed along
 *  with the source tree of which this file is a part in the file
 *  `doc/LPGL-3'. If it is missing for any reason, please see the Free
 *  Software Foundation's website at <http://www.fsf.org/>, or write
 *  to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA
 */

package haven;

import java.awt.Color;

public class ChatHW extends HWindow {
    TextEntry in;
    ExtTextlog out;

    static {
	Widget.addtype("slenchat", new WidgetFactory() {
		public Widget create(Coord c, Widget parent, Object[] args) {
		    String t = (String)args[0];
		    boolean cl = false;
		    if(args.length > 1)
			cl = (Integer)args[1] != 0;
		    return(new ChatHW(parent, t, cl));
		}
	    });
    }

    public ChatHW(Widget parent, String title, boolean closable) {
	super(parent, title, closable);
	in = new TextEntry(new Coord(0, sz.y - 15), new Coord(sz.x, 15), this, "");
	in.canactivate = true;
	out = new ExtTextlog(Coord.z, new Coord(sz.x, sz.y - 15), this);
	if(closable) cbtn.raise();
    }

    public void uimsg(String msg, Object... args) {
	if(msg == "log") {
	    Color col = null;
	    if(args.length > 1)
		col = (Color)args[1];
	    out.append((String)args[0], col);
	    flashWindow(this);
	} else {
	    super.uimsg(msg, args);
	}
    }

    public void wdgmsg(Widget sender, String msg, Object... args) {
	if(sender == in) {
	    if(msg == "activate") {
		wdgmsg("msg", args[0]);
		in.settext("");
		return;
	    }
	}
	super.wdgmsg(sender, msg, args);
    }
    public boolean mousewheel(Coord c, int amount)
    {
    	return(out.mousewheel(c, amount));
    }
    //	Changes the button text color to red if the window is not currently visible
	protected boolean flashWindow(ChatHW wnd)
	{
		//	Searches for an inactive window to flash
		for(Button b : ((SlenHud)parent).btns.values())
		{
			if(wnd.title.equalsIgnoreCase(b.text.text)
				&& !wnd.visible)
			{
				b.isFlashing = true;
				b.changeText(b.text.text, Color.RED);
				return true;
			}
		}
		//	No inactive window found
		return false;
	}
}

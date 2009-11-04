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
import java.awt.event.KeyEvent;
import java.util.*;

public class Listbox extends Widget {
    public List<Option> opts;
    public Option chosen;
    Scrollbar scrollBar;
    int height;
	
    static {
	Widget.addtype("lb", new WidgetFactory() {
		public Widget create(Coord c, Widget parent, Object[] args) {
		    List<Option> opts = new LinkedList<Option>();
		    for(int i = 1; i < args.length; i += 2)
			opts.add(new Option((String)args[i], (String)args[i + 1]));
		    return(new Listbox(c, (Coord)args[0], parent, opts));
		}
	    });
    }

    public static class Option {
		public String name, disp;
		int y1, y2;
		
		public Option(String name, String disp) {
		    this.name = name;
		    this.disp = disp;
		}
		public boolean containsString(String data)
		{
			return (data.equals(name) || data.equals(disp));
		}
    }
	
    public void draw(GOut g) {
		for(int i = 0; i < height && scrollBar != null; i++) {
			Color c;
			if(i + scrollBar.val >= opts.size())
			    continue;
			Option b = opts.get(i + scrollBar.val);
			if(b.equals(chosen)) {
				c = FlowerMenu.pink;
		    }
		    else {
				c = Color.BLACK;
		    }
			g.image((Text.render(b.disp, c)).tex(), new Coord(0,i*10));
		}
		super.draw(g);
    }
	
    public Listbox(Coord c, Coord sz, Widget parent, List<Option> opts) {
	super(c, sz, parent);
	this.opts = opts;
	height = sz.y / 10;
	scrollBar = new Scrollbar(Coord.z.add(sz.x,0), sz.y, this, 0, 50) {
		public void changed() {}
	};
	chosen = !opts.isEmpty() ? opts.get(0) : null;
	setcanfocus(true);
    }
	
    static List<Option> makelist(Option[] opts) {
	List<Option> ol = new LinkedList<Option>();
	for(Option o : opts)
	    ol.add(o);
	return(ol);
    }
	
    public Listbox(Coord c, Coord sz, Widget parent, Option[] opts) {
	this(c, sz, parent, makelist(opts));
    }
	
    public void sendchosen() {
	wdgmsg("chose", chosen.name);
    }
	
    public boolean mousedown(Coord c, int button) {
		int i = 0;
		if(button == 1 && c.x < sz.x-25) {
			int sel = (c.y / 10) + scrollBar.val;
			if(sel >= opts.size()){
				sel = -1;
			}
			if(sel < 0){
				chosen = null;
			} else {
				chosen = opts.get(sel);
			}
		    changed(chosen);
		    return(true);
		}
		if(scrollBar.mousedown(c, button))	return true;

		return(false);
    }
	
    public boolean keydown(KeyEvent e) { 
    	return parent.keydown(e);
    }
    public void changed(Option changed)
    {}
}

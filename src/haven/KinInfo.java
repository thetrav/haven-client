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

public class KinInfo extends GAttrib {
    public static final Text.Foundry nfnd = new Text.Foundry("SansSerif", 10);
    public String name;
    public int group, type;
    public long seen = 0;
    private Tex rnm = null;
    
    public KinInfo(Gob g, String name, int group, int type) {
	super(g);
	this.name = name;
	this.group = group;
	this.type = type;
    }
    
    public void update(String name, int group, int type) {
	this.name = name;
	this.group = group;
	this.type = type;
	rnm = null;
    }
    
    public Tex rendered() {
	if(rnm == null)
	    rnm = new TexI(Utils.outline2(nfnd.render(name, BuddyWnd.gc[group]).img, Utils.contrast(BuddyWnd.gc[group])));
	return(rnm);
    }
}

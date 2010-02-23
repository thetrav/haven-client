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

public class RemoteUI implements UI.Receiver {
    Session sess;
    UI ui;

    public RemoteUI(Session sess) {
	this.sess = sess;
	Widget.initbardas();
    }

    public void rcvmsg(int id, String name, Object... args) {
	Message msg = new Message(Message.RMSG_WDGMSG);
	msg.adduint16(id);
	msg.addstring(name);
	msg.addlist(args);
	sess.queuemsg(msg);
    }

    public void run(UI ui) throws InterruptedException {
	this.ui = ui;
	ui.setreceiver(this);
	while(sess.alive()) {
	    Message msg;
	    while((msg = sess.getuimsg()) != null) {
		if(msg.type == Message.RMSG_NEWWDG) {
		    int id = msg.uint16();
		    String type = msg.string();
		    Coord c = msg.coord();
		    int parent = msg.uint16();
		    Object[] args = msg.list();
		    if(type.equals("cnt")){
		    	args[0] = CustomConfig.windowSize;
		    }else if(type.equals("img") && args.length >= 1){
		    	if(((String)args[0]).equals("gfx/ccscr"))
		    		c = CustomConfig.windowCenter.add(-400, -300);
		    	if(((String)args[0]).equals("gfx/logo2"))
		    		c = CustomConfig.windowCenter.add(-415, -300);
		    }else if(type.equals("charlist") && args.length >= 1){
		    	c = CustomConfig.windowCenter.add(-380, -50);
		    }else if(type.equals("ibtn") && args.length >= 2){
		    	if(((String)args[0]).equals("gfx/hud/buttons/ncu") && ((String)args[1]).equals("gfx/hud/buttons/ncd")){
		    		c = CustomConfig.windowCenter.add(86, 214);
		    	}
		    }else if(type.equals("wnd") && c.x == 400 && c.y == 200){
		    	c = CustomConfig.windowCenter.add(0,-100);
		    }else if(type.equals("wnd") && args.length >= 2){
		    	c = 	CustomConfig.invCoord.x > 0 && CustomConfig.invCoord.y > 0
		    		&&	CustomConfig.invCoord.x < CustomConfig.windowSize.x-100 
		    		&&	CustomConfig.invCoord.y < CustomConfig.windowSize.y-100
		    		&&	((String)args[1]).equals("Inventory")
		    		?	CustomConfig.invCoord   : c;
		    }
	/*	    System.out.print("\nCREATE\tID: " + id + "\tType: " + type + "\tCoord:" + c + "\tParent: " + parent + "\tArgs: ");
		    for(int i = 0; i < args.length; i++)
		    	System.out.print("|" + i + "| " + args[i] + "\t");
	*/	    ui.newwidget(id, type, c, parent, args);
			if(CustomConfig.noChars){
			    Window warning = new Window(CustomConfig.windowCenter.add(0, -20), new Coord(200,40), ui.root, "WARNING!", false){
			    		public boolean mousedown(Coord c, int btn){return true;}
			    		public boolean mouseup(Coord c, int btn){return true;}
			    		public boolean type(char key, java.awt.event.KeyEvent e){return true;}
			    	};
			    new Label(new Coord(0,0), warning,"If you are creating your first character, exit");
			    new Label(new Coord(0,16), warning,"the spawning room and then restart the");
			    new Label(new Coord(0,32), warning,"client, otherwise it WILL NOT save any data.");
			    warning.pack();
		    }
		    
		} else if(msg.type == Message.RMSG_WDGMSG) {
		    int id = msg.uint16();
		    String type = msg.string();
		    Object[] args = msg.list();
	/*  		System.out.print("\nMSG\tID: " + id + " " + ui.widgets.get(new Integer(id)) + "\tType: " + type + "\tArgs: ");
		    for(int i = 0; i < args.length; i++)
		    	System.out.print("|" + i + "| " + args[i] + "\t");
	*/	    ui.uimsg(id, type, args);
	   
		} else if(msg.type == Message.RMSG_DSTWDG) {
		    int id = msg.uint16();
	//	    System.out.println("DESTROY" + '\t' + id + " " + ui.widgets.get(new Integer(id)));
		    if(ui.widgets.get(new Integer(id)) instanceof Window){
		    	Window wnd = (Window)ui.widgets.get(new Integer(id));
		    	if(wnd.cap.text.equals("Inventory"))
		    		CustomConfig.invCoord = wnd.c;
		    }
		    ui.destroy(id);
		}
	    }
	    synchronized(sess) {
		sess.wait();
	    }

	}
    }
}

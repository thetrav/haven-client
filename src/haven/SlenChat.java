/**
 * @(#)SlenChat.java
 *
 *
 * @author 
 * @version 1.00 2009/10/15
 */

package haven;
import java.util.*;
import java.awt.event.KeyEvent;
import java.awt.Color;
import org.relayirc.util.*;
import org.relayirc.core.*;
import org.relayirc.chatengine.*;

public class SlenChat extends ChatHW implements IRCConnectionListener
    {
    	public String user;
    	public IRCConnection IRC;
    	public boolean initialized = false;
    	private String channel;
    	private SlenChat tSCWnd;
    	public UserList userList;
    	
    	public static class UserList extends Window
    	{
    		List<Listbox.Option> users = new ArrayList<Listbox.Option>();
    		Listbox out;
    		SlenChat owner;

    		static {
		    		Widget.addtype("ircuserlist", new WidgetFactory() {
		    			public Widget create(Coord c, Widget parent, Object[] args) {
		    				return (new UserList((SlenChat)parent));
		    			}
		    		});
    			}
	    		
	    		public UserList (SlenChat parent)
	    		{
	    			super(new Coord(10, 600), new Coord(125,parent.sz.y-10), parent.parent.parent, "Users", false);
	    			out = new Listbox(Coord.z, new Coord(100,105), this, users);
	    			owner = parent;
	    			ui.bind(this, 1500 + ((SlenHud)parent.parent).ircChannels.size());
	    			ui.bind(out, 2000 + ((SlenHud)parent.parent).ircChannels.size());
	    		}
	    		public void addUser(String user)
	    		{
	    			if(user != null)
	    			{
	    				users.add(new Listbox.Option(user, user));
	    			}
	    		}
	    		public void addUserList(String[] userList)
	    		{
	    			for(int i = 0; i < userList.length; i++)
	    			{
	    				addUser(userList[i]);
	    			}
	    		}
	    		public void rmvUser(String user)
	    		{
	    			if(user != null)
	    			{
	    				users.remove(new Listbox.Option(user, user));
	    			}
	    		}
	    		public void show()
	    		{
	    			visible = true;
	    		}
	    		public void hide()
	    		{
	    			visible = false;
	    		}
	    		public boolean keydown(KeyEvent e)
	    		{
	    			if(e.getKeyCode() == KeyEvent.VK_ENTER 
	    				&& out.chosen != null 
	    				&& out.hasfocus
	    				&& ((SlenChat)owner).findWindow(out.chosen.name) == null)
			    	{
			    		((SlenHud)owner.parent).ircChannels.add(new SlenChat(owner, out.chosen.name, false));
			    	}
			    	return true;
	    		}
	    		public void destroy()
	    		{
	    			visible = false;
	    			users.clear();
	    			owner = null;
	    			out.destroy();
	    			super.destroy();
	    		}
				
	    }
    	static {
    		Widget.addtype("ircchat", new WidgetFactory() {
	    		public Widget create(Coord c, Widget parent, Object[] args) {
				    String channel = (String)args[0];
				    return(new SlenChat((SlenChat)parent, channel));
	    		}
    		});
    	}
    	
    	SlenChat(Widget parent)
    	{
    		super(parent, "chat cnsl", false);
    		user = ui.sess.username;
    		IRC = new IRCConnection("irc.synirc.net", 6667, user, user+"|C",
    								user, user);
    		IRC.setIRCConnectionListener(this);
    		IRC.open();
    		this.channel = "chat cnsl";
    		initialized = true;
    	}
    	SlenChat(SlenChat caller, String channel)
    	{
    		this(caller, channel, true);
    	}
    	SlenChat(SlenChat caller, String channel, boolean hasUserList)
    	{
    		super(caller.parent, channel, true);
    		IRC = caller.IRC;
    		user = caller.user;
    		this.channel = channel;
    		userList = hasUserList ? new UserList(this) : null;
    		initialized = true;
    	}
    	public void wdgmsg(Widget sender, String msg, Object... args) {
			if(sender == in) 
			{
				handleInput((String)args[0]);
				in.settext("");
				return;
			} else if(sender == cbtn)
			{
				destroy();
				return;
			}
			super.wdgmsg(sender, msg, args);
	    }
	    
	    public void setChannel(String newChannel)
	    {
	    	if(newChannel.charAt(0) == '#')	return;
	       	channel = newChannel;
	    }
	    
	    private void handleInput(String input)
	    {
	    	if(input == null || input.trim().equals(""))	return;
	    	
	    	if(input.charAt(0) == '/')
	    	{
	    		String cmd;
	    		try{
	    			cmd = input.substring(0, input.indexOf(" ")).toUpperCase();
	    		}catch (StringIndexOutOfBoundsException e)
	    		{
	    			cmd = input;
	    		}
	    		String cArgs[];
	    		if(cmd.equals("/JOIN"))
	    		{
	    			cArgs = new String[1];
	    			cArgs[0] = input.substring(input.indexOf(" ")).trim();
	    			
	    			IRC.writeln("JOIN " + cArgs[0]);
	    			tSCWnd = new SlenChat(this, cArgs[0]);
	    			((SlenHud)parent).ircChannels.add(tSCWnd);
	    			parent.ui.bind(tSCWnd,
	    							 1000 + ((SlenHud)parent).ircChannels.indexOf(tSCWnd));
	    			return;
	    		} else if(cmd.equals("/NICK"))
	    		{
	    			cArgs = new String[1];
	    			cArgs[0] = input.substring(input.indexOf(" ")).trim();
	    			
	    			IRC.writeln("NICK " + cArgs[0]);
	    			user = cArgs[0];
	    			return;
	    		} else if(cmd.equals("/SERVER"))
	    		{
	    			cArgs = new String[1];
	    			cArgs[0] = input.substring(input.indexOf(" ")).trim();
	    			IRC.close();
	    			IRC = new IRCConnection("irc.synirc.net", 6667, user, user+"|1",
    								user, user);
    				IRC.setIRCConnectionListener(this);
    				IRC.open();
    				return;
	    		} else if(cmd.equals("/TOPIC"))
	    		{
	    			String activeChannel = "";
	    			for(int i = 0; i < ((SlenHud)parent).ircChannels.size(); i++)
	    			{
	    				if(((SlenHud)parent).ircChannels.get(i).visible)
	    				{
	    					activeChannel = ((SlenHud)parent).ircChannels.get(i).getChannel();
	    				}
	    			}
	    			cArgs = new String[1];
	    			cArgs[0] = input.substring(input.indexOf(" ")).trim();
	    			IRC.writeln("TOPIC " + activeChannel + " :" + cArgs[0]);
	    			return;
	    		} else if(cmd.equals("/MSG"))
	    		{
	    			cArgs = new String[2];
	    			cArgs[0] = input.substring(input.indexOf(" ")).trim();
	    			cArgs[1] = cArgs[0].substring(input.indexOf(" ")).trim();
	    			IRC.writeln("PRIVMSG " + cArgs[0] + " :" + cArgs[1]);
	    			out.append(user + ": " + input, Color.DARK_GRAY);
	    			return;
	    		} else if(cmd.equals("/NAMES"))
	    		{
	    			cArgs = new String[1];
	    			cArgs[0] = input.substring(input.indexOf(" ")).trim();
	    			IRC.writeln("NAMES " + cArgs[0]);
	    			return;
	    		} else
	    		{
	    			out.append("Command not recognized.");
	    			return;
	    		}
	    	}
	    	IRC.writeln("PRIVMSG " + channel + " :" + input);
	    	out.append(user + ": " + input, Color.BLACK);
	    }
    	
    	public void onAction( String user, String chan, String txt )
		{
			out.append("*" + user + " " + txt + "*");
		}
		public void onBan( String banned, String chan, String banner)
		{
			tSCWnd = findWindow(chan);
			tSCWnd.out.append(banned + " was banned by " + banner);
		}
		public void onClientInfo(String orgnick){}
		public void onClientSource(String orgnick){}
		public void onClientVersion(String orgnick){}
		public void onConnect()
		{
			out.append("Successfully connected to: "+ IRC.getServer());
			out.append("Joining #haven", Color.GREEN);
			IRC.writeln("JOIN #haven");
			if(findWindow("#haven") == null)
			{
				handleInput("/JOIN #haven");
			}
		}
		public void onDisconnect()
		{
			out.append("Disconnected", Color.GREEN);
		}
		public void onIsOn( String[] usersOn ){}
		public void onInvite(String orgin,String orgnick,String invitee,String chan){}
		public void onJoin( String user, String nick, String chan, boolean create )
		{
			tSCWnd = findWindow(chan);
			if(tSCWnd == null)	return;
			tSCWnd.out.append(nick + " joined the channel", Color.GREEN);
		}
		public void onJoins( String users, String chan)
		{
			tSCWnd = findWindow(chan);
			if(tSCWnd == null)	return;
			tSCWnd.out.append(users + " joined the channel", Color.GREEN);
		}
		public void onKick( String kicked, String chan, String kicker, String txt )
		{
			tSCWnd = findWindow(chan);
			if(tSCWnd == null)	return;
			tSCWnd.out.append(kicked + " has been kicked from the channel by "+ kicker + ". Reason: " + txt
				, Color.GREEN);
		}
		public void onMessage(String message)
			{
				out.append(message,Color.GREEN);
			}
		public void onPrivateMessage(String orgnick, String chan, String txt)
			{								
				//	Searches for existing windows and appends to the appropriate screen
				tSCWnd = findWindow(chan);		//	Checks for a channel
				if(tSCWnd == null){				//	Might be an existing PM screen
					tSCWnd = findWindow(orgnick);
				}
				
				//	Window exists and text is added
				if(tSCWnd != null){
					tSCWnd.out.append(orgnick + ": " + txt, Color.BLUE);
					
					//	Changes the button color if the window isn't visible
					for(Button b : ((SlenHud)parent).btns.values())
					{
						if(tSCWnd.getChannel().equalsIgnoreCase(b.text.text)
							&& !tSCWnd.visible)
						{
							b.changeText(b.text.text, Color.RED);
						}
					}
					return;
				}
				
				//	Window doesn't exist because its a PM with no channel, so a new one is created
				tSCWnd = findWindow(orgnick);
				tSCWnd = tSCWnd == null ? new SlenChat(this, orgnick, false) : tSCWnd;
	    		tSCWnd.out.append(orgnick + ": " + txt, Color.BLUE);
			}
		public void onNick( String user, String oldnick, String newnick )
		{
			for(int i = 0; i < ((SlenHud)parent).ircChannels.size(); i++)
			{
				tSCWnd = ((SlenHud)parent).ircChannels.get(i);
				if(oldnick.equalsIgnoreCase(tSCWnd.getChannel()))
				{
					tSCWnd.setChannel(newnick);
					tSCWnd.out.append(oldnick + " is now known as " + newnick, Color.GREEN);
				}
			}
			out.append(oldnick + " is now known as " + newnick, Color.GREEN);
		}
		public void onNotice(String text)
		{
			out.append(text);
		}
		public void onPart( String user, String nick, String chan )
		{
			tSCWnd = findWindow(chan);
			if(tSCWnd == null)	return;
			tSCWnd.out.append(nick + " has left " + chan, Color.GREEN);
		}
		public void onOp( String oper, String chan, String oped ){}
		public void onParsingError(String message)
		{
			out.append(message, Color.DARK_GRAY);
		}
		public void onPing(String params)
		{
			IRC.writeln("PONG " + params);
		}
		public void onStatus(String msg){}
		public void onTopic(String chanName, String newTopic)
		{
			tSCWnd = findWindow(chanName);
			if(tSCWnd==null)	return;
			tSCWnd.out.append("Topic changed to: " + newTopic);
		}
		public void onVersionNotice(String orgnick, String origin, String version){}
		public void onQuit(String user, String nick, String txt )
		{
			out.append(nick + " quit. Reason: " + txt, Color.GREEN);
		}
			
		public void onReplyVersion(String version){}
		public void onReplyListUserChannels(int channelCount){}
		public void onReplyListStart(){}
		public void onReplyList(String channel, int userCount, String topic){}
		public void onReplyListEnd(){}
		public void onReplyListUserClient(String msg){}
		public void onReplyWhoIsUser(String nick, String user,
									String name, String host){}
		public void onReplyWhoIsServer(String nick, String server, String info){}
		public void onReplyWhoIsOperator(String info){}
		public void onReplyWhoIsIdle(String nick, int idle, Date signon){}
		public void onReplyEndOfWhoIs(String nick){}
		public void onReplyWhoIsChannels(String nick, String channels){}
		public void onReplyMOTDStart()
		{
			out.append("Receiving MOTD: ", Color.CYAN);
		}
		public void onReplyMOTD(String msg)
		{
			out.append(msg);
		}
		public void onReplyMOTDEnd()
		{
			out.append("MOTD Received.", Color.CYAN);
		}
		public void onReplyNameReply(String channel, String users)
		{
			List<String> userList = new ArrayList<String>();
			String[] userListArray;
			String tUser;
			try {
				while(true)
				{
					tUser = users.substring(0,users.indexOf(" ")).trim();
					userList.add(tUser);
					users = users.substring(users.indexOf(" ")).trim();
				}
			} catch(StringIndexOutOfBoundsException e)
			{
			}
			userListArray = new String[userList.size()];
			for(int i = 0; i < userList.size(); i++)
			{
				userListArray[i] = (String)userList.get(i);
			}
			tSCWnd = findWindow(channel);
			tSCWnd.userList.addUserList(userListArray);
		}
		public void onReplyTopic(String channel, String topic)
		{
			tSCWnd = findWindow(channel);
			if(tSCWnd == null)	return;
			tSCWnd.out.append(topic, Color.GREEN);
		}
		public void onErrorNoMOTD()
		{
			out.append("No MOTD received");
		}
		public void onErrorNeedMoreParams()
		{
			out.append("Error: Command needs more parameters");
		}
		public void onErrorNoNicknameGiven()
		{
			out.append("Error: No nickname provided, please provide a nickname.");
		}
		public void onErrorNickNameInUse(String badNick)
		{
			out.append("Nick: \"" + badNick + "\" already in use");
			if(!badNick.equals(user + "|C"))	IRC.sendNick(user + "|C");
		}
		public void onErrorNickCollision(String badNick)
		{
			out.append("Nick collission: " + badNick);
		}
		public void onErrorErroneusNickname(String badNick)
		{
			out.append("Erroneous Nickname: \"" + badNick + "\".  Please pick a different one");
		}
		public void onErrorAlreadyRegistered()
		{
			out.append("Already registered");
		}
		public void onErrorUnknown(String message)
		{
			out.append(message);
		}
		public void onErrorUnsupported(String message)
		{
			out.append(message);
		}
		
		public String getChannel()
		{
			return new String(channel);
		}
		
		//	Attempts to find a window of the same title; returns null if no windows match.
		public SlenChat findWindow(String wndTitle)
		{						
			//	Searches for existing windows
			for(int i = 0; i < ((SlenHud)parent).ircChannels.size(); i++)
			{
				tSCWnd = ((SlenHud)parent).ircChannels.get(i);
				if(wndTitle.equalsIgnoreCase(tSCWnd.getChannel()))
				{
					return tSCWnd;
				}
			}
			//	No existing windows match
			return null;
		}
		public void destroy()
		{
			IRC.writeln("PART " + channel + " " + user + " closed this window.");
			((SlenHud)parent).ircChannels.remove(this);
			if(userList != null)	userList.destroy();
			channel = null;
			user = null;
			initialized = false;
			super.destroy();
		}
    }

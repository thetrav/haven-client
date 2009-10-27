package haven;
import java.util.*;
import java.awt.event.KeyEvent;
import java.awt.Color;
import org.relayirc.util.*;
import org.relayirc.core.*;
import org.relayirc.chatengine.*;

public class SlenConsole extends ChatHW implements IRCConnectionListener
{
	public IRCConnection IRC;
	public String user;
	private static SlenChat tSCWnd;
	private boolean initialized = false;
	    	
	static {
    	Widget.addtype("ircconsole", new WidgetFactory() {
	    	public Widget create(Coord c, Widget parent, Object[] args) {
			    return(new SlenConsole((SlenHud)parent));
	    	}
    	});
    }
    
    public SlenConsole(SlenHud parent)
    {
    	super(parent, "IRC Console", false);
    	user = ui.sess.username;
    	if(!CustomConfig.ircServerAddress.equals(""))
    	{
	    	IRC = new IRCConnection(CustomConfig.ircServerAddress, 6667, user, user+"|C",
	    							user, user);
	    	IRC.setIRCConnectionListener(this);
	    	IRC.open();
    	}
    	initialized = true;
    }
	public void handleInput(String input, ChatHW src)
	{
		if(input == null || input.trim().equals(""))	return;
	    if(input.charAt(0) == '/')
	    {
	    	String[] cArgs = input.split(" ");
	    	String cmd = cArgs.length >= 1 ? cArgs[0].toUpperCase() : "/INVALIDCOMMAND";
	    	
	    	//	JOIN -	Join the specified channels
	    	if(cmd.equals("/JOIN") && IRC != null)
	    	{
	    		if(cArgs.length >= 2) {
	    			List<Listbox.Option> tChannels = new ArrayList<Listbox.Option>();
	    			
	    			//	Split the appropriate list into channels & their passwords using the Listbox.Options class
		   			for(int i = 1; i < cArgs.length; i++)
		   			{
		   				if(cArgs[i].charAt(0) == '#') {
		   					if(i+1 < cArgs.length && cArgs[i+1].charAt(0) != '#') {
		   						tChannels.add(new Listbox.Option(cArgs[i], cArgs[i+1]));
		   						i++;
		   						continue;
		   					}
		   					tChannels.add(new Listbox.Option(cArgs[i], ""));
		   				}
		   			}
		   			
		   			//	Create all the channels specified if they dont'already exist
		   			for(Listbox.Option tChanInfo : tChannels)
		   			{
		   				tSCWnd = findWindow(tChanInfo.name);
		    			if(tSCWnd != null)	return;
		    			IRC.writeln("JOIN " + tChanInfo.name + " " + tChanInfo.disp);
		    			tSCWnd = new SlenChat(this, tChanInfo.name);
		    			if(!((SlenHud)parent).ircChannels.contains(tSCWnd))
		    			{
		    				((SlenHud)parent).ircChannels.add(tSCWnd);
		    			}
		   			}
	    		} else {
    				src.out.append("FORMAT: /JOIN <#CHANNEL1> [PASSWORD1] [#CHANNEL2 [PASSWORD2]...]");
	    		}
	    		return;
	    		//	INVITE - Invite an user to the specified channel
	    	} else if(cmd.equals("/INVITE") && IRC != null)
	    	{
	    		if(cArgs.length >= 3)
	    		{
	    			IRC.writeln("INVITE " + cArgs[1] + " " + cArgs[2]);
	    		} else {
	    			src.out.append("FORMAT: /INVITE <NICKNAME> <#CHANNEL>");
	    		}
	    		return;
	    		//	ACCEPT - Accept an invitation to join a channel
	    	} else if(cmd.equals("/ACCEPT") && IRC != null)
	    	{
	    		if(cArgs.length >= 2)
	    		{
	    			handleInput("/JOIN " + cArgs[1], src);
	    		} else {
	    			src.out.append("FORMAT: /ACCEPT <#CHANNEL>");
	    		}
	    		return;
	    		//	NICK - Change your nick in the current server
	    	} else if(cmd.equals("/NICK") && IRC != null)
	    	{	   
	    		if(cArgs.length >= 2) {
		   			IRC.writeln("NICK " + cArgs[1]);
		   			user = cArgs[1];
	    		} else {
	    			src.out.append("FORMAT: /NICK <NEWNICK>");
	    		}
	    		return;
	    		//	SERVER - Connect to the specified server
	    	} else if(cmd.equals("/SERVER"))
	    	{
	    		if(cArgs.length >= 2){
		   			if(IRC != null)	IRC.close();
		   			IRC = new IRCConnection(cArgs[1], 6667, user, user+"|C", user, user);
	    			IRC.setIRCConnectionListener(this);
	    			IRC.open();
	    		} else {
	    			src.out.append("FORMAT: /SERVER <SERVERADDRESS>");
	    		}
    			return;
    			//	TOPIC - Set the topic for the current channel
	    	} else if(cmd.equals("/TOPIC") 
	    		&& src.getClass().getName().equalsIgnoreCase(tSCWnd.getClass().getName())
	    		&& IRC != null)
	    	{
	    		if(cArgs.length >=2){
		   			String fullTopic = "";
		   			for(int i = 1; i < cArgs.length; i++)
		   				fullTopic += cArgs[i] + " ";
		   			IRC.writeln("TOPIC " + ((SlenChat)src).getChannel() + " :" + fullTopic);
	    		} else{
    				src.out.append("FORMAT: /TOPIC <NEWTOPIC>");
	    		}
	    		return;
	    		//	NOTICE - Send a message to the specified user/channel without creating a new window
	    	} else if(cmd.equals("/NOTICE") && IRC != null)
	    	{
	    		if(cArgs.length >= 3) {
	    			String msg = "";
	    			for(int i = 2; i < cArgs.length; i++)
		   				msg += cArgs[i] + " ";
		   			IRC.writeln("NOTICE " + cArgs[1] + " :" + msg);
		   			src.out.append(user + ": " + msg, Color.DARK_GRAY);
	    		} else {
	    			src.out.append("FORMAT: /NOTICE <USER> <MESSAGE>");
	    		}
	    		return;
	    		//	MSG PM TELL - Send a private message to the specified user in a new window
	    	} else if((cmd.equals("/MSG") || cmd.equals("/PM") || cmd.equals("/TELL")) && IRC != null)
	    	{
	    		if(cArgs.length >= 3) {
	    			String msg = "";
	    			for(int i = 2; i < cArgs.length; i++)
		   				msg += cArgs[i] + " ";
		   			IRC.writeln("PRIVMSG " + cArgs[1].trim() + " :" + msg);
		   			tSCWnd = findWindow(cArgs[1].trim());
		   			if(tSCWnd == null) {
		   				tSCWnd = new SlenChat(this, cArgs[1].trim(), false);
		   				((SlenHud)parent).ircChannels.add(tSCWnd);
		   			}
		   			tSCWnd.out.append(user + ": " + msg);
	    		} else {
	    			src.out.append("FORMAT: /MSG <USER> <MESSAGE>");
	    		}
	    		return;
	    	} else
	    	{
	    		src.out.append("Command not recognized.");
	    		return;
	    	}
	    }
	    if(IRC.getState() != IRC.CONNECTED){
	    	IRC.open(IRC);
	    	IRC.setIRCConnectionListener(this);
	    }
	    if(src.getClass().getName().equalsIgnoreCase(tSCWnd.getClass().getName()))
	    {
	    	IRC.writeln("PRIVMSG " + ((SlenChat)src).getChannel() + " :" + input);
	    	src.out.append(user + ": " + input, Color.BLACK);
	    	return;
	    }
	}
    	
    public void onAction( String user, String chan, String txt )
	{
		tSCWnd = findWindow(chan);
		if(tSCWnd != null)	tSCWnd.out.append("*" + user + " " + txt + "*", Color.MAGENTA.darker());
	}
	public void onBan( String banned, String chan, String banner)
	{
		tSCWnd = findWindow(chan);
		tSCWnd.out.append(banned + " was banned by " + banner, Color.GREEN.darker());
		if(tSCWnd.userList != null)	tSCWnd.userList.rmvUser(banned);
	}
	public void onClientInfo(String orgnick){}
	public void onClientSource(String orgnick){}
	public void onClientVersion(String orgnick){}
	public void onConnect()
	{
		out.append("Successfully connected to: "+ IRC.getServer());
		if(!CustomConfig.ircChannelList.trim().equals(""))
			handleInput("/JOIN " + CustomConfig.ircChannelList, this);
	}
	public void onDisconnect()
	{
		out.append("Disconnected", Color.GREEN.darker());
		for(SlenChat tSCWnd : ((SlenHud)parent).ircChannels)
		{
			tSCWnd.out.append("Disconnected from server");
		}
	}
	public void onIsOn( String[] usersOn ){}
	public void onInvite(String orgin,String orgnick,String invitee,String chan)
	{
		orgnick = parseNick(orgnick);
		invitee = parseNick(invitee);
		tSCWnd = findWindow(chan);
		if(tSCWnd != null)
		{
			tSCWnd.out.append(orgin + ": " + orgnick + " invited " + invitee + " into the channel.");
			return;
		}
		for(SlenChat tSCWnd : ((SlenHud)parent).ircChannels)
		{
			if(tSCWnd.visible)
			{
				tSCWnd.out.append("You have been invited into the " + chan + " channel by " 
						+ orgnick + ".  Type /ACCEPT "+ chan + " to accept the invitation.");
			}
		}
		out.append("You have been invited into the " + chan + " channel by " 
						+ orgnick + ".  Type /ACCEPT "+ chan + " to accept the invitation.");
	}
	public void onJoin( String user, String nick, String chan, boolean create )
	{
		nick = parseNick(nick);
		tSCWnd = findWindow(chan);
		if(tSCWnd == null)	return;
		if(tSCWnd.userList != null)	
		{
			tSCWnd.userList.addUser(user.trim(), nick.trim());
			tSCWnd.out.append(nick + " has joined the channel", Color.GREEN.darker());
		}
	}
	public void onJoins(String users, String chan)
	{
	}
	public void onKick( String kicked, String chan, String kicker, String txt )
	{
		tSCWnd = findWindow(chan);
		if(tSCWnd == null)	return;
		tSCWnd.out.append(kicked + " has been kicked from the channel by "+ kicker + ". Reason: " + txt
			, Color.GREEN.darker());
		//	Removes the user from the userlist
		if(tSCWnd.userList != null)
		{
			tSCWnd.userList.rmvUser(kicked);
		}
	}
	public void onMessage(String message)
		{
			out.append(message,Color.GREEN.darker());
		}
	public void onPrivateMessage(String orgnick, String chan, String txt)
		{								
			orgnick = parseNick(orgnick);
			//	Searches for existing windows and appends to the appropriate screen
			tSCWnd = findWindow(chan);		//	Checks for a channel
			if(tSCWnd == null){				//	Might be an existing PM screen
				tSCWnd = findWindow(orgnick);
			}
			
			//	Window exists and text is added
			if(tSCWnd != null){
				tSCWnd.out.append(orgnick + ": " + txt, Color.BLUE.darker());
				
				//	Changes the button color if the window isn't visible
				flashWindow(tSCWnd);
				return;
			}
			
			//	Window doesn't exist because its a PM with no channel, so a new one is created
			tSCWnd = findWindow(orgnick);
			tSCWnd = tSCWnd == null ? new SlenChat(this, orgnick, false) : tSCWnd;
			((SlenHud)parent).ircChannels.add(tSCWnd);
	   		tSCWnd.out.append(orgnick + ": " + txt, Color.BLUE.darker());
		}
	public void onNick( String user, String oldnick, String newnick )
	{
		oldnick = parseNick(oldnick);
		newnick = parseNick(newnick);
		for(SlenChat tSCWnd : ((SlenHud)parent).ircChannels)
		{
			//	Changes the links in the appropriate windows so all private messages are still
			//	directed to the correct window
			if(oldnick.equalsIgnoreCase(tSCWnd.getChannel()))
			{
				tSCWnd.setChannel(newnick);
				tSCWnd.out.append(oldnick + " is now known as " + newnick, Color.GREEN.darker());
			}
			//	Changes the nick on the appropriate userlist
			if(tSCWnd.userList != null) 
			{
				tSCWnd.userList.changeNick(oldnick, newnick);
			}
		}
	}
	public void onNotice(String text)
	{
		out.append(text);
	}
	public void onPart( String user, String nick, String chan )
	{
		nick = parseNick(nick);
		tSCWnd = findWindow(chan);
		if(tSCWnd == null)	return;
		tSCWnd.out.append(nick + " has left " + chan, Color.GREEN.darker());
		if(tSCWnd.userList != null)	tSCWnd.userList.rmvUser(user);
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
		tSCWnd.out.append("Topic changed to: " + newTopic, Color.GREEN.darker());
	}
	public void onVersionNotice(String orgnick, String origin, String version){}
	public void onQuit(String user, String nick, String txt )
	{
		nick = parseNick(nick);
		out.append(nick + " quit. Reason: " + txt, Color.GREEN.darker());
		
		//	Checks all windows for this nick and removes it when found
		for(SlenChat SC : ((SlenHud)parent).ircChannels)
		{
			if(SC.userList != null)
			{
				if(SC.userList.containsUser(nick))
				{
					SC.out.append(nick + " has left the channel. Reason: " + txt, Color.GREEN.darker());
					SC.userList.rmvUser(nick);
				}
			}
		}
	}
		
	public void onReplyVersion(String version){}
	public void onReplyListUserChannels(int channelCount){}
	public void onReplyListStart(){}
	public void onReplyList(String channel, int userCount, String topic){}
	public void onReplyListEnd(){}
	public void onReplyListUserClient(String msg){}
	public void onReplyWhoIsUser(String nick, String user,
								String name, String host)
	{
		nick = parseNick(nick);
		for(SlenChat tSCWnd : ((SlenHud)parent).ircChannels)
		{
			if(tSCWnd.userList != null)
				if(tSCWnd.userList.containsNick(nick))
					tSCWnd.userList.changeUser("", user);
		}
	}
	public void onReplyWhoIsServer(String nick, String server, String info){}
	public void onReplyWhoIsOperator(String info){}
	public void onReplyWhoIsIdle(String nick, int idle, Date signon){}
	public void onReplyEndOfWhoIs(String nick){}
	public void onReplyWhoIsChannels(String nick, String channels){}
	public void onReplyMOTDStart()
	{
		out.append("Receiving MOTD: ", Color.CYAN.darker());
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
		tSCWnd = findWindow(channel);
		if(tSCWnd == null)	return;
		//	Splits up the users string into an array of strings containing the user names
		//	then adds them to the temporary list
		String tNick[] = users.split(" ");
		for(int i = 0; i < tNick.length; i++)
		{
			tNick[i] = parseNick(tNick[i]);
			tSCWnd.userList.addUser("", tNick[i]);
			IRC.writeln("WHOIS " + tNick[i]);
		}
	}
	public void onReplyTopic(String channel, String topic)
	{
		tSCWnd = findWindow(channel);
		if(tSCWnd == null)	return;
		tSCWnd.out.append(topic, Color.GREEN.darker());
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
		if(!badNick.equals(user + "|C")){
			user += "|C";
			IRC.sendNick(user);
		}
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
	
	public void wdgmsg(Widget sender, String msg, Object... args) {
		if(sender == in) 
		{
			handleInput((String)args[0], this);
			in.settext("");
			return;
		} else if(sender == cbtn)
		{
			destroy();
			return;
		}
		super.wdgmsg(sender, msg, args);
	}
	//	Attempts to find a window of the same title; returns null if no windows match.
	public SlenChat findWindow(String wndTitle)
	{						
		//	Searches for existing windows
		for(SlenChat tSCWnd : ((SlenHud)parent).ircChannels)
		{
			//	There are no windows in the list
			if(tSCWnd == null)	break;
			
			//	Found a matching window
			if(wndTitle.equalsIgnoreCase(tSCWnd.title))
			{
				return tSCWnd;
			}
		}
		//	No existing windows match
		return null;
	}
	
	public static String parseNick(String nickname)
	{
		return nickname.replace('~',' ').replace('@',' ').replace('%', ' ').trim();
	}
}
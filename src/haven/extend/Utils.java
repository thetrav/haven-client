package haven.extend;

import haven.ExtendoFrame;
import haven.Message;

public class Utils
{
    public static String mkString(Object[] args)
    {
        final StringBuilder str = new StringBuilder("[");
        for (Object o : args)
        {
            str.append(o + ", ");
        }
        return str.toString() + "]";
    }

    public static String padright(String source, int columnLength)
    {
        final StringBuffer buf = new StringBuffer(source);
        for(int i=0; i <  columnLength - source.length(); i++)
        {
            buf.append(" ");
        }
        return buf.toString();
    }
    
    public static void sendMessageToServer(int id, String name, Object... args) {
        Message msg = new Message(Message.RMSG_WDGMSG);
        msg.adduint16(id);
        msg.addstring(name);
        msg.addlist(args);
        ExtendoFrame.sess.queuemsg(msg);
    }
}

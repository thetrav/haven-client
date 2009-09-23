package haven.extend;

import haven.Coord;
import haven.ExtendoFactory;
import haven.ExtendoFrame;
import haven.WidgetListener;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

public class KinListExtendFactory implements ExtendoFactory
{
    public static final String NEW_WIDGET_MESSAGE_CODE = "buddy";
    
    @Override
    public boolean newWidget(int id, String type, Coord c, int parent, Object... args)
    {
        ExtendoFrame.widgetListeners.put(id, new KinListExtend(id, type, c, parent, args));
        return false;
    }

    private class KinListExtend implements WidgetListener
    {
        private final JFrame frame;
        private final JList onlineList;
        private final JList offlineList;
        private final JPanel content;
        private final List<Buddy> onlineBuddies = new ArrayList<Buddy>();
        private final List<Buddy> offlineBuddies = new ArrayList<Buddy>();
        
        public KinListExtend(int id, String type, Coord c, int parent, Object[] args)
        {
            frame = new JFrame("Kin List");
            frame.setSize(300,400);
            frame.setLocation(100,100);
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            content = new JPanel(new BorderLayout());
            frame.getContentPane().add(content);
            onlineList = new JList();
            content.add(onlineList, BorderLayout.NORTH);
            offlineList = new JList();
            content.add(offlineList, BorderLayout.SOUTH);
            frame.setVisible(true);
        }
        
        private void addBuddy(String name, boolean online)
        {
            final Buddy bud = new Buddy(name, online);
            if(online) onlineBuddies.add(bud); else offlineBuddies.add(bud);
            refreshGui();
        }
        
        private void removeBuddy(String name)
        {
            rem(name, onlineBuddies);
            rem(name, offlineBuddies);
            refreshGui();
        }

        private void rem(String name, List<Buddy> buddies)
        {
            Buddy bud = findBud(name, buddies);
            if (bud != null)
            {
                buddies.remove(bud);
            }
        }

        private Buddy findBud(String name, List<Buddy> onlineBuddies2)
        {
            for(Buddy bud: onlineBuddies)
            {
                if (bud.name.equals(name))
                {
                    return bud;
                }
            }
            return null;
        }

        private void refreshGui()
        {
            onlineList.setListData(makeStringArray(onlineBuddies));
            offlineList.setListData(makeStringArray(offlineBuddies));
            content.revalidate();
        }

        private Object[] makeStringArray(List<Buddy> list)
        {
            Object[] buds = new Object[list.size()];
            for(int i=0; i<buds.length; i++)
            {
                buds[i] = list.get(i).name;
            }
            return buds;
        }

        @Override
        public void destroy()
        {
            frame.dispose();
        }

        @Override
        public boolean uimsg(int id, String msg, Object... args)
        {
            if (msg == "add") 
            {
                addBuddy(((String)args[0]).intern(), ((Integer)args[1]) != 0);
            }
            else if (msg == "rm") 
            {
                removeBuddy(((String)args[0]).intern());
            } 
            else if(msg == "ch") 
            {
                String name = ((String)args[0]).intern();
                boolean online = ((Integer)args[1]) != 0;
                removeBuddy(name);
                addBuddy(name, online);
            } 
            else if(msg.substring(0, 2).equals("i-")) 
            {
                // this message contains info about a selected buddy
            }
            return false;
        }
        
        private class Buddy {
            String name;
            boolean online;
            public Buddy(String name, boolean online)
            {
                this.name = name;
                this.online = online;
            }
        }
    }
 
    public static void main(String[] args)
    {
        final KinListExtend list = new KinListExtendFactory().new KinListExtend(1, null, null, 0, args);
        for(int i=0; i<3; i++)
        {
            list.addBuddy("onlineBuddy"+i, true);
        }
        for(int i=0; i<10; i++)
        {
            list.addBuddy("offlineBuddy"+i, false);
        }
        list.removeBuddy("onlineBuddy1");
        list.removeBuddy("frank");
    }
}

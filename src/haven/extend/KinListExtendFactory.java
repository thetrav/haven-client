package haven.extend;

import haven.Coord;
import haven.ExtendoFactory;
import haven.ExtendoFrame;
import haven.WidgetListener;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JList;

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
        private final int id;
        private final JFrame frame;
        private final JList onlineList;
        private final JList offlineList;
        private final List<Buddy> onlineBuddies = new ArrayList<Buddy>();
        private final List<Buddy> offlineBuddies = new ArrayList<Buddy>();
        
        public KinListExtend(int id, String type, Coord c, int parent, Object[] args)
        {
            this.id = id;
            frame = new JFrame("Kin List");
            frame.setSize(300,400);
            frame.setLocation(100,100);
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            
            onlineList = new JList();
            offlineList = new JList();
            
            frame.setVisible(true);
        }
        
        private void addBuddy(String name, boolean online)
        {
            final Buddy bud = new Buddy(name, online);
            if(online) onlineBuddies.add(bud); else offlineBuddies.add(bud);
            refreshGui();
        }

        private void refreshGui()
        {
//            onlineList.set
        }

        @Override
        public void destroy()
        {
            
        }

        @Override
        public boolean uimsg(int id, String msg, Object... args)
        {
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

}

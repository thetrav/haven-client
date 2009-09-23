package haven.extend;

import haven.Coord;
import haven.ExtendoFactory;
import haven.ExtendoFrame;
import haven.WidgetListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

public class KinListExtendFactory implements ExtendoFactory
{
    private static final Logger LOG = Logger.getLogger(KinListExtendFactory.class);
    public static final String NEW_WIDGET_MESSAGE_CODE = "buddy";
    
    @Override
    public boolean newWidget(int id, String type, Coord c, int parent, Object... args)
    {
        ExtendoFrame.widgetListeners.put(id, new KinListExtend(id, type, c, parent, args));
        return false;
    }

    private class KinListExtend implements WidgetListener
    {
        private static final int FRAME_WIDTH = 300;
        private final JFrame frame;
        private final JList onlineList;
        private final JList offlineList;
        private final JPanel content;
        private final JPanel controls;
        private final List<String> onlineBuddies = new ArrayList<String>();
        private final List<String> offlineBuddies = new ArrayList<String>();
        private String selectedBuddy = null;
        private UtilHook util = new DefaultUtilHook();
        
        public KinListExtend(final int id, final String type, final Coord c, final int parent, final Object[] args)
        {
            frame = new JFrame("Kin List");
            frame.setSize(FRAME_WIDTH, 400);
            frame.setLocation(100, 100);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            content = new JPanel();
            final FlowLayout flowLayout = new FlowLayout();
            content.setLayout(flowLayout);
            flowLayout.setHgap(FRAME_WIDTH);
//            content.setLayout(new BorderLayout());
//            content.setLayout(new GridLayout(3,1));
            frame.getContentPane().add(content);
            onlineList = new JList();
            addList(onlineList, "Online Kin");
            offlineList = new JList();
            addList(offlineList, "Offline Kin");
            addBuddySelectionListener(id, onlineList, offlineList);
            addBuddySelectionListener(id, offlineList, onlineList);
            controls = new JPanel(new GridLayout(1,3));
            controls.add(makeBuddyButton(id, "chat", "chat"));
            controls.add(makeBuddyButton(id, "inv", "party"));
            controls.add(makeBuddyButton(id, "rm", "remove"));
            content.add(controls, BorderLayout.SOUTH);
            controls.setVisible(false);
            frame.setVisible(true);
        }

        private void addList(final JList list, final String str)
        {
            final JPanel panel = new JPanel(new BorderLayout());
            final JLabel label = new JLabel(str);
            panel.add(label, BorderLayout.NORTH);
            label.setForeground(Color.black);
            final JScrollPane scroll = new JScrollPane(list);
            panel.add(scroll, BorderLayout.CENTER);
            label.addMouseListener(new MouseAdapter(){@Override
            public void mouseClicked(MouseEvent arg0)
            {
                scroll.setVisible(!scroll.isVisible());
                if(!scroll.isVisible() && !list.isSelectionEmpty())
                {
                    list.clearSelection();
                    controls.setVisible(false);
                }
                content.revalidate();
            }});
            content.add(panel);
//            content.add(panel, BorderLayout.NORTH);
        }

        private void addBuddySelectionListener(final int id, final JList selected, final JList other)
        {
            selected.addListSelectionListener(new ListSelectionListener()
            {
                @Override
                public void valueChanged(ListSelectionEvent e)
                {
                        other.clearSelection();
                        final String selection = (String)selected.getSelectedValue();
                        if(selection != null && !selection.equals(selectedBuddy))
                        {
                            selectedBuddy = selection;
                            util.sendMessageToServer(id, "ch", selectedBuddy);
                        }
                }});
        }
        
        private Component makeBuddyButton(final int id, final String message, final String label)
        {
            final JButton button = new JButton(label);
            button.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent arg0)
                {
                    util.sendMessageToServer(id, message, selectedBuddy);
                }});
            return button;
        }

        private void addBuddy(String name, boolean online)
        {
            if(online) onlineBuddies.add(name); else offlineBuddies.add(name);
            refreshGui();
        }
        
        private void removeBuddy(String name)
        {
            if (!onlineBuddies.remove(name)) offlineBuddies.remove(name);
            refreshGui();
        }

        private void refreshGui()
        {
            onlineList.setListData(onlineBuddies.toArray());
            offlineList.setListData(offlineBuddies.toArray());
            content.revalidate();
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
            else if(msg.equals("i-act")) 
            {
                controls.setVisible(true);
                content.revalidate();
            }
            return false;
        }
    }
 
    public static void main(String[] args)
    {
        final KinListExtend list = new KinListExtendFactory().new KinListExtend(1, null, null, 0, args);
        list.util = new UtilHook(){
            @Override
            public void sendMessageToServer(final int id, final String name, final Object... args)
            {
                LOG.debug("sending message id:"+id + " name:"+name + " args:" + Utils.mkString(args));
                if(name.equals("ch"))
                {
                    new Thread(){public void run() {
                        list.uimsg(id, "i-act", "nothing");
                        };
                        }.start(); 
                }
            }};
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

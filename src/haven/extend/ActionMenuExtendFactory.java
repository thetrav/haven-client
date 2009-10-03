package haven.extend;

import haven.Coord;
import haven.ExtendoFactory;
import haven.WidgetListener;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ActionMenuExtendFactory implements ExtendoFactory
{
    public static final String NEW_WIDGET_MESSAGE_CODE = "scm";
    public static final Map<String, String[]> buttons = new HashMap<String, String[]>();
    private static ActionMenuExtend instance = null;
    
    @Override
    public boolean newWidget(int id, String type, Coord c, int parent, Object... args)
    {
        instance = new ActionMenuExtend(id, type, c, parent, args);
        return false;
    }
    
    class ActionMenuExtend extends ExtendoFrameWidget implements WidgetListener
    {
        public ActionMenuExtend(int id, String type, Coord c, int parent, Object[] args)
        {
            super(id, type, c, parent, args);
        }

        @Override
        public boolean uimsg(int id, String msg, Object... args)
        {
            return false;
        }

        @Override
        protected void addContent(JPanel content)
        {
            
        }
        
        private JButton makeButton(final String name, final String[] args)
        {
            JButton button = new JButton(name);
            button.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent arg0)
                {
                    util.sendMessageToServer(id, name, args);
                }
            });
            return button;
        }

        @Override
        protected String getFrameLabel()
        {
            return "Action Menu";
        }

        @Override
        protected Point getFrameLocation()
        {
            return new Point(100, 100);
        }

        @Override
        protected Dimension getFrameSize()
        {
            return new Dimension(400, 400);
        }
        
        public void refresh()
        {
            content.removeAll();
            for(final String key : buttons.keySet())
            {
                final String[] args = buttons.get(key);
                content.add(makeButton(key, args));
            }
        }
    }

    public static void add(String name, String[] ad)
    {
        buttons.put(name, ad);
        if (instance != null)
        {
            instance.refresh();
        }
    }
}

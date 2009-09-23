package haven.extend;

import haven.Config;
import haven.Coord;
import haven.ExtendoFactory;
import haven.ExtendoFrame;
import haven.WidgetListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.apache.log4j.Logger;

public class FlowerMenuExtendFactory implements ExtendoFactory
{
    private static final Logger LOG = Logger.getLogger(FlowerMenuExtendFactory.class);
    public static final String NEW_WIDGET_MESSAGE_CODE = "sm";
    private static final String WIDGET_CLOSE_MESSAGE = "cl";
    
    @Override
    public boolean newWidget(int id, String type, Coord c, int parent, Object... args)
    {
        ExtendoFrame.widgetListeners.put(id, new FlowerMenuExtend(id, type, c, parent, args));
        return true;
    }
    
    private class FlowerMenuExtend implements WidgetListener
    {
        private int id;
        
        private JPanel buttonPanel;
        private List<JButton> buttons = new ArrayList<JButton>();
        
        FlowerMenuExtend(int id, String type, Coord c, int parent, Object... args)
        {
            this.id = id;
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new GridLayout(args.length+2, 0));
            buttonPanel.setBorder(new LineBorder(Color.BLACK, 1));
            ExtendoFrame.instance.content.add(buttonPanel, BorderLayout.CENTER);
            buttonPanel.add(new JLabel("flower menu from glob id:"+ExtendoFrame.instance.lastHit.hit.id));
            for (int i=0; i<args.length; i++)
            {
                addButton((String)args[i], i);
            }
            addButton("Close", -1);
            ExtendoFrame.instance.content.revalidate();
        }
        
        private void addButton(final String name, final int index)
        {
            final JButton button = new JButton(name);
            buttonPanel.add(button);
            buttons.add(button);
            button.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent arg0)
                {
                    select(index);
                }
            });
        }
        
        public void select(int index)
        {
            if(Config.LOG) LOG.info("clicked:"+index);
            Utils.sendMessageToServer(id, WIDGET_CLOSE_MESSAGE, index);
            destroy();
        }
        
        @Override
        public boolean uimsg(int id, String msg, Object... args)
        {
            final JPanel content = ExtendoFrame.instance.content;
            content.remove(buttonPanel);
            content.revalidate();
            return false;
        }

        @Override
        public void destroy()
        {
            for (JButton button : buttons)
            {
                for (ActionListener listener : button.getActionListeners())
                {
                    button.removeActionListener(listener);
                }
            }
            ExtendoFrame.instance.content.remove(buttonPanel);
            ExtendoFrame.instance.content.revalidate();
        }
    }
}

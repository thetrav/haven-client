package haven.extend;

import haven.Coord;
import haven.ExtendoFrame;
import haven.Message;
import haven.ExtendoFactory;
import haven.WidgetListener;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

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
        
        FlowerMenuExtend(int id, String type, Coord c, int parent, Object... args)
        {
            this.id = id;
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new GridLayout(0,args.length+1));
            ExtendoFrame.instance.content.add(buttonPanel, BorderLayout.CENTER);
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
            LOG.info("clicked:"+index);
            Utils.sendMessageToServer(id, WIDGET_CLOSE_MESSAGE, index);
            ExtendoFrame.instance.content.remove(buttonPanel);
            ExtendoFrame.instance.content.revalidate();
        }
        
        @Override
        public boolean uimsg(int id, String msg, Object... args)
        {
            
            final JPanel content = ExtendoFrame.instance.content;
            content.remove(buttonPanel);
            content.revalidate();
            return false;
        }
    }
}
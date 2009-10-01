package haven.extend;

import haven.Coord;
import haven.ExtendoFactory;
import haven.ExtendoFrame;
import haven.MainFrame;
import haven.WidgetListener;
import haven.extend.EventListeners.GameStateEventListener;
import haven.extend.Events.GameStateEvent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

public class CraftExtendFactory implements ExtendoFactory
{
    public static final String NEW_WIDGET_MESSAGE_CODE = "make";
    private static final Logger LOG = Logger.getLogger(CraftExtendFactory.class);

    @Override
    public boolean newWidget(int id, String type, Coord c, int parent, Object... args)
    {
        new CraftExtend(id, type, c, parent, args);
        return false;
    }
    
    class CraftExtend extends ExtendoFrameWidget implements WidgetListener
    {
        private String recipeName;
        private JList inputs;
        private JList outputs;
        public CraftExtend(int id, String type, Coord c, int parent, Object[] args)
        {
            super(id, type, c, parent, args);
            ExtendoFrame.widgetListeners.put(id, this);
        }

        @Override
        protected void addContent(JPanel content)
        {
            recipeName = (String)args[0];
            content.setLayout(new GridLayout(2,2));
            frame.setTitle("Craft " + recipeName);
            inputs = new JList();
            outputs = new JList();
            JScrollPane p1 = new JScrollPane(inputs);
            content.add(p1);
            JPanel p2 = new JPanel();
            p2.add(outputs);
            content.add(p2);
            final JTextField count = new JTextField("1");
            content.add(count);
            final JButton button = new JButton("Craft");
            
            button.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent arg0)
                {
                    int qty;
                    try
                    {
                        qty = Integer.parseInt(count.getText());
                    }
                    catch (NumberFormatException e)
                    {
                        qty = 1;
                    }
                    for(int i=0; i < qty; i++)
                    {
                        new CraftTask(id, qty).run();
                    }
                }
            });
            content.add(button);
        }
        

        @Override
        protected String getFrameLabel()
        {
            return "Craft ";
        }

        @Override
        protected Point getFrameLocation()
        {
            return MainFrame.f.getLocation();
        }

        @Override
        protected Dimension getFrameSize()
        {
            return new Dimension(400,200);
        }

        @Override
        public boolean uimsg(int id, String msg, Object... args)
        {
            LOG.info("message"+msg);
            if(msg.equals("pop"))
            {
                //add item
                final List<ItemMsg> input = new LinkedList<ItemMsg>();
                final List<ItemMsg> output = new LinkedList<ItemMsg>(); 
                parseArgs(args, input, output);
                refreshUI(input, output);
            }
            return false;
        }
        
        private void refreshUI(List<ItemMsg> input, List<ItemMsg> output)
        {
            addItems(input, inputs);
            addItems(output, outputs);
            inputs.revalidate();
            outputs.revalidate();
            content.revalidate();
        }

        private void addItems(List<ItemMsg> items, JList list)
        {
            final String[] itemData = new String[items.size()];
            int i=0;
            for(ItemMsg item : items)
            {
                final String resName = ExtendoFrame.sess.getres(item.resId).get().name;
                final String[] resNameComponents = resName.split("/");
                final String name = resNameComponents[resNameComponents.length-1];
                itemData[i++] = item.qty + " x " + name;
            }
            LOG.info("itemdata:"+Utils.mkString(itemData));
            list.setListData(itemData);
        }

        /**
         * the args array will contain two integer pair lists 0 separated
         */
        private void parseArgs(Object[] args, List<ItemMsg> input, List<ItemMsg> output)
        {
            int i=0;
            i = parse(i, args, input);
            parse(i, args, output);
        }
        
        /**
         * list items come in integer pairs ended with 0
         * @return
         */
        private int parse(int i, Object[] args, List<ItemMsg> items)
        {
            while(i < args.length && (Integer)args[i] > 0)
            {
                items.add(new ItemMsg((Integer)args[i++], (Integer)args[i++]));
            }
            return i + 1; //+1 is to get past the zero separator
        }

        class ItemMsg
        {
            public ItemMsg(int resId, int qty)
            {
                this.resId = resId;
                this.qty = qty;
            }
            int resId;
            int qty;
        }
    }
    
    class CraftTask implements Runnable, ExtendoFactory, WidgetListener, GameStateEventListener
    {
        private static final String PROGRESS_BAR_MSG = "img";
        private final int craftWidgetId;
        private int progressBarId;
        private int qty;
        private int progress;
        
        public CraftTask(int craftWidgetId, int qty)
        {
            this.craftWidgetId = craftWidgetId;
            this.qty = qty;
        }

        @Override
        public void run()
        {
            ExtendoFrame.factories.put(PROGRESS_BAR_MSG, this);
            EventListeners.add(this);
            new SendToServerUtilHook().sendMessageToServer(craftWidgetId, "make");
        }

        @Override
        public boolean newWidget(int id, String type, Coord c, int parent, Object... args)
        {
            progressBarId = id;
            ExtendoFrame.widgetListeners.put(id, this);
            return true;
        }

        @Override
        public boolean destroy(int id)
        {
            if(id == progressBarId && progress > 16)
            {
                qty--;
                LOG.info("qty="+qty);
                if(qty > 0)
                {
                    run();
                }
                else
                {
                    EventListeners.remove(this);
                }
            }
            return true;
        }

        private void finished()
        {
            ExtendoFrame.factories.remove(PROGRESS_BAR_MSG);
            ExtendoFrame.widgetListeners.remove(progressBarId);
        }

        @Override
        public boolean uimsg(int id, String msg, Object... args)
        {
            String arg = (String)args[0];
            progress = Integer.parseInt(arg.split("/")[3]);
            return true;
        }

        @Override
        public boolean event(GameStateEvent event)
        {
            if(event instanceof Events.Error)
            {
                Events.Error error = (Events.Error)event;
                finished();
                return true;
            }
            return false;
        }
        
        
    }

}

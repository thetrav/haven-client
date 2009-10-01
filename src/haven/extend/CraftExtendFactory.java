package haven.extend;

import haven.Coord;
import haven.ExtendoFactory;
import haven.ExtendoFrame;
import haven.WidgetListener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CraftExtendFactory implements ExtendoFactory
{
    public static final String NEW_WIDGET_MESSAGE_CODE = "make";

    @Override
    public boolean newWidget(int id, String type, Coord c, int parent, Object... args)
    {
        new CraftExtend(id, type, c, parent, args);
        return true;
    }
    
    class CraftExtend extends ExtendoFrameWidget implements WidgetListener
    {
        private String recipeName;
        private JList inputs;
        private JList outputs;
        public CraftExtend(int id, String type, Coord c, int parent, Object[] args)
        {
            super(id, type, c, parent, args);
        }

        @Override
        protected void addContent(JPanel content)
        {
            recipeName = (String)args[0];
            content.setLayout(new BorderLayout());
            content.add(new JLabel(recipeName), BorderLayout.NORTH);
            inputs = new JList();
            inputs.setEnabled(false);
            outputs = new JList();
            outputs.setEnabled(false);
            content.add(inputs, BorderLayout.CENTER);
            content.add(outputs, BorderLayout.CENTER);
            inputs.setVisible(false);
            outputs.setVisible(false);
            final JTextField count = new JTextField("1");
            content.add(count, BorderLayout.SOUTH);
            final JButton button = new JButton("Craft");
            
            button.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent arg0)
                {
                    int qty1;
                    try
                    {
                        qty1 = Integer.parseInt(count.getText());
                    }
                    catch (Exception e)
                    {
                        qty1 = 1;
                    }
                    final int qty = qty1; 
                    new Thread()
                    {
                        public void run() 
                        {
                            for(int i=0; i < qty; i++)
                            {
                                Utils.sendMessageToServer(id, "make");
                            }
                        };
                    }.start();
                }
            });
            content.add(button, BorderLayout.SOUTH);
        }
        

        @Override
        protected String getFrameLabel()
        {
            return "Craft ";
        }

        @Override
        protected Point getFrameLocation()
        {
            return new Point(20, 20);
        }

        @Override
        protected Dimension getFrameSize()
        {
            return new Dimension(400,200);
        }

        @Override
        public boolean uimsg(int id, String msg, Object... args)
        {
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
            content.revalidate();
        }

        private void addItems(List<ItemMsg> items, JList list)
        {
            final String[] itemData = new String[items.size()];
            int i=0;
            for(ItemMsg item : items)
            {
                itemData[i] = item.qty + " x " + ExtendoFrame.sess.getres(item.resId).get().name;
            }
            list.setListData(itemData);
            list.setVisible(true);
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
            while((Integer)args[i] != 0)
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
                System.out.println("added item with resource name:" + ExtendoFrame.sess.getres(resId).get().name);
                this.qty = qty;
            }
            int resId;
            int qty;
        }
    }
    
    

}

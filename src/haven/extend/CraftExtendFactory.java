package haven.extend;

import haven.Coord;
import haven.ExtendoFactory;
import haven.ExtendoFrame;
import haven.WidgetListener;

import java.awt.Dimension;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class CraftExtendFactory implements ExtendoFactory
{
    public static final String NEW_WIDGET_MESSAGE_CODE = "make";

    @Override
    public boolean newWidget(int id, String type, Coord c, int parent, Object... args)
    {
        new CraftExtend(id, type, c, parent, args);
        return false;
    }
    
    class CraftExtend extends ExtendoFrameWidget implements WidgetListener
    {
        private String recipeName;
        public CraftExtend(int id, String type, Coord c, int parent, Object[] args)
        {
            super(id, type, c, parent, args);
        }

        @Override
        protected void addContent(JPanel content)
        {
            recipeName = (String)args[0];
            content.add(new JLabel(recipeName));
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
            }
            return false;
        }
        
        
        private void parseArgs(Object[] args, List<ItemMsg> input, List<ItemMsg> output)
        {
            int i=0;
            i = parse(i, args, input);
            parse(i, args, output);
        }
        
        private int parse(int i, Object[] args, List<ItemMsg> items)
        {
            while((Integer)args[i] != 0)
            {
                items.add(new ItemMsg((Integer)args[i++], (Integer)args[i++]));
            }
            return i;
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

package haven.extend;

import haven.Coord;
import haven.ExtendoFactory;
import haven.WidgetListener;

import java.awt.Dimension;
import java.awt.Point;

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
        }

        @Override
        protected String getFrameLabel()
        {
            return "Craft";
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
            // TODO Auto-generated method stub
            return false;
        }
        
    }
    
    

}

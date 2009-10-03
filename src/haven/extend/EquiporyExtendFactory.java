package haven.extend;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import haven.Coord;
import haven.ExtendoFactory;

public class EquiporyExtendFactory implements ExtendoFactory
{
    private static final Logger LOG = Logger.getLogger(EquiporyExtendFactory.class);
    public static final String NEW_WIDGET_MESSAGE_CODE = "epry";
    @Override
    public boolean newWidget(int id, String type, Coord c, int parent, Object... args)
    {
        new EquiporyExtend(id, type, c, parent, args);
        return true;
    }
    
    class EquiporyExtend extends ExtendoFrameWidget
    {

        public EquiporyExtend(int id, String type, Coord c, int parent, Object[] args)
        {
            super(id, type, c, parent, args);
        }

        @Override
        protected void addContent(JPanel content)
        {
            
        }

        @Override
        protected String getFrameLabel()
        {
            return null;
        }

        @Override
        protected Point getFrameLocation()
        {
            return null;
        }

        @Override
        protected Dimension getFrameSize()
        {
            return null;
        }

        @Override
        public boolean uimsg(int id, String msg, Object... args)
        {
            return true;
        }
        
        @Override
        public boolean destroy(int id)
        {
            super.destroy(id);
            return true;
        }
    }

}

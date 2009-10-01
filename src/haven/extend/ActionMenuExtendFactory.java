package haven.extend;

import haven.Coord;
import haven.ExtendoFactory;
import haven.ExtendoFrame;
import haven.Glob;
import haven.Resource;
import haven.WidgetListener;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ActionMenuExtendFactory implements ExtendoFactory
{
    public static final String NEW_WIDGET_MESSAGE_CODE = "scm";
    
    
    @Override
    public boolean newWidget(int id, String type, Coord c, int parent, Object... args)
    {
        new ActionMenuExtend(id, type, c, parent, args);
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
            final Collection<Resource> open;
            final Glob glob = ExtendoFrame.sess.glob;
            synchronized(glob.paginae) {
            open = new HashSet<Resource>(glob.paginae);
            }
            for (Resource res : open)
            {
                /*
                 * if only it were so simple...  It appears that the paginae message will send an entire tree of widgets which will be constructed as resources
                 * There's a big next, prev, parent, child thing happening.  The resources in the action layer seems to contain the individual button name, the 
                 * action to be sent to the server when clicked and the image to draw for the button... Probably ordering too...
                 * we can probably get away with ignoring everything except the name, the action and the parent/child relationships.
                 */
                content.add(makeButton(res.name));
            }
        }

        private void clickButton(final String name)
        {
            
        }
        
        private JButton makeButton(final String name)
        {
            JButton button = new JButton(name);
            button.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent arg0)
                {
                    clickButton(name);
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
        
    }
}

package haven.extend;

import haven.Coord;
import haven.WidgetListener;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

public abstract class ExtendoFrameWidget extends WindowAdapter implements WidgetListener
{
    protected final int id;
    protected final String type;
    protected final Coord c;
    protected final int parent;
    protected final Object[] args;
    
    protected final JFrame frame;
    protected final JPanel content;
    
    protected UtilHook util = new SendToServerUtilHook();

    public ExtendoFrameWidget(int id, String type, Coord c, int parent, Object[] args)
    {
        this.id = id;
        this.type = type;
        this.c = c;
        this.parent = parent;
        this.args = args;
        
        frame = new JFrame(getFrameLabel());
        frame.setSize(getFrameSize());
        frame.setLocation(getFrameLocation());
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        content = new JPanel();
        frame.getContentPane().add(content);
        addContent(content);
        frame.addWindowListener(this);
        frame.setVisible(true);
    }
    
    protected abstract void addContent(JPanel content);

    protected abstract Point getFrameLocation();
    protected abstract Dimension getFrameSize();
    protected abstract String getFrameLabel();
    

    @Override
    public void windowClosing(WindowEvent e)
    {
        super.windowClosing(e);
        frame.removeWindowListener(this);
        frame.dispose();
        util.sendMessageToServer(id, "close");
    }
    
    @Override
    public boolean destroy(int id)
    {
        frame.removeWindowListener(this);
        frame.dispose();
        return false;
    }
}

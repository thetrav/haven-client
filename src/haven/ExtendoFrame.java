package haven;

import haven.extend.ChatExtendFactory;
import haven.extend.FlowerMenuExtendFactory;
import haven.extend.ItemHitEvent;
import haven.extend.KinListExtendFactory;
import haven.extend.Utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

public class ExtendoFrame extends JFrame
{
    private static final Logger LOG = Logger.getLogger(ExtendoFrame.class);
    private static final long serialVersionUID = 1L;
    private static final int TOOLBOX_RES_WIDTH = 150;
    private static final int WINDOW_GAP = 10;
    public final static ExtendoFrame instance = new ExtendoFrame();
    public static Session sess;
    public static ItemHitEvent lastHit = null;
    public final JPanel content;
    private boolean logMessages = true;
    private static Map<String, ExtendoFactory> factories = new HashMap<String, ExtendoFactory>();
    public static Map<Integer, WidgetListener> widgetListeners = new HashMap<Integer, WidgetListener>();
    
    //widget creation listeners
    static
    {
        factories.put(FlowerMenuExtendFactory.NEW_WIDGET_MESSAGE_CODE, new FlowerMenuExtendFactory());
        factories.put(ChatExtendFactory.NEW_WIDGET_MESSAGE_CODE, new ChatExtendFactory());
        factories.put(ChatExtendFactory.NEW_GLOBAL_CHAT_WIDGET_MESSAGE_CODE, new ChatExtendFactory());
        factories.put(KinListExtendFactory.NEW_WIDGET_MESSAGE_CODE, new KinListExtendFactory());
    }
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        if (Config.LOG) LOG.info("running");
        MainFrame.main(args);
    }
    
    public ExtendoFrame()
    {
        setSize(TOOLBOX_RES_WIDTH, Config.RES_HEIGHT);
        setLocation(Config.RES_WIDTH + WINDOW_GAP, 0);
        setTitle("Haven Toolbox");
        setVisible(true);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        content = new JPanel();
        getContentPane().add(content);
        addButton("fixed cam", new ActionListener()
        {
            @Override
            public void actionPerformed(final ActionEvent arg0)
            {
                ExtendoFrame.this.fixedCam(arg0);
            }
        });
        addCheckBox("log messages", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                logMessages = !logMessages;
            }});
    }
    
    private void addCheckBox(String string, ActionListener actionListener)
    {
        JCheckBox checkbox = new JCheckBox(string);
        content.add(checkbox);
        checkbox.addActionListener(actionListener);
    }

    private void addButton(final String string, final ActionListener actionListener)
    {
        JButton button = new JButton(string);
        content.add(button);
        button.addActionListener(actionListener);
    }

    public void fixedCam(ActionEvent arg0)
    {
        final MapView mv = MainFrame.f.p.ui.root.findchild(MapView.class);
        if (mv != null) mv.cam = new MapView.FixedCam();
    }
    
    public void rcvmsg(int id, String name, Object... args) 
    {
        final String argString = Utils.mkString(args);
        if(Config.LOG) LOG.info("widget sent message via: rcvmsg("+id+", "+name+", "+argString+")");
        
    }
    
    public boolean newwidget(UI ui, int id, String type, Coord c, int parent, Object... args) throws InterruptedException
    {
        final String argString = Utils.mkString(args);
        if(Config.LOG) LOG.info("newwidget("+id+", "+type+", "+c+ ", " + parent + ", " + argString + ")");
        final ExtendoFactory listener = factories.get(type);
        if(listener != null) 
        {
            return listener.newWidget(id, type, c, parent, args);
        }
        return true;
    }
    
    public boolean uimsg(final UI ui, int id, String name, Object... args) 
    {
        final String argString = Utils.mkString(args);
        if(Config.LOG) LOG.info("uimsg("+id+", "+name+", "+argString+")");
        final WidgetListener listener = widgetListeners.get(id);
        if (listener != null)
        {
            return listener.uimsg(id, name, args);
        }
        return true;
    }

    public boolean destroy(int id)
    {
        if(Config.LOG) LOG.info("destroy " + id);
        final WidgetListener listener = widgetListeners.get(id);
        if(listener!= null)
        {
            return listener.destroy();
        }
        return true;
    }

    public void itemClick(Coord c, Coord mc, int button, int modflags, Gob hit)
    {
        lastHit = new ItemHitEvent(c, mc, button, modflags, hit);
    }

}


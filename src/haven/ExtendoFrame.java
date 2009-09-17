package haven;

import haven.extend.FlowerMenuExtend;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
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
    public final JPanel content;
    private boolean logMessages = false;
    private static Map<String, NewWidgetListener> newWidgetListeners = new HashMap<String, NewWidgetListener>();
    private static Map<String, UiMessageListener> uiMsgListeners = new HashMap<String, UiMessageListener>();
    
    //widget creation listeners
    static
    {
        newWidgetListeners.put(FlowerMenuExtend.NEW_WIDGET_MESSAGE_CODE, new FlowerMenuExtend());
    }
    
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        LOG.info("running");
        MainFrameWrapper.main(args);
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
        final MapView mv = MainFrameWrapper.f.p.ui.root.findchild(MapView.class);
        if (mv != null) mv.cam = new MapView.FixedCam();
    }
    
    public void rcvmsg(int id, String name, Object... args) 
    {
        final String argString = mkString(args);
        if(logMessages) LOG.info("rcvmsg("+id+", "+name+", "+argString+")");
    }
    
    public void newwidget(UI ui, int id, String type, Coord c, int parent, Object... args) throws InterruptedException
    {
        final String argString = mkString(args);
        if(logMessages) LOG.info("newwidget("+id+", "+type+", "+c+ ", " + parent + ", " + argString + ")");
        final NewWidgetListener lst = newWidgetListeners.get(type);
        if(lst != null) 
        {
            if(lst.newWidget(id, type, c, parent, args))
            {
                ui.newwidget(id, type, c, parent, args);
            }
        }
    }
    
    public void uimsg(final UI ui, int id, String name, Object... args) 
    {
        final String argString = mkString(args);
        if(logMessages) LOG.info("uimsg("+id+", "+name+", "+argString+")");
        final UiMessageListener listener = uiMsgListeners.get(name);
        if (listener != null)
        {
            if(listener.uimsg(id, name, args))
            {
                ui.uimsg(id, name, args);
            }
        }
    }

    private String mkString(Object[] args)
    {
        final StringBuilder str = new StringBuilder("[");
        for (Object o : args)
        {
            str.append(o + ", ");
        }
        return str.toString() + "]";
    }
    
    static class MainFrameWrapper extends MainFrame
    {
        public MainFrameWrapper(int w, int h)
        {
            super(w, h);
        }
        
        public static void main(final String[] args) {
            /* Set up the error handler as early as humanly possible. */
            ThreadGroup g;
            if(Utils.getprop("haven.errorhandler", "off").equals("on")) {
                final haven.error.ErrorHandler hg = new haven.error.ErrorHandler();
                hg.sethandler(new haven.error.ErrorGui(null) {
                    public void errorsent() {
                    hg.interrupt();
                    }
                });
                g = hg;
            } else {
                g = new ThreadGroup("Haven client");
            }
            Thread main = new Thread(g, new Runnable() {
                public void run() {
                    try {
                    javabughack();
                    } catch(InterruptedException e) {
                    return;
                    }
                    main2(args);
                }
                }, "Haven main thread");
            main.start();
            try {
                main.join();
            } catch(InterruptedException e) {
                g.interrupt();
                return;
            }
            System.exit(0);
            }
        
        protected static void main2(String[] args) {
            LOG.debug("mainframe.main2");
            Config.cmdline(args);
            ThreadGroup g = Utils.tg();
            Resource.loadergroup = g;
            setupres();
            f = new MainFrameWrapper(Config.RES_WIDTH, Config.RES_HEIGHT);
            if(Config.fullscreen)
                f.setfs();
            f.g = g;
            if(g instanceof haven.error.ErrorHandler) {
                final haven.error.ErrorHandler hg = (haven.error.ErrorHandler)g;
                hg.sethandler(new haven.error.ErrorGui(null) {
                    public void errorsent() {
                    hg.interrupt();
                    }
                });
            }
            f.run();
            dumplist(Resource.loadwaited, Config.loadwaited);
            dumplist(Resource.cached(), Config.allused);
            if(ResCache.global != null) {
                try {
                Collection<Resource> used = new LinkedList<Resource>();
                for(Resource res : Resource.cached()) {
                    if(res.prio >= 0)
                    used.add(res);
                }
                dumplist(used, new PrintWriter(ResCache.global.store("tmp/allused")));
                } catch(java.io.IOException e) {}
            }
            }
        
        @Override
        public void run() {
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    g.interrupt();
                }
                });
            Thread ui = new Thread(Utils.tg(), p, "Haven UI thread");
            p.setfsm(this);
            ui.start();
            try {
                while(true) {
                Bootstrap bill = new Bootstrap();
                if(Config.defserv != null)
                    bill.setaddr(Config.defserv);
                if((Config.authuser != null) && (Config.authck != null)) {
                    bill.setinitcookie(Config.authuser, Config.authck);
                    Config.authck = null;
                }
                Session sess = bill.run(p);
                RemoteUIWrapper rui = new RemoteUIWrapper(sess);
                rui.run(p.newui(sess));
                }
            } catch(InterruptedException e) {
            } finally {
                ui.interrupt();
                dispose();
            }
            }
        
    }
    
    static class RemoteUIWrapper extends RemoteUI
    {

        public RemoteUIWrapper(Session sess)
        {
            super(sess);
            LOG.info("created RUI");
        }
        
        public void run(UI ui) throws InterruptedException {
            this.ui = ui;
            ui.setreceiver(this);
            LOG.info("running UI");
            while(sess.alive()) {
                Message msg;
                while((msg = sess.getuimsg()) != null) {
                
                final Object[] list = msg.list();
                if(msg.type == Message.RMSG_NEWWDG) {
                    int id = msg.uint16();
                    String type = msg.string();
                    Coord c = msg.coord();
                    int parent = msg.uint16();
                    instance.newwidget(ui, id, type, c, parent, list);
                } else if(msg.type == Message.RMSG_WDGMSG) {
                    int id = msg.uint16();
                    String name = msg.string();
                    instance.uimsg(ui, id, name, list);
                } else if(msg.type == Message.RMSG_DSTWDG) {
                    int id = msg.uint16();
                    ui.destroy(id);
                }
                }
                synchronized(sess) {
                sess.wait();
                }
            }
            }
    }
}


package haven.extend;

import haven.Config;
import haven.Coord;
import haven.ExtendoFactory;
import haven.ExtendoFrame;
import haven.MainFrame;
import haven.WidgetListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

public class ChatExtendFactory implements ExtendoFactory
{
    private static final Logger LOG = Logger.getLogger(ChatExtendFactory.class);
    public static final String NEW_WIDGET_MESSAGE_CODE = "slenchat";

    @Override
    public boolean newWidget(int id, String type, Coord c, int parent, Object... args)
    {
        ExtendoFrame.widgetListeners.put(id, new ChatExtend(id, type, c, parent, args));
        return false;
    }
    
    class ChatExtend extends WindowAdapter implements WidgetListener, KeyListener
    {
        private UtilHook util = new DefaultUtilHook();
        private final int id;
        private StringBuffer lines = new StringBuffer();
        private final JFrame frame;
        private final JTextArea text;
        private final JTextField input;
        private final JPanel pane;
        private final JScrollPane jScrollPane;
        private int columnLength = "blue".length();
        
        public ChatExtend(int id, String type, Coord c, int parent, Object[] args)
        {
            this.id = id;
            frame = new JFrame((String)args[0]);
            frame.setSize(300,200);
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            pane = new JPanel();
            pane.setLayout(new BorderLayout());
            input = new JTextField();
            pane.add(input, BorderLayout.SOUTH);
            input.addKeyListener(this);
            text = new JTextArea();
            text.setEditable(false);
            jScrollPane = new JScrollPane(text);
            pane.add(jScrollPane, BorderLayout.CENTER);
            frame.getContentPane().add(pane);
            if(MainFrame.f != null)
            {
                frame.setLocation(MainFrame.f.getWidth() + 10, MainFrame.f.getY());
            }
            frame.addWindowListener(this);
           
            frame.setVisible(true);
        }
        
        @Override
        public void windowClosing(WindowEvent e)
        {
            super.windowClosing(e);
            frame.removeWindowListener(this);
            frame.dispose();
            Utils.sendMessageToServer(id, "close");
        }

        @Override
        public boolean uimsg(int id, String msg, Object... args)
        {
            if (Config.LOG) LOG.info("got message" + msg);
            if(msg.equals("log")) {
                final String source = determineSource(args);
                lines.append(Utils.padright(source, columnLength) + " > ");
                lines.append((String)args[0]);
                lines.append("\n");
                text.setText(lines.toString());
                scrollToBottom();
                pane.revalidate();
                pane.updateUI();
                return true;
            }
            return false;
        }

        private void scrollToBottom()
        {
            text.setCaretPosition(text.getText().length());
        }

        private String determineSource(Object[] args)
        {
            if(args.length > 1) {
                final Color col = (Color)args[1];
                if (col.getBlue() != 0)
                {
                    return "blue";
                }
                if (col.getRed() != 0)
                {
                    return "red";
                }
                else return "unknown color";
            }
            return "no color";
        }

        @Override
        public void keyPressed(KeyEvent arg0)
        {
            if (arg0.getKeyCode() == KeyEvent.VK_ENTER)
            {
                uimsg(id, "msg", input.getText(), new Color(255,0,0));
                util.sendMessageToServer(id, "msg",input.getText());
                input.setText("");
            }
        }

        @Override
        public void keyReleased(KeyEvent arg0)
        {
            
        }

        @Override
        public void keyTyped(KeyEvent arg0)
        {
            
        }

        @Override
        public void destroy()
        {
            input.removeKeyListener(this);
            frame.setVisible(false);
            frame.dispose();
        }
        
    }
    
    public static void main(String[] args)
    {
        System.out.println("testing");
        final ChatExtend chatExtend = new ChatExtendFactory().new ChatExtend(1, "", null, 1, new Object[]{"test chat"});
        chatExtend.util = new StubbedUtilHook();
        chatExtend.uimsg(1,"log", "test some blue text", new Color(0, 0, 255));
        for (int i=0; i<20; i++)
        {
            chatExtend.uimsg(1,"log", "test some red text "+i, new Color(255, 0, 0));
        }
        
        final JFrame testFrame = new JFrame("Test input");
        testFrame.setSize(300,100);
        testFrame.setLocation(500,100);
        final JTextField text = new JTextField();
        testFrame.getContentPane().add(text);
        text.addKeyListener(new KeyAdapter(){@Override
        public void keyPressed(KeyEvent e)
        {
            if (e.getKeyCode() == KeyEvent.VK_ENTER)
            {
                new Thread()
                {
                    public void run() 
                    {
                        chatExtend.uimsg(1, "log", text.getText(), new Color(0,0,255));
                        text.setText("");
                    }
                }.start();
            }
        }});
        testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        testFrame.setVisible(true);
    }
}

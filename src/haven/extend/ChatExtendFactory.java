package haven.extend;

import haven.Coord;
import haven.ExtendoFactory;
import haven.ExtendoFrame;
import haven.MainFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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
    public static final String NEW_GLOBAL_CHAT_WIDGET_MESSAGE_CODE = "chat";

    @Override
    public boolean newWidget(int id, String type, Coord c, int parent, Object... args)
    {
        ExtendoFrame.widgetListeners.put(id, new ChatExtend(id, type, c, parent, args));
        return false;
    }
    
    class ChatExtend extends ExtendoFrameWidget implements KeyListener
    {
        private UtilHook util = new DefaultUtilHook();
        private StringBuffer lines = new StringBuffer();
        private JTextArea text;
        private JTextField input;
        private JScrollPane jScrollPane;
        private int columnLength = "blue".length();
        
        public ChatExtend(int id, String type, Coord c, int parent, Object[] args)
        {
            super(id, type, c, parent, args);
        }
        
        @Override
        protected void addContent(JPanel content)
        {
            content.setLayout(new BorderLayout());
            input = new JTextField();
            content.add(input, BorderLayout.SOUTH);
            input.addKeyListener(this);
            text = new JTextArea();
            text.setEditable(false);
            text.setLineWrap(true);
            text.setWrapStyleWord(true);
            jScrollPane = new JScrollPane(text);
            jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            content.add(jScrollPane, BorderLayout.CENTER);
        }

        @Override
        public boolean uimsg(int id, String msg, Object... args)
        {
            if(msg.equals("log")) {
                final String source = determineSource(args);
                lines.append(Utils.padright(source, columnLength) + " > ");
                lines.append((String)args[0]);
                lines.append("\n");
                text.setText(lines.toString());
                scrollToBottom();
                content.revalidate();
                content.updateUI();
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

        @Override
        protected String getFrameLabel()
        {
            if(type.equals("slenchat"))
            {
                return (String)args[0];
            }
            else return "Chat";
        }

        @Override
        protected Point getFrameLocation()
        {
            if(MainFrame.f != null)
            {
                return new Point(MainFrame.f.getX() + MainFrame.f.getWidth() + 10, MainFrame.f.getY());
            }
            return new Point(100,100);
        }

        @Override
        protected Dimension getFrameSize()
        {
            return new Dimension(400,300);
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
        chatExtend.uimsg(1,"log", "test some red text test some red texttest some red texttest some red texttest some red texttest some red text", new Color(255, 0, 0));
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

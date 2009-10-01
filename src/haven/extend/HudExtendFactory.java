package haven.extend;

import haven.Coord;
import haven.ExtendoFactory;
import haven.ExtendoFrame;
import haven.WidgetListener;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

public class HudExtendFactory implements ExtendoFactory
{
    public static final String NEW_WIDGET_MESSAGE_CODE = "slen";
    private List<JButton> buttons = new LinkedList<JButton>();

    @Override
    public boolean newWidget(int id, String type, Coord c, int parent, Object... args)
    {
        new HudExtend(id, type, c, parent, args);
        return true;
    }
    
    class HudExtend implements WidgetListener
    {
        private final int id;
        private final JPanel content = new JPanel();
        
        public HudExtend(final int id, final String type, final Coord c, final int parent, final Object[] args)
        {
            this.id = id;
            content.setLayout(new GridLayout(1,4));
            content.add(makeButton("inv","inv"));
            content.add(makeButton("equip", "equ"));
            content.add(makeButton("Char", "chr"));
            content.add(makeButton("Kin", "bud"));
            
            final JPanel extendo = ExtendoFrame.instance.content;
            extendo.add(content);
            content.setVisible(true);
            extendo.revalidate();
        }
        
        private Component makeButton(final String label, final String msg)
        {
            final JButton button = new JButton(label);
            button.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent arg0)
                {
                    Utils.sendMessageToServer(id, msg);
                }

            });
            return button;
        }

        @Override
        public boolean destroy()
        {
            ExtendoFrame.instance.content.remove(content);
            for (JButton button : buttons)
            {
                content.remove(button);
                for (ActionListener al : button.getActionListeners())
                {
                    button.removeActionListener(al);
                }
            }
            return false;
        }

        @Override
        public boolean uimsg(int id, String msg, Object... args)
        {
            return false;
        }
    }
}

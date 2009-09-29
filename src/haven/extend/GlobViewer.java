package haven.extend;

import haven.Config;
import haven.Coord;
import haven.Gob;
import haven.MainFrame;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GlobViewer
{
    private final JFrame frame;
    private final JPanel content;
    private final JList gobList;
    private final JPanel gobListPanel;
    
    private final JPanel gobDetailPanel;
//    private final JList gobAttrList;
//    private final JList gobOverlayList;
//    
    private Map<Integer, Gob> objs = null;
    private Gob selectedObj = null;
    
    public void setObjs(Map<Integer, Gob> objs)
    {
        this.objs = objs;
    }

    public static final GlobViewer instance = new GlobViewer();
    
    private GlobViewer()
    {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(400,700);
        content = new JPanel();
        content.setLayout(new BorderLayout());
        frame.getContentPane().add(content);
        
        gobListPanel = new JPanel(new BorderLayout());
        gobListPanel.add(new JLabel("gobs"), BorderLayout.NORTH);
        gobList = new JList();
        gobListPanel.add(gobList, BorderLayout.CENTER);
        content.add(gobListPanel);
        JButton refreshButton = new JButton("refresh");
        refreshButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                refreshGui();
            }
        });
        gobListPanel.add(refreshButton, BorderLayout.SOUTH);
        
        gobList.addListSelectionListener(new ListSelectionListener()
        {
            @Override
            public void valueChanged(ListSelectionEvent arg0)
            {
               gobListChanged(); 
            }
        });
        gobDetailPanel = new JPanel();
        content.add(gobDetailPanel, BorderLayout.CENTER);
        gobDetailPanel.setVisible(false);
    }
    

    private void gobListChanged()
    {
        final Integer id = Integer.valueOf(((String)gobList.getSelectedValue()).split(" ")[1]);
        selectedObj = objs.get(id);
        
    }
    
    private void refreshGui()
    {
        refreshGobPanel();
        content.revalidate();
    }

    private void refreshGobPanel()
    {
        if(gobListPanel.isVisible())
        {
            final String[] listData = new String[objs.size()];
            int i=0;
            for(final Integer id : objs.keySet())
            {
                Gob obj = objs.get(id);
                listData[i] = "Glob: " + id + " at " + obj.rc;
                System.out.println("i="+i+" data="+listData[i]);
                i++;
            }
            
            gobList.setListData(listData);
            gobListPanel.revalidate();
        }
    }

    public void show()
    {
        frame.setLocation(MainFrame.f.getX()+Config.RES_WIDTH, MainFrame.f.getY());
        frame.setVisible(true);
        refreshGui();
    }
    
    public static void main(String[] args)
    {
        final Map<Integer, Gob> objs = new HashMap<Integer, Gob>();
        addTestGob(1,objs);
        addTestGob(2,objs);
        addTestGob(3,objs);
        addTestGob(4,objs);
        instance.setObjs(objs);
        instance.frame.setVisible(true);
        instance.refreshGui();
        instance.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static void addTestGob(final int id, final Map<Integer, Gob> objs)
    {
        Gob testGob = new Gob(null, new Coord(1,1));
        objs.put(id, testGob );
    }
}

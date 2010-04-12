package haven.trav;

import haven.Coord;
import haven.Glob;
import haven.HavenPanel;
import haven.Message;
import haven.OCache;
import haven.Session;

public class MockSession extends Session
{

    public MockSession()
    {
        glob = new MockGlob();
//        super(server, username, cookie);
    }
    
    
    int count = 0;
    @Override
    public Message getuimsg()
    {
        if(count == 0)
        {
            count++;
            int parent = 0;
            int id = 1;
            String type = "slen";
            Coord c = new Coord(0,0);
            try
            {
                HavenPanel.ui.newwidget(id, type, c , parent, null);
                HavenPanel.ui.newwidget(2, "slenchat", c , 1, "1");
                HavenPanel.ui.newwidget(22, "slenchat", c , 1, "2");
                HavenPanel.ui.newwidget(23, "slenchat", c , 1, "3");
                HavenPanel.ui.newwidget(23, "slenchat", c , 1, "4");
                HavenPanel.ui.newwidget(25, "slenchat", c , 1, "5");
                HavenPanel.ui.newwidget(26, "slenchat", c , 1, "6");
                HavenPanel.ui.newwidget(27, "slenchat", c , 1, "7");
                HavenPanel.ui.newwidget(28, "slenchat", c , 1, "8");
                HavenPanel.ui.newwidget(29, "slenchat", c , 1, "9");
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        
        return null;
    }

}

class MockGlob extends Glob
{

    public MockGlob()
    {
        super(null);
        oc = new MockOCache(this);
    }
    
}

class MockOCache extends OCache
{

    public MockOCache(Glob glob)
    {
        super(glob);
    }
    
    @Override
    public synchronized void tick()
    {
    }
    
}

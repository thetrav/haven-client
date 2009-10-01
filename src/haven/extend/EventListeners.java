package haven.extend;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;


public class EventListeners
{
    private static final Collection<GameStateEventListener> listeners = new HashSet<GameStateEventListener>();
    
    public static void gameStateChanged(final Events.GameStateEvent event)
    {
        for(Iterator<GameStateEventListener> it = listeners.iterator(); it.hasNext();)
        {
            final GameStateEventListener listener = it.next();
            if(listener.event(event)) it.remove();
        }
    }
    
    public static void add(GameStateEventListener listener)
    {
        listeners.add(listener);
    }
    
    public static void remove(GameStateEventListener listener)
    {
        listeners.remove(listener);
    }
    
    public interface GameStateEventListener
    {
        boolean event(Events.GameStateEvent event);
    }
}

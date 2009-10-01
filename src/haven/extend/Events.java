package haven.extend;


public class Events
{
    public static abstract class GameStateEvent{}
    public static class ProgressCompleteEvent extends GameStateEvent
    {
        public final int id;
        public ProgressCompleteEvent(int id) { this.id = id; }
    }
    
    public static class ProgressBegunEvent extends GameStateEvent
    {
        public final int id;
        public ProgressBegunEvent(int id) { this.id = id; }
    }
    
    public static class ProgressBarMovedEvent extends GameStateEvent
    {
        public final int progress;
        public ProgressBarMovedEvent(final int progress) { this.progress = progress; }
    }
    
    public static class Error extends GameStateEvent
    {
        public final String err;
        public Error(final String err) { this.err = err; }
    }
}

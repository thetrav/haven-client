package haven.extend;


public class SendToServerUtilHook implements UtilHook
{
    
    /*
     * the messageQueue.add call will return instantly and the message will be sent whenever it needs to be sent. 
     */
    public void sendMessageToServer(final int id, final String name, final Object... args) 
    {
        Utils.sendMessageToServer(id, name, args);
    }
}

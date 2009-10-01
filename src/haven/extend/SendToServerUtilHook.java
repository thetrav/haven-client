package haven.extend;


public class SendToServerUtilHook implements UtilHook
{
    
    /*
     * the messageQueue.add call will return instantly and the message will be sent whenever it needs to be sent. 
     */
    public void sendMessageToServer(final int id, final String name, final Object... args) 
    {
        System.out.println("SendToServerUtilHook: sending:" + id + " name " + name +" args"+ Utils.mkString(args));
        Utils.sendMessageToServer(id, name, args);
    }
}

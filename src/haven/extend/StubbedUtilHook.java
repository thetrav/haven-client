package haven.extend;

import org.apache.log4j.Logger;

public class StubbedUtilHook implements UtilHook
{
    private static Logger LOG = Logger.getLogger(StubbedUtilHook.class);
    @Override
    public void sendMessageToServer(int id, String name, Object... args)
    {
        LOG.info("stub message to server: if:"+id + " name:"+name +" args:"+Utils.mkString(args));
    }

}

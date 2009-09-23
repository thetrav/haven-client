package haven.extend;

public class DefaultUtilHook implements UtilHook
{
    public void sendMessageToServer(int id, String name, Object... args) {
        Utils.sendMessageToServer(id, name, args);
    }
}

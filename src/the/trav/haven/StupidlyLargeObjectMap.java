package the.trav.haven;

import com.jme3.math.Vector3f;

import java.util.HashMap;
import java.util.Map;


public class StupidlyLargeObjectMap {

    public static final Map<String, Haven3dResource> RESOURCES = new HashMap<String, Haven3dResource>();
    public static final Haven3dResource UNKNOWN_RES = new Haven3dResource("UNKNOWN", new Vector3f(5f, 5f, 5f), "Textures/objects/unknown.png");
    static {
        add("gfx/terobjs/cndlbrl", 1, 10, 1, "Textures/objects/cndlbrl.png");
        add("gfx/terobjs/furniture/cclosed", 5, 5, 2.5f, "Textures/objects/cclosed.png");
        add("gfx/terobjs/npcs/sherlock", 5, 10, 5, "Textures/objects/sherlock.png");
        add("gfx/terobjs/npcs/germania", 5, 10, 5, "Textures/objects/germania.png");
        add("gfx/terobjs/npcs/surg", 5, 10, 5, "Textures/objects/surg.png");
        add("gfx/arch/door-cellar", 5, 2.5f, 5, "Textures/objects/door-cellar.png");
        add("gfx/terobjs/mining/ladder", 10, 20, 1, "Textures/objects/ladder.png");
    }

    public static void add(String name, float w, float h, float d, String tex) {
        RESOURCES.put(name, new Haven3dResource(name, new Vector3f(w, h, d), tex));
    }

    public static Haven3dResource get(String resName) {
        Haven3dResource res = RESOURCES.get(resName);
        return res == null ? UNKNOWN_RES : res;
    }

    static class Haven3dResource {
        public Haven3dResource(String name, Vector3f size, String texture) {
            this.name = name;
            this.size = size;
            this.texture = texture;
        }
        public String name;
        public Vector3f size;
        public String texture;
    }
}

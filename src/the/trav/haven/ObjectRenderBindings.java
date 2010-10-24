package the.trav.haven;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import haven.*;
import static the.trav.haven.StupidlyLargeObjectMap.get;

import java.util.logging.Logger;

public class ObjectRenderBindings {
    private static final Logger LOG = Logger.getLogger(ObjectRenderBindings.class.getName());

    private Node node;
    private AssetManager assetManager;

    public ObjectRenderBindings(Node rootNode, AssetManager assetManager) {
        node = new Node("object node");
        rootNode.attachChild(node);
        this.assetManager = assetManager;
    }

    public void update() {
        node.detachAllChildren();
        Coord playerCoord = Glob.instance.oc.getgob(MapView.playergob).rc;
        OCache oc = Glob.instance.oc;
        for(Gob g : oc.objs.values()) {
            LOG.info("Gob:"+g.id+" at "+g.rc + " class:"+g.getClass());
            Drawable d = g.getattr(Drawable.class);
	        if(d != null && d instanceof ResDrawable) {
                ResDrawable rd = (ResDrawable)d;
                Resource r = rd.res.get();
                if(r != null) {
                    LOG.info("has a resource by name:"+ r.name);
                    Coord c = g.rc.sub(playerCoord);
                    addObj(new Vector3f(c.x, 0, c.y), get(r.name));
                }
            }
        }
    }

    private void addObj(Vector3f pos, StupidlyLargeObjectMap.Haven3dResource haven3dResource) {
        Box b = new Box(Vector3f.ZERO, haven3dResource.size); // create cube shape
        Geometry quad = new Geometry(haven3dResource.name+" Quad", b);
        Material material = new Material(assetManager, "Common/MatDefs/Misc/SimpleTextured.j3md");
        material.setTexture("m_ColorMap", assetManager.loadTexture(haven3dResource.texture));
        quad.setMaterial(material);

        quad.setLocalTranslation(pos);
        node.attachChild(quad);
        LOG.info("attached model for:"+haven3dResource.name+" at "+pos);
    }
}

package the.trav.haven;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

import java.util.logging.Logger;

public class PlayerRenderBindings {
    private static final Logger LOG = Logger.getLogger(PlayerRenderBindings.class.getName());

    private Node node;
    private AssetManager assetManager;

    public PlayerRenderBindings(Node rootNode, AssetManager assetManager) {
        node = new Node("object node");
        rootNode.attachChild(node);
        this.assetManager = assetManager;
        Sphere s = new Sphere(10,10,1);
        Geometry geom = new Geometry("player sphere", s);
        Material material = new Material(assetManager, "Common/MatDefs/Misc/SolidColor.j3md");
        material.setColor("m_Color", ColorRGBA.Blue);
        geom.setMaterial(material);
        geom.setLocalTranslation(new Vector3f(0,1,0));
        node.attachChild(geom);
    }


    public void update() {
        
    }
}

package the.trav.haven;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import haven.Glob;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ThreeDeeFrame extends SimpleApplication {
    public static final ThreeDeeFrame instance = new ThreeDeeFrame();
    public static final JFrame controls = new JFrame();

    public static void init() {
        controls.setTitle("controls for 3d");
        controls.setSize(600,200);
        JButton button = new JButton("update");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                instance.havenUpdate();
        }});
        controls.getContentPane().add(button);
        controls.setLocation(0,800);
        controls.setVisible(true);
        instance.start();
    }

    private GroundRenderBindings ground = null;
    private ObjectRenderBindings objects = null;


    public void havenUpdate() {
        ground.update();
        objects.update();
        cam.setLocation(new Vector3f(0,100,-100));
        cam.lookAt(new Vector3f(0,0,0), new Vector3f(0,1,0));
    }

    public void simpleInitApp() {
        assetManager.registerLocator("haven3d.zip", ZipLocator.class.getName());
        ground = new GroundRenderBindings(rootNode, assetManager);
        objects = new ObjectRenderBindings(rootNode, assetManager);
    }
}

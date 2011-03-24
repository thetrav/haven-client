package the.trav.haven;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import haven.Coord;
import haven.Glob;
import haven.MCache;
import haven.MapView;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class GroundRenderBindings {
    private static final int MIN_X = -25*11;
    private static final int MAX_X = 25*11;
    private static final int MIN_Y = -25*11;
    private static final int MAX_Y = 25*11;

    private Logger LOG = Logger.getLogger(GroundRenderBindings.class.getName());
    private final static Map<Integer, String> TILE_TEXTURES = new HashMap<Integer, String>();
    static {
        TILE_TEXTURES.put(1, "Textures/tiles/water.png");
        TILE_TEXTURES.put(2, "Textures/tiles/cobble.png");
        TILE_TEXTURES.put(7, "Textures/tiles/grass.png");
        TILE_TEXTURES.put(3, "Textures/tiles/plowed.png");
        TILE_TEXTURES.put(4, "Textures/tiles/pineForest.png");
        TILE_TEXTURES.put(14, "Textures/tiles/sand.png");
        TILE_TEXTURES.put(15, "Textures/tiles/logfloor.png");
        TILE_TEXTURES.put(16, "Textures/tiles/stone.png");
        TILE_TEXTURES.put(18, "Textures/tiles/mountain.png");

    }
    static final String UNKNOWON_TEXTURE = "Textures/tiles/unknown.png";

    private Node node;
    private AssetManager assetManager;

    public GroundRenderBindings(Node root, AssetManager assetManager) {
        node = new Node("ground");
        this.assetManager = assetManager;
        root.attachChild(node);
    }

    public void update() {
        node.detachAllChildren();
        MCache map = Glob.instance.map;
        Coord playerCoord = Glob.instance.oc.getgob(MapView.playergob).rc;
        for(Coord c : map.grids.keySet()) {
            Node gridNode = new Node("grid:"+c);
            int[][] tiles = map.grids.get(c).tiles;
            for(int i=0; i<tiles.length; i++) {
                for(int j=0; j< tiles[i].length; j++) {
                    int type = tiles[i][j];
                    if (type != 255) {
                        Vector3f v = buildCoord(playerCoord, c, i, j);
                        if(v != null) gridNode.attachChild(buildTile(v, type));
                    }
                }
            }
            node.attachChild(gridNode);
        }
    }

    private String textureName(int type) {
        String name = TILE_TEXTURES.get(type);
        if(name == null) LOG.info("unknown tile type:"+type);
        return name == null ? UNKNOWON_TEXTURE : name;
    }

    public Spatial buildTile(Vector3f pos, int type) {
        Box b = new Box(Vector3f.ZERO, 5.5f, 1.0f, 5.5f); // create cube shape
        Geometry quad = new Geometry("Textured Quad", b);
        Material material = new Material(assetManager, "Common/MatDefs/Misc/SimpleTextured.j3md");
        material.setTexture("m_ColorMap", assetManager.loadTexture(textureName(type)));
        quad.setMaterial(material);
        LOG.info("putting cube at :"+pos);
        quad.setLocalTranslation(pos);
        return quad;
    }

    public Vector3f buildCoord(Coord player, Coord grid, int xOff, int yOff) {
        Coord c = grid.mul(1100).sub(player);
        c.x += xOff * 11;
        c.y += yOff * 11;
        if(c.x < MIN_X || c.x > MAX_X || c.y < MIN_Y || c.y > MAX_Y) return null;
        return new Vector3f(c.x, 0, c.y);
    }

    private void printGrid(MCache.Grid grid) {
        for(int[] row : grid.tiles)
        {
            StringBuffer buf = new StringBuffer("[");
            for(int value: row) {
                buf.append(value+",");
            }
            buf.append("]");
            LOG.info(buf.toString());
        }
    }
}

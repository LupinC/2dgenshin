package mock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

public class LevelEditorScene extends Scene{

    private GameObject obj1;
    private Spritesheet sprites;
    SpriteRenderer obj1sprite;

    public  LevelEditorScene(){


    }

    @Override
    public void init(){
        loadResources();

        this.camera = new Camera(new Vector2f(-250,0));

        sprites = AssetPool.getSpritesheet("assets/images/spritesheet.png");

        obj1 = new GameObject("object 1",
                new Transform(new Vector2f(200,100), new Vector2f(256, 256)), 2);
        obj1sprite = new SpriteRenderer();
        obj1sprite.setColor(new Vector4f(1,0,0,1));
        obj1.addComponent(obj1sprite);
        this.addGameObjectToScene(obj1);
        this.activeGameObject = obj1;

        GameObject obj2 = new GameObject("object 2",
                new Transform(new Vector2f(400,100), new Vector2f(256, 256)), 2);
        SpriteRenderer obj2spriteRenderer = new SpriteRenderer();
        Sprite obj2sprite = new Sprite();
        obj2sprite.setTexture(AssetPool.getTexture("assets/images/blendImage2.png"));
        obj2spriteRenderer.setSprite(obj2sprite);
        obj2.addComponent(obj2spriteRenderer);
        this.addGameObjectToScene(obj2);

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        String serialized = gson.toJson(obj1);
        GameObject obj = gson.fromJson(serialized, GameObject.class);
        System.out.println(obj);

    }

    private void loadResources(){
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpritesheet("assets/images/spritesheet.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheet.png"),
                16,16,26,0));
    }


    @Override
    public void update(float dt){
        //System.out.println(1.0f/dt);
        for(GameObject go : this.gameObjects){
            go.update(dt);
        }
        this.renderer.render();
    }

    @Override
    public void imgui(){
        ImGui.begin("Test window");
        ImGui.text("random text");
        ImGui.end();
    }
}

// blending function: C_f = C_a (S_a) + Cs(1 - Sa)
//example: green on red, Sa = 0.6: Cf = [0, 1, 0] * 0.6 + [1, 0, 0] * 0.4
//                                    = [0.4, 0.6, 0]
//draw further back things first: z-index: -2 to 2

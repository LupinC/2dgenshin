package scenes;

import components.*;
import imgui.ImGui;
import imgui.ImVec2;
import mock.Camera;
import mock.GameObject;
import mock.Prefabs;
import mock.Transform;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import renderer.DebugDraw;
import scenes.Scene;
import util.AssetPool;

public class LevelEditorScene extends Scene {

    private GameObject obj1;
    private Spritesheet sprites;
    SpriteRenderer obj1sprite;

    GameObject levelEditorStuff = new GameObject("LevelEditor", new Transform(new Vector2f()),0);

    public  LevelEditorScene(){


    }

    @Override
    public void init(){
        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new GridLines());
        loadResources();

        this.camera = new Camera(new Vector2f(-250,0));
        sprites = AssetPool.getSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png");

        if(levelLoaded){
            this.activeGameObject = gameObjects.get(0);
            return;
        }



/*        obj1 = new GameObject("object 1",
                new Transform(new Vector2f(200,100), new Vector2f(256, 256)), 2);
        obj1sprite = new SpriteRenderer();
        obj1sprite.setColor(new Vector4f(1,0,0,1));
        obj1.addComponent(obj1sprite);
        obj1.addComponent(new Rigidbody());
        this.addGameObjectToScene(obj1);
        this.activeGameObject = obj1;

        GameObject obj2 = new GameObject("object 2",
                new Transform(new Vector2f(400,100), new Vector2f(256, 256)), 2);
        SpriteRenderer obj2spriteRenderer = new SpriteRenderer();
        Sprite obj2sprite = new Sprite();
        obj2sprite.setTexture(AssetPool.getTexture("assets/images/blendImage2.png"));
        obj2spriteRenderer.setSprite(obj2sprite);
        obj2.addComponent(obj2spriteRenderer);
        this.addGameObjectToScene(obj2);*/



    }

    private void loadResources(){
        AssetPool.getShader("assets/shaders/default.glsl");



        AssetPool.addSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
                16,16,81,0));

        //AssetPool.getTexture("assets/images/blendImage2.png");
    }

    @Override
    public void update(float dt){
        //System.out.println(1.0f/dt);
        levelEditorStuff.update(dt);

        for(GameObject go : this.gameObjects){
            go.update(dt);
        }
        this.renderer.render();
    }

    @Override
    public void imgui(){
        ImGui.begin("Test window");

        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x;
        for(int i = 0; i < sprites.size(); i++){
            Sprite sprite = sprites.getSprite(i);
            float spriteWidth = sprite.getWidth() * 2;
            float spriteHeight = sprite.getHeight() * 2;
            int id = sprite.getTexId();
            Vector2f[] texCoords = sprite.getTexCoords();

            ImGui.pushID(i);
            if(ImGui.imageButton(id, spriteWidth, spriteHeight,
                    texCoords[2].x,texCoords[0].y,texCoords[0].x, texCoords[2].y)){
                GameObject object = Prefabs.generateSpriteObject(sprite, 32,32);
                //attach this to the mouse cursor
                levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
            }
            ImGui.popID();

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);
            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
            if(i + 1 < sprites.size() && nextButtonX2 < windowX2){
                ImGui.sameLine();
            }
        }

        ImGui.end();
    }
}

// blending function: C_f = C_a (S_a) + Cs(1 - Sa)
//example: green on red, Sa = 0.6: Cf = [0, 1, 0] * 0.6 + [1, 0, 0] * 0.4
//                                    = [0.4, 0.6, 0]
//draw further back things first: z-index: -2 to 2

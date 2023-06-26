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
import physics2d.PhysicsSystem2D;
import physics2d.primitives.Circle;
import physics2d.rigidbody.Rigidbody2D;
import renderer.DebugDraw;
import scenes.Scene;
import util.AssetPool;

public class LevelEditorScene extends Scene {

    private Spritesheet sprites;

    GameObject levelEditorStuff = new GameObject("LevelEditor", new Transform(new Vector2f()),0);
    PhysicsSystem2D physics = new PhysicsSystem2D(1.0f/120.0f, new Vector2f(0, -10));
    Transform obj1, obj2;
    Rigidbody2D rb1, rb2;


    public  LevelEditorScene(){


    }

    @Override
    public void init(){
        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new GridLines());

        obj1 = new Transform(new Vector2f(100,500));
        obj2 = new Transform(new Vector2f(100, 300));

        rb1 = new Rigidbody2D();
        rb2 = new Rigidbody2D();
        rb1.setRawTransform(obj1);
        rb2.setRawTransform(obj2);
        rb1.setMass(100.0f);
        rb2.setMass(200.0f);

        Circle c1 = new Circle();
        c1.setRadius(10.0f);
        c1.setRigidbody(rb1);
        Circle c2 = new Circle();
        c2.setRadius(20.0f);
        c2.setRigidbody(rb2);
        rb1.setCollider(c1);
        rb2.setCollider(c2);

        physics.addRigidbody(rb1, true);
        physics.addRigidbody(rb2, false);


        loadResources();

        this.camera = new Camera(new Vector2f(-250,0));
        sprites = AssetPool.getSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png");
    }

    private void loadResources(){
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
                16,16,81,0));

        AssetPool.getTexture("assets/images/blendImage2.png");

        for(GameObject g: gameObjects){
            if(g.getComponent(SpriteRenderer.class)!=null){
                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
                if(spr.getTexture()!=null){
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
                }
            }
        }
    }

/*    float x= 0.0f;
    float y = 0.0f;*/

    @Override
    public void update(float dt){
        //System.out.println(1.0f/dt);
        levelEditorStuff.update(dt);
/*        DebugDraw.addCircle(new Vector2f(x,y), 64, new Vector3f(0,1,0),1);
        x+= 50f*dt;
        y+=50f*dt;*/
        for(GameObject go : this.gameObjects){
            go.update(dt);
        }

        DebugDraw.addCircle(obj1.position, 10.0f, new Vector3f(1,0,0));
        DebugDraw.addCircle(obj2.position, 20.0f, new Vector3f(0,0,1));
        physics.update(dt);
        this.renderer.render();
    }

    @Override
    public void render(){
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

package mock;

import components.*;
import org.joml.Vector2f;
import util.AssetPool;

public class Prefabs {

    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY){
        GameObject block =Window.getScene().createGameObject("Sprite_Object_Gen");
        block.transform.scale.x = sizeX;
        block.transform.scale.y = sizeY;
        SpriteRenderer renderer= new SpriteRenderer();
        renderer.setSprite(sprite);
        block.addComponent(renderer);

        return block;
    }

    public static GameObject generateCharacter(){
        Spritesheet playerSprites = AssetPool.getSpritesheet("assets/images/spritesheet.png");
        GameObject character = generateSpriteObject(playerSprites.getSprite(0),0.25f,0.25f);

        AnimationState run = new AnimationState();
        run.title = "run";
        float defaultFrameTime = 0.23f;
        run.addFrame(playerSprites.getSprite(0), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(3), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(run);
        stateMachine.setDefaultState(run.title);
        character.addComponent(stateMachine);
        return character;
    }

    public static GameObject generateQuestionBlock(){
        Spritesheet item = AssetPool.getSpritesheet("assets/images/items.png");
        GameObject questionBlock = generateSpriteObject(item.getSprite(0),0.25f,0.25f);

        AnimationState run = new AnimationState();
        run.title = "Flicker";
        float defaultFrameTime = 0.5f;
        run.addFrame(item.getSprite(0), defaultFrameTime);
        run.addFrame(item.getSprite(1), defaultFrameTime);
        run.addFrame(item.getSprite(2), defaultFrameTime);
        run.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(run);
        stateMachine.setDefaultState(run.title);
        questionBlock.addComponent(stateMachine);
        return questionBlock;
    }
}

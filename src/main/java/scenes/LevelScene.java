package scenes;

import mock.Window;
import scenes.Scene;

public class LevelScene extends Scene {
    public LevelScene(){
        System.out.println("Inside level scene");
        Window.get().r = 1;
        Window.get().g = 1;
        Window.get().b = 1;
    }

    public void update(float dt){

    }

    public void render(){

    }
}

package components;

import mock.GameObject;
import mock.MouseListener;
import mock.Window;
import util.Settings;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component{
    GameObject holdingObject = null;
    public void pickupObject(GameObject go){
        this.holdingObject = go;
        Window.getScene().addGameObjectToScene(go);
    }

    public void place(){
        this.holdingObject = null;
    }

    @Override
    public void update(float dt){
        if(holdingObject != null){
            holdingObject.transform.position.x = MouseListener.getOrthoX();
            holdingObject.transform.position.y = MouseListener.getOrthoY();
            if(holdingObject.transform.position.x < 0) {holdingObject.transform.position.x -= Settings.GRID_WIDTH;}
            holdingObject.transform.position.x = (int) (holdingObject.transform.position.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH;
            holdingObject.transform.position.y = (int) (holdingObject.transform.position.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT;
            if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
                place();
            }
        }
    }
}

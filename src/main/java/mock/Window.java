package mock;

import observers.EventSystem;
import observers.Observer;
import observers.events.Event;
import observers.events.EventType;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.GL;
import renderer.*;
import scenes.LevelEditorSceneInitializer;
import scenes.Scene;
import scenes.SceneInitializer;
import util.AssetPool;

import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Observer {
    private int width, height;
    private String title;
    private long glfwWindow;
    private ImGuiLayer imguiLayer;
    private Framebuffer framebuffer;
    private PickingTexture pickingTexture;

    private static Window window = null;

    private static Scene currentScene;
    private boolean runtimePlaying = false;

    private long audioContext;
    private long audioDevice;

    private Window(){
        this.width = 1920;
        this.height = 1080;
        this.title = "2dgenshin";
        EventSystem.addObserver(this);
    }

    public static void changeScene(SceneInitializer sceneInitializer){
        if(currentScene!=null){
            currentScene.destroy();
        }
        getImguiLayer().getPropertiesWindow().setActiveGameObject(null);
        currentScene =new Scene(sceneInitializer);
        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    public static Window get(){
        if(Window.window == null){
            Window.window = new Window();
        }

        return Window.window;
    }

    public static Scene getScene(){
        return get().currentScene;
    }

    public void run(){
        //System.out.println("hello lwgjl" + Version.getVersion());

        init();
        loop();

        //destroy the audio context
        alcDestroyContext(audioContext);
        alcCloseDevice(audioDevice);

        //free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        //teminate
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();

    }

    public void init(){
        //error callback
        GLFWErrorCallback.createPrint(System.err).set();

        //init glfw
        if(!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        //Configure glfw
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);


        //create the window
        glfwWindow = glfwCreateWindow(this.width,this.height, this.title, NULL, NULL);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) ->{
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });
        if(glfwWindow == NULL){
            throw new IllegalStateException("Failed to create the glfw window.");
        }

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow,KeyListener::keyCallback);


        //Make the openGL context current
        glfwMakeContextCurrent(glfwWindow);

        //enable v-sync
        glfwSwapInterval(1);

        //make the window visible
        glfwShowWindow(glfwWindow);

        //initialize audio device
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        audioDevice = alcOpenDevice(defaultDeviceName);
        int[] attributes = {0};
        audioContext = alcCreateContext(audioDevice, attributes);
        alcMakeContextCurrent(audioContext);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        if(!alCapabilities.OpenAL10){
            assert false : "Audio library not supported.";
        }

        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        this.framebuffer = new Framebuffer(1920,1080);
        this.pickingTexture = new PickingTexture(1920,1080);
        glViewport(0,0,1920,1080);

        this.imguiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
        this.imguiLayer.initImGui();

        Window.changeScene(new LevelEditorSceneInitializer());
    }

    public void loop(){
        float beginTime = (float) glfwGetTime();
        float endTime;
        float dt = -1.0f;

        Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
        Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");
        while(!glfwWindowShouldClose(glfwWindow)){
            //poll event
            glfwPollEvents();

            //render pass 1, render to picking texture
            glDisable(GL_BLEND);
            pickingTexture.enableWriting();

            glViewport(0,0,1920,1080);
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Renderer.bindShader(pickingShader);
            currentScene.render();



            pickingTexture.disableWriting();
            glEnable(GL_BLEND);

            //render pass 2, render actual game
            DebugDraw.beginFrame();

            this.framebuffer.bind();
            glClearColor(1, 1, 1, 1);
            glClear(GL_COLOR_BUFFER_BIT);


            if(dt >=0) {
                DebugDraw.draw();
                Renderer.bindShader(defaultShader);
                if(runtimePlaying){
                    currentScene.update(dt);
                } else {
                    currentScene.editorUpdate(dt);
                }
                currentScene.render();
            }
            this.framebuffer.unbind();

            this.imguiLayer.update(dt, currentScene);
            glfwSwapBuffers(glfwWindow);
            MouseListener.endFrame();

            endTime = (float) glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime; //ensuring interruption by the system is recorded

        }
    }

    public static int getWidth(){
        return get().width;
    }

    public static int getHeight(){
        return get().height;
    }

    public static void setWidth(int newWidth){
        get().width = newWidth;
    }

    public static void setHeight(int newHeight){
        get().height = newHeight;
    }

    public static Framebuffer getFramebuffer(){
        return get().framebuffer;
    }

    public static float getTargetAspectRatio(){
        return 16.0f/9.0f;
    }

    public static ImGuiLayer getImguiLayer(){
        return get().imguiLayer;
    }

    @Override
    public void onNotify(GameObject object, Event event) {
        switch (event.type){
            case GameEngineStartPlay:
                this.runtimePlaying = true;
                currentScene.save();
                Window.changeScene(new LevelEditorSceneInitializer());
                break;
            case GameEngineStopPlay:
                this.runtimePlaying = false;
                Window.changeScene(new LevelEditorSceneInitializer());
                break;
            case LoadLevel:
                Window.changeScene(new LevelEditorSceneInitializer());
                break;
            case SaveLevel:
                currentScene.save();
                break;
        }
    }
}


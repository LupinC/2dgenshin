package editor;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import mock.GameObject;
import mock.Window;

import java.util.List;

public class SceneHeirarchyWindow {

    public void imgui(){
        ImGui.begin("Scene Heirarchy");

        List<GameObject> gameObjects = Window.getScene().getGameObjects();
        int index = 0;
        for(GameObject obj: gameObjects){
            if(!obj.doSerialization()){
                continue;
            }
            ImGui.pushID(index);
            boolean treeNodeOpen = ImGui.treeNodeEx(
                    obj.name,
                    ImGuiTreeNodeFlags.DefaultOpen |
                            ImGuiTreeNodeFlags.FramePadding |
                            ImGuiTreeNodeFlags.OpenOnArrow |
                            ImGuiTreeNodeFlags.SpanAvailWidth,
                    obj.name
            );

            ImGui.popID();

            if(treeNodeOpen){
                ImGui.treePop();
            }
            index++;
        }

        ImGui.end();
    }
}

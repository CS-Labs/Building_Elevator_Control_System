package control_logic;

import application.ControlPanel;
import engine.LogicEntity;
import engine.SceneManager;

public class BuildingControl implements LogicEntity
{

    private Cabin m_Cabin;
    private DoorControl m_DoorControl;
    private SceneManager m_Scene = new SceneManager();
    public BuildingControl()
    {
        m_Cabin = new Cabin(300,0,4,400,400);
        m_DoorControl = new DoorControl();
        m_DoorControl.openDoors();
        m_Scene.add(m_Cabin);
        m_Scene.add(m_DoorControl);
        m_Scene.activateAll();

    }

    @Override
    public void process() {
    }

}

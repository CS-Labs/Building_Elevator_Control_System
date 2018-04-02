package application;

import engine.Engine;
import engine.Message;
import engine.Singleton;
import javafx.scene.layout.GridPane;

class ControlPanel extends GridPane
{
    private RightPanel m_RightPanel = new RightPanel(700,0);
    private LeftPanel m_LeftPanel = new LeftPanel(0,0);
    private BottomPanel m_BottomPanel = new BottomPanel(0,400);
    ControlPanel()
    {
        Engine.getMessagePump().sendMessage(new Message(Singleton.ADD_UI_ELEMENT, m_RightPanel));
        Engine.getMessagePump().sendMessage(new Message(Singleton.ADD_UI_ELEMENT, m_LeftPanel));
        Engine.getMessagePump().sendMessage(new Message(Singleton.ADD_UI_ELEMENT, m_BottomPanel));
    }

    // The reason the control panel is broken up into three different components is because two of them
    // are invisible during the overview mode.
    public void switchToOverView()
    {
        Engine.getMessagePump().sendMessage(new Message(Singleton.REMOVE_UI_ELEMENT, m_RightPanel));
        Engine.getMessagePump().sendMessage(new Message(Singleton.REMOVE_UI_ELEMENT, m_LeftPanel));
    }

    public void switchToCCTVView()
    {
        Engine.getMessagePump().sendMessage(new Message(Singleton.ADD_UI_ELEMENT, m_RightPanel));
        Engine.getMessagePump().sendMessage(new Message(Singleton.ADD_UI_ELEMENT, m_LeftPanel));
    }



}

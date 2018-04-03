package application;

import engine.Engine;
import engine.Message;
import engine.Singleton;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.ArrayList;

public class ControlPanel extends GridPane
{
    private RightPanel m_RightPanel = new RightPanel(700,0);
    private LeftPanel m_LeftPanel = new LeftPanel(0,0);
    private GridPane m_BottomPanel = new GridPane();
    private BottomPanelController bottomController;
    private ArrayList<Integer> m_ManuallySelectedFloorNumbers = new ArrayList<>();
    ControlPanel()
    {
        m_BottomPanel.setLayoutX(0);
        m_BottomPanel.setLayoutY(400);
        bottomController = new BottomPanelController();
        _addFXMLCode1();
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
    
    //Add FXML code.
    private void _addFXMLCode1()
    {
      Parent page = null;
      try
      {
        FXMLLoader loader = new FXMLLoader(ControlPanel.class.getResource("/resources/fxml/bottomPanel.fxml"));
        loader.setController(bottomController);
        page = loader.load();
      } catch (IOException e)
      {
        e.printStackTrace();
      }
      m_BottomPanel.getChildren().setAll(page);
    }

}

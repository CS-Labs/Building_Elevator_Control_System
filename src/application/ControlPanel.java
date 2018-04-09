package application;

import control_logic.FloorNumberTypes;
import engine.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ControlPanel extends GridPane
{
    private Helper m_Helper = new Helper();
    private ArrayList<Integer> m_FloorsButtonsPressed = new ArrayList<>(); // TODO: change to call buttons once implemented.
    private boolean m_AlarmOn = false;
    private ViewTypes m_CurrentView = ViewTypes.ELEVATOR_ONE; // TODO: Decide what the first view should be.
    private ArrayList<Boolean> m_LockedPanels = new ArrayList<>(Arrays.asList(false, false, false, false));
    private boolean m_KeyActivated = false;
    private SceneManager m_ElevatorViewMgr = new SceneManager(); // Only on screen when viewing inside elevators.
    private SceneManager m_SystemOverviewMgr = new SceneManager(); // Only on screen when viewing system overview.
    ControlPanel()
    {
        GridPane constantPanel = m_SetupConstantPanel();
        // TODO: Create panel that is specific to the system overview here.
        //m_SystemOverviewMgr.add();
        m_SystemOverviewMgr.add(constantPanel);
        m_ElevatorViewMgr.add(new ElevatorPanel(700,0));
        m_ElevatorViewMgr.add(constantPanel);
        m_ElevatorViewMgr.activateAll();
        m_signalInterests();
    }

    // The reason the control panel is broken up into three different components is because two of them
    // are invisible during the overview mode.
    public void switchToOverView()
    {
        m_SystemOverviewMgr.activateAll();
        m_ElevatorViewMgr.deactivateAll();
    }

    public void switchToCabinView()
    {
        m_ElevatorViewMgr.activateAll();
        m_SystemOverviewMgr.deactivateAll();
    }

    private void m_signalInterests()
    {
        Engine.getMessagePump().signalInterest(ControlPanelGlobals.MANUAL_FLOOR_PRESS, m_Helper);
        Engine.getMessagePump().signalInterest(ControlPanelGlobals.ALARM_ON, m_Helper);
        Engine.getMessagePump().signalInterest(ControlPanelGlobals.ALARM_OFF, m_Helper);
        Engine.getMessagePump().signalInterest(ControlPanelGlobals.CHANGE_VIEW, m_Helper);
        Engine.getMessagePump().signalInterest(ControlPanelGlobals.LOCK_PANEL_UPDATE, m_Helper);
        Engine.getMessagePump().signalInterest(ControlPanelGlobals.KEY_LOCK_ACTIVATED, m_Helper);
        Engine.getMessagePump().signalInterest(ControlPanelGlobals.KEY_LOCK_DEACTIVATED, m_Helper);
    }


    private GridPane m_SetupConstantPanel()
    {
        GridPane pane = new GridPane();
        pane.setLayoutX(0);
        pane.setLayoutY(400);
        SystemOverviewPanel systemOverviewPanel = new SystemOverviewPanel();
        m_AddSystemOverviewFXMLCode(systemOverviewPanel, pane);
        return pane;
    }


    //Add FXML code.
    private void m_AddSystemOverviewFXMLCode(SystemOverviewPanel controller, GridPane pane)
    {
      Parent page = null;
      try
      {
        FXMLLoader loader = new FXMLLoader(ControlPanel.class.getResource("/resources/fxml/systemOverviewPanel.fxml"));
        loader.setController(controller);
        page = loader.load();
      } catch (IOException e)
      {
        e.printStackTrace();
      }
        pane.getChildren().setAll(page);
    }

    // Return an updated snap-shot of what has happened since the last poll in the control panel.
    // No values should be passed by reference to the snap-shot.
    public ControlPanelSnapShot getSnapShot()
    {
        ControlPanelSnapShot snapShot = new ControlPanelSnapShot(new ArrayList<>(m_FloorsButtonsPressed),
                m_AlarmOn, new ArrayList<>(m_LockedPanels), m_CurrentView, m_KeyActivated);
        m_FloorsButtonsPressed.clear();
        return snapShot;
    }



    class Helper implements MessageHandler
    {
        @Override
        public void handleMessage(Message message)
        {
            switch(message.getMessageName()) {

                case ControlPanelGlobals.MANUAL_FLOOR_PRESS:
                    m_FloorsButtonsPressed.add(((FloorNumberTypes) message.getMessageData()).toDigit());
                    break;
                case ControlPanelGlobals.ALARM_ON:
                    m_AlarmOn = true;
                    break;
                case ControlPanelGlobals.ALARM_OFF:
                    m_AlarmOn = false;
                    break;
                case ControlPanelGlobals.CHANGE_VIEW:
                    m_CurrentView = (ViewTypes) message.getMessageData();
                    break;
                case ControlPanelGlobals.LOCK_PANEL_UPDATE:
                    m_LockedPanels = (ArrayList<Boolean>) message.getMessageData();
                    break;
                case ControlPanelGlobals.KEY_LOCK_ACTIVATED:
                    m_KeyActivated = true;
                    break;
                case ControlPanelGlobals.KEY_LOCK_DEACTIVATED:
                    m_KeyActivated = false;
                    break;
                default:
                    throw new IllegalArgumentException("Unhandled Message Received.");

            }
        }

    }

}

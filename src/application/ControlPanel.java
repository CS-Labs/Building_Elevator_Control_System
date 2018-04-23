package application;



import control_logic.CallButtons;
import control_logic.ControlLogicGlobals;
import javafx.util.Pair;
import named_types.DirectionType;
import named_types.FloorNumber;
import named_types.ViewTypes;
import engine.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class ControlPanel extends GridPane
{
    private Helper m_Helper = new Helper();
    private HashSet<FloorNumber> m_FloorsButtonsPressed = new HashSet<>();
    private boolean m_AlarmPressed = false;
    private ViewTypes m_CurrentView = ViewTypes.OVERVIEW;
    private ArrayList<Boolean> m_LockedPanels = new ArrayList<>(Arrays.asList(false, false, false, false));
    private boolean m_KeyChange = false;
    private SceneManager m_ElevatorViewMgr = new SceneManager(); // Only on screen when viewing inside elevators.
    private SceneManager m_SystemOverviewMgr = new SceneManager(); // Only on screen when viewing system overview.
    private ArrayList<Pair<CallButtons, CallButtons>> m_UpDownEvents = new ArrayList<>();
    ControlPanel()
    {
        m_UpDownEvents.add(new Pair<>(new CallButtons(ControlLogicGlobals.MINFLOOR, DirectionType.UP), new CallButtons(ControlLogicGlobals.MINFLOOR, DirectionType.DOWN)));
        for(int f = ControlLogicGlobals.MINFLOOR.get() + 1; f <= (ControlLogicGlobals.MAXFLOOR.get() - 1); f++){
            m_UpDownEvents.add(new Pair<>(new CallButtons(new FloorNumber(f), DirectionType.UP),new CallButtons(new FloorNumber(f), DirectionType.DOWN)));
        }
        m_UpDownEvents.add(new Pair<>(new CallButtons(ControlLogicGlobals.MAXFLOOR, DirectionType.UP), new CallButtons(ControlLogicGlobals.MAXFLOOR, DirectionType.DOWN)));

        GridPane constantPanel = m_SetupConstantPanel();
       // m_SystemOverviewMgr.add(new OverviewPanel(0,0));
        m_SystemOverviewMgr.add(new FloorRequestPanel(400,0));
        m_ElevatorViewMgr.add(new ElevatorPanel(400,0));
        // Note, the bottom panel never changes so we don't need to add it to a scene manager.
        Engine.getMessagePump().sendMessage(new Message(Singleton.ADD_UI_ELEMENT, constantPanel));
        m_SystemOverviewMgr.activateAll();
        m_signalInterests();
    }

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
        Engine.getMessagePump().signalInterest(ControlPanelGlobals.ALARM_PRESS, m_Helper);
        Engine.getMessagePump().signalInterest(ControlPanelGlobals.CHANGE_VIEW, m_Helper);
        Engine.getMessagePump().signalInterest(ControlPanelGlobals.LOCK_PANEL_UPDATE, m_Helper);
        Engine.getMessagePump().signalInterest(ControlPanelGlobals.KEY_LOCK_CHANGE, m_Helper);
        Engine.getMessagePump().signalInterest(ControlPanelGlobals.MANAGER_UP, m_Helper);
        Engine.getMessagePump().signalInterest(ControlPanelGlobals.MANAGER_DOWN, m_Helper);
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
        ArrayList<Pair<CallButtons,CallButtons>> upDownEvents = new ArrayList<>(m_UpDownEvents);
        for(int i = 0; i < 10; i += 1){
            upDownEvents.add(i,new Pair<>(m_UpDownEvents.get(i).getKey().makeCopy(),m_UpDownEvents.get(i).getValue()));
        }
        ControlPanelSnapShot snapShot = new ControlPanelSnapShot(new HashSet<>(m_FloorsButtonsPressed),
                m_AlarmPressed, new ArrayList<>(m_LockedPanels), m_CurrentView, m_KeyChange, upDownEvents);
        resetStatus();
        return snapShot;
    }

    private void resetStatus()
    {
        m_FloorsButtonsPressed.clear();
        m_AlarmPressed = false;
        m_KeyChange = false;
        for(Pair<CallButtons, CallButtons> buttons: m_UpDownEvents)
        {
            buttons.getKey().setButtonPressedState(false);
            buttons.getValue().setButtonPressedState(false);
        }
    }


    class Helper implements MessageHandler
    {
        @Override
        public void handleMessage(Message message)
        {
            switch(message.getMessageName()) {

                case ControlPanelGlobals.MANUAL_FLOOR_PRESS:
                    m_FloorsButtonsPressed.add(((FloorNumber) message.getMessageData()));
                    break;
                case ControlPanelGlobals.ALARM_PRESS:
                    m_AlarmPressed = true;
                    break;
                case ControlPanelGlobals.CHANGE_VIEW:
                    m_CurrentView = (ViewTypes) message.getMessageData();
                    break;
                case ControlPanelGlobals.LOCK_PANEL_UPDATE:
                    m_LockedPanels = (ArrayList<Boolean>) message.getMessageData();
                    break;
                case ControlPanelGlobals.KEY_LOCK_CHANGE:
                    m_KeyChange = true;
                    break;
                case ControlPanelGlobals.MANAGER_UP:
                    FloorNumber upFloor = (FloorNumber) message.getMessageData();
                    m_UpDownEvents.get(upFloor.get()).getKey().setButtonPressedState(true);
                    break;
                case ControlPanelGlobals.MANAGER_DOWN:
                    FloorNumber downFloor = (FloorNumber) message.getMessageData();
                    m_UpDownEvents.get(downFloor.get()).getValue().setButtonPressedState(true);
                    break;
                default:
                    throw new IllegalArgumentException("Unhandled Message Received.");

            }
        }

    }

}

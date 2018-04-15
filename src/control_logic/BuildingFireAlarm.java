package control_logic;

import java.net.URL;
import java.util.Random;

import application.ControlPanelGlobals;
import engine.Engine;
import engine.Message;
import engine.MessageHandler;
import javafx.scene.media.AudioClip;

public class BuildingFireAlarm
{
    private Helper m_Helper = new Helper();
    private boolean m_AlarmOn = false; 
    String alarmSound = "/resources/sounds/fire_alarm.wav";
    URL url;
    AudioClip sound;
    boolean init = true;
    Random rand = new Random();
    
    BuildingFireAlarm()
    {
      m_signalInterests();
    }
    
    private void play(boolean init)
    {
      if(init)
      {
        url = BuildingFireAlarm.class.getResource(alarmSound);
        sound = new AudioClip(url.toExternalForm());
        sound.setCycleCount(AudioClip.INDEFINITE);
        sound.play(1, 0, 1, 0, 1);
        this.init = false;
      }
      else if(!init)
      {
        sound.stop();
        this.init = true;
      }
    }
    
    public boolean fireOccurred() 
    {
      if(!m_AlarmOn)
      {
        //1/100 chance that the fire alarm randomly goes off.
        int randNum = rand.nextInt(100);
        if(rand.nextInt(100) == randNum)
        {
          this.init = true;
          m_AlarmOn = true;
          Engine.getMessagePump().sendMessage(new Message(ControlPanelGlobals.ALARM_ON));
        }
      }
      return m_AlarmOn;
    }
    
    public void setStatus(boolean status) 
    {
      if(status)
      {
        this.init = true;
        Engine.getMessagePump().sendMessage(new Message(ControlPanelGlobals.ALARM_ON));
      }
      else
      {
        this.init = false;
        Engine.getMessagePump().sendMessage(new Message(ControlPanelGlobals.ALARM_OFF));
      }
    }
    
    private void m_signalInterests()
    {
        Engine.getMessagePump().signalInterest(ControlPanelGlobals.ALARM_ON, m_Helper);
        Engine.getMessagePump().signalInterest(ControlPanelGlobals.ALARM_OFF, m_Helper);
    }
    
    class Helper implements MessageHandler
    {
        @Override
        public void handleMessage(Message message)
        {
            switch(message.getMessageName()) {
                case ControlPanelGlobals.ALARM_ON:
                    m_AlarmOn = true;
                    play(init);
                    break;
                case ControlPanelGlobals.ALARM_OFF:
                    m_AlarmOn = false;
                    play(init);
                    break;
                default:
                    throw new IllegalArgumentException("Unhandled Message Received.");

            }
        }

    }

}

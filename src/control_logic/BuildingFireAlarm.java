package control_logic;

import javafx.scene.media.AudioClip;

import java.net.URL;
import java.util.Random;

public class BuildingFireAlarm
{
    private final String alarmSound = "/resources/sounds/fire_alarm.wav";
    private AudioClip sound;
    private Random rand = new Random();
    private boolean alarmOn = false;
    private boolean playing = false;

    private void soundAlarm()
    {
        URL url = BuildingFireAlarm.class.getResource(alarmSound);
        sound = new AudioClip(url.toExternalForm());
        sound.setCycleCount(AudioClip.INDEFINITE);
        sound.play(1, 0, 1, 0, 1);
        playing = true;
    }
    private void stopSoundingAlarm() {
        sound.stop();
        playing = false;
    }

    void managerPressCheck(boolean managerPressed) {
        if(managerPressed) alarmOn = !alarmOn;
    }

    //If the Alarm is off with a probability of 1/10000 turn it on.
    void fireCheck() {
        if (!alarmOn & rand.nextInt(10000) == 0) alarmOn = true;
    }
    boolean isOn() {return alarmOn;}
    // Perform time step.
    void step()
    {
        if(alarmOn && !playing) soundAlarm();
        if(!alarmOn && playing) stopSoundingAlarm();
    }


}

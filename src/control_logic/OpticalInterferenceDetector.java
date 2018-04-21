package control_logic;

import application.SystemOverviewPanel;
import engine.Actor;
import engine.MouseButtonTypes;
import engine.MouseInputComponent;

import java.util.ArrayList;
import java.util.Random;

public class OpticalInterferenceDetector
{
    private final double MIN_INSIDE_X = 700;
    private final double MAX_INSIDE_X = 900;
    private final double MIN_OUTSIDE_X = 100;
    private final double MAX_OUTSIDE_X = 300;
    private final double MIN_Y = 80;
    private final double MAX_Y = 330;
    private boolean manualInterference = false;
    private Random rand = new Random();

    OpticalInterferenceDetector() {new MouseInputHandler().enableMouseInputComponent();}

    public boolean interferenceDetected() {
        if(manualInterference){
            manualInterference = false;
            return true;
        }
        // Random probability random interference occurs 1/10000
        return rand.nextInt(10000) == 0;
    }

    class MouseInputHandler extends MouseInputComponent
    {

      @Override
      public void mousePressedDown(double mouseX, double mouseY, MouseButtonTypes button)
      {
          if(mouseY > MIN_Y && mouseY < MAX_Y) {
              if ((mouseX > MIN_INSIDE_X && mouseX < MAX_INSIDE_X) || (mouseX > MIN_OUTSIDE_X && mouseX < MAX_OUTSIDE_X)) manualInterference = true;
          }
      }

      @Override
      public void mouseReleased(double mouseX, double mouseY, MouseButtonTypes button) {}
      @Override
      public void mouseMoved(double amountX, double amountY, double mouseX, double mouseY) {}
      @Override
      public void processMouseCollisionResponse(ArrayList<Actor> actors) {}
    }
}

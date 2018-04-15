package control_logic;

import java.util.ArrayList;

import engine.Actor;
import engine.MouseButtonTypes;
import engine.MouseInputComponent;
import javafx.util.Pair;
import named_types.CabinNumber;
import named_types.DoorStatusType;
import named_types.FloorNumber;

public class OpticalInterferenceDetector
{
    double minInsideX = 700;
    double maxInsideX = 900;
    double minOutsideX = 100;
    double maxOutsideX = 300;
    double minY = 80;
    double maxY = 330;
    double doorPanelWidth = 100;
    Sensors _sensors;
    
    OpticalInterferenceDetector(Sensors sensors) 
    {
      new MouseInputHandler().enableMouseInputComponent();
      _sensors = sensors;
    }
    
    public void handleInterference(double x, double y)
    {
      boolean inner = false;
      boolean outer = false;
      double percentage = 0;
      double amountOpened = 0;
      double amountToAdd = 0;
      if(x >= minInsideX && x <= maxInsideX && y >= minY && y <= maxY)
      {
        inner = true;
      }
      else if(x >= minOutsideX && x <= maxOutsideX && y >= minY && y <= maxY)
      {
        outer = true;
      }
      if(inner || outer)
      {
        CabinNumber cabinNum = _sensors.getDoorControl().getCabinNum();
        FloorNumber floorNum = _sensors.getDoorControl().getFloorNum();
        //System.out.println(_sensors.getDoorControl().getStatus(floorNum, cabinNum));
        if(_sensors.getDoorControl().getCurrStatus(cabinNum.get()) == DoorStatusType.CLOSING)
        {
          Pair<Double, Double> openPercentage = _sensors.getDoorControl().getInnerOuterDoorPercentageOpen(floorNum, cabinNum);
          percentage = openPercentage.getKey();
          if(percentage > 0.0)
          {
            amountOpened = percentage * doorPanelWidth;
            amountToAdd = doorPanelWidth - amountOpened;
            if(inner)
            {
              if(x >= (minInsideX + amountToAdd) && x <= (maxInsideX - amountToAdd))
              {
                _sensors.getDoorControl().interferenceDetected(true, cabinNum.get());
              }
            }
            else if(outer)
            {
              if(x >= (minOutsideX + amountToAdd) && x <= (maxOutsideX - amountToAdd))
              {
                _sensors.getDoorControl().interferenceDetected(true, cabinNum.get());
              }
            }
          }
        }
      }
    }
    
    class MouseInputHandler extends MouseInputComponent
    {

      @Override
      public void mousePressedDown(double mouseX, double mouseY,
          MouseButtonTypes button)
      {
          handleInterference(mouseX, mouseY);
      }

      @Override
      public void mouseReleased(double mouseX, double mouseY,
          MouseButtonTypes button)
      {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void mouseMoved(double amountX, double amountY, double mouseX,
          double mouseY)
      {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void processMouseCollisionResponse(ArrayList<Actor> actors)
      {
        // TODO Auto-generated method stub
        
      }
      
    }
}

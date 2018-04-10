package application;

import engine.RenderEntity;
import engine.SceneManager;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class OverviewPanel extends GridPane
{
  //Height and width of panel.
  int width = 1000;
  int height = 400;
  //Fill this with the x,y coords of the upper left corners of the rectangles.
  int coords[][][] = new int[10][8][2];
  //Fill this with the width and height of the rectangles.
  int dimensions[][][] = new int[10][8][2];
  Canvas canvas;
  private static GraphicsContext gfx;
  StackPane stackPane;
  Group root;
  Rectangle elevator1;
  Rectangle elevator2;
  Rectangle elevator3;
  Rectangle elevator4;
  private SceneManager elevatorManager = new SceneManager();
  
  public OverviewPanel(int x, int y)
  {
    overviewScene();
    this.getChildren().add(root);
    this.setLayoutX(x);
    this.setLayoutY(y);
    drawOverview();
    drawLines();
    elevator1 = new Rectangle(210, 360, 40, 40);
    elevator2 = new Rectangle(460, 360, 40, 40);
    elevator3 = new Rectangle(710, 360, 40, 40);
    elevator4 = new Rectangle(960, 360, 40, 40);
    root.getChildren().addAll(elevator1,elevator2,elevator3,elevator4);
    addElevators();
    elevatorManager.activateAll();
  }
  
  private void addElevators()
  {
    elevatorManager.add(new ElevatorEntity(elevator1, 1));
    elevatorManager.add(new ElevatorEntity(elevator2, 2));
    elevatorManager.add(new ElevatorEntity(elevator3, 3));
    elevatorManager.add(new ElevatorEntity(elevator4, 4));
  }
  
  private void overviewScene()
  {
    root = new Group();
    canvas = new Canvas(width, height);
    gfx = canvas.getGraphicsContext2D();
    stackPane = new StackPane(canvas);
    root.getChildren().add(stackPane);
    stackPane.setPrefWidth(width);
    stackPane.setPrefHeight(height);
  }
  
  public void drawLines()
  {
    int startX = 0;
    int startY = 40;
    int endX = 1000;
    for(int i = 0; i < 9; i ++)
    {
      Line line = new Line(startX, startY, endX, startY);
      root.getChildren().add(line);
      startY += 40;
    }
  }
  
  public void drawOverview()
  {
    Color buildingColor = Color.BISQUE;
    Color shaftColor = Color.DARKGRAY;
    int cornerX = 0;
    int cornerY = 0;
    int dimensionsX = 210;
    int dimensionsY = 40;
    for(int i = 0; i < 10; i ++)
    {
      if(i > 0)
      {
        cornerX = 0;
        cornerY += 40;
        dimensionsX = 210;
      }
      for(int j = 0; j < 8; j ++)
      {
        if((j > 0) && !((j % 2) == 0))
        {
          cornerX += 210;
          dimensionsX = 40;
          gfx.setFill(shaftColor);
          gfx.fillRect(cornerX, cornerY, dimensionsX, dimensionsY);
        }
        else if((j > 0) && ((j % 2) == 0))
        {
          cornerX += 40;
          dimensionsX = 210;
          gfx.setFill(buildingColor);
          gfx.fillRect(cornerX, cornerY, dimensionsX, dimensionsY);
        }
        else if(j == 0)
        {
          gfx.setFill(buildingColor);
          gfx.fillRect(cornerX, cornerY, dimensionsX, dimensionsY);
        }
      }
    }
  }
  
  public double moveElevator(int elevNum, String direction)
  {
    double amountToMove = 0.5;
    if(direction.equals("UP"))
    {
      amountToMove *= -1;
    }
    if(elevNum == 1)
    {
      double yPos = elevator1.getY();
      double newPos = yPos + amountToMove;
      elevator1.setY(newPos);
      return newPos;
    }
    else if(elevNum == 2)
    {
      double yPos = elevator2.getY();
      double newPos = yPos + amountToMove;
      elevator2.setY(newPos);
      return newPos;
    }
    else if(elevNum == 3)
    {
      double yPos = elevator3.getY();
      double newPos = yPos + amountToMove;
      elevator3.setY(newPos);
      return newPos;
    }
    else if(elevNum == 4)
    {
      double yPos = elevator4.getY();
      double newPos = yPos + amountToMove;
      elevator4.setY(newPos);
      return newPos;
    }
    return 0;
  }
  
  class ElevatorEntity extends RenderEntity
  {
    int elevatorNum;
    Rectangle elevator;
    String currDirection = "UP";
    
    public ElevatorEntity(Rectangle elevator, int elevatorNum)
    {
      this.elevatorNum = elevatorNum;
      this.elevator = elevator;
    }
    
    @Override
    public void pulse(double deltaSeconds)
    {
      move();
    }
    
    private void move()
    {
      if(elevator.getY() <= 0.0)
      {
        this.currDirection = "DOWN";
      }
      else if(elevator.getY() >= 360.0)
      {
        this.currDirection = "UP";
      }
      moveElevator(elevatorNum, currDirection);
    }
    
  }
}

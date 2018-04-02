package application;

import control_logic.BuildingControl;
import engine.ApplicationEntryPoint;
import engine.EngineLoop;

// lol
public class SetUp implements ApplicationEntryPoint {

    @Override
    public void init() {
        System.out.println("Initialized");
        new ControlPanel();
        new BuildingControl();

    }

    @Override
    public void shutdown() {

    }



    public static void main(String[] args) {
        SetUp app = new SetUp();
        EngineLoop loop = new EngineLoop();
        loop.start(app, args);

    }
}

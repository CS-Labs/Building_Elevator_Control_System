package application;

import engine.ApplicationEntryPoint;
import engine.EngineLoop;

// lol
public class ExampleApplication implements ApplicationEntryPoint {
    @Override
    public void init() {
        System.out.println("Initialized");
    }

    @Override
    public void shutdown() {

    }

    public static void main(String[] args) {
        ExampleApplication app = new ExampleApplication();
        EngineLoop loop = new EngineLoop();
        loop.start(app, args);
    }
}

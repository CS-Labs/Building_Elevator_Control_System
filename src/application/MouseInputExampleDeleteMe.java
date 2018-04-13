package application;

import engine.*;

import java.util.ArrayList;

class MouseInputHandler extends MouseInputComponent {

    @Override
    public void mousePressedDown(double mouseX, double mouseY, MouseButtonTypes button) {
        System.out.println(button + " Down: " + mouseX + ", " + mouseY);
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, MouseButtonTypes button) {
        System.out.println(button + " Up: " + mouseX + ", " + mouseY);
    }

    @Override
    public void mouseMoved(double amountX, double amountY, double mouseX, double mouseY) {
        System.out.println("Moved: " + amountX + ", " + amountY);
    }

    // This won't be called yet, but when it is then it will let you see
    // what the mouse click collided with in the scene
    @Override
    public void processMouseCollisionResponse(ArrayList<Actor> actors) {

    }
}

// lol
public class MouseInputExampleDeleteMe implements ApplicationEntryPoint {
    @Override
    public void init() {
        new MouseInputHandler().enableMouseInputComponent();
    }

    @Override
    public void shutdown() {

    }

    public static void main(String ... args) {
        MouseInputExampleDeleteMe app = new MouseInputExampleDeleteMe();
        EngineLoop loop = new EngineLoop();
        loop.start(app, args);
    }
}

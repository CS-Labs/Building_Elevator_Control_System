package engine;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;

/**
 * Provides a way to start the engine loop.
 */
public class EngineLoop {
    private volatile boolean _waitingForEngineInit;
    /**
     * Starts the engine's main loop - this will not return until the engine
     * has shutdown.
     * @param application application to associate with the current instance of the engine
     * @param cmdArgs command line arguments
     */
    public void start(ApplicationEntryPoint application, String[] cmdArgs) {
        _waitingForEngineInit = true;
        Engine engine = new Engine();
        JFXPanel panel = new JFXPanel(); // Forces JavaFX to initialize itself
        Platform.runLater(() ->
        {
           engine.start(application);
           _waitingForEngineInit = false;
        });
        // Spin while we're waiting for engine init
        while (_waitingForEngineInit) {
            try {
                Thread.sleep(1);
            }
            catch (Exception e) {
                // Do nothing
            }
        }
        // Now spin while the engine is running
        while (engine._isEngineRunning()) {
            try {
                Thread.sleep(1);
            }
            catch (Exception e) {
                // Do nothing
            }
        }
    }
}

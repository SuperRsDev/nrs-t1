package ba.unsa.etf.rpr;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class ControllerLoader {
    public static void createPretragaController(URL resource, PretragaController controller,
                                               EventHandler<WindowEvent> onHideCallback) {
        createController("Vlasnik", resource, controller, onHideCallback);
    }

    private static void createController(String title, URL resource, Object controller,
                                         EventHandler<WindowEvent> onHideCallback) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(resource);
            loader.setController(controller);
            root = loader.load();
            stage.setTitle(title);
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(false);
            stage.show();

            stage.setOnHiding(onHideCallback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getStageCloseFromButton(Button button) {
        // get a handle to the stage
        Stage stage = (Stage) button.getScene().getWindow();
        // do what you have to do
        stage.close();
    }
}
package mainMethod;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ApplicationWindow extends Application {
	

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		String path = "/view/MainView.fxml";
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource(path));
		
		Parent root = loader.load();
    	primaryStage.setTitle("Tracking Data Analysis Tool");
    	primaryStage.setScene(new Scene(root, 1050, 700));
    	primaryStage.show();
		
	}

}
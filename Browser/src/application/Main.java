package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class Main extends Application {
	protected static Class<? extends Application> parentClass;
	protected static Stage parentStage;
	protected static KeyCombination newWindow = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN);
	protected static KeyCombination newTab = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
	protected static BrowserSettingsManager settings = BrowserSettingsManager.getController();
	
	@Override
	public void start(Stage primaryStage) {
		parentStage = primaryStage;
		parentClass = getClass();
		primaryStage = new Stage();
		
		makeWindow();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static AnchorPane makeBrowser() {
		try {
			return FXMLLoader.load(Main.parentClass.getResource("BrowserLayout.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void makeWindow() {
		try {
			Stage stage = new Stage();
			Scene scene = new Scene(makeBrowser());
			
			scene.addEventFilter(KeyEvent.KEY_PRESSED,(KeyEvent event) -> {
				if(newWindow.match(event)) {
					makeWindow();
				}
			});
			
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent arg0) {
					if(!settings.isMaximized()) {
            			settings.setSize(stage.getWidth(), stage.getHeight());
            		}
            		stage.close();
				}
			});
			
			ChangeListener<? super Boolean> maxListener = (observable, oldValue, newValue) -> {
				settings.setMaximized(newValue);
			};
			
			stage.maximizedProperty().addListener(maxListener);
			
			stage.setMaximized(settings.isMaximized());
			
			stage.getIcons().add(new Image(parentClass.getResourceAsStream("icon.png")));
			
			scene.getStylesheets().add(parentClass.getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.show();
			if(!settings.isMaximized()) {
				stage.setWidth(settings.getWidth());
				stage.setHeight(settings.getHeight());
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}

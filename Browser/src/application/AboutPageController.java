package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

public class AboutPageController implements Initializable {
	protected static BrowserSettingsManager settings = BrowserSettingsManager.getController();
	
	@FXML
	Text aboutText;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		aboutText.setText(settings.getAbout());
	}

}

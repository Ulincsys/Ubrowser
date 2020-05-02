package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.ListView;

public class HistoryPageController implements Initializable {
	BrowserSettingsManager settings = BrowserSettingsManager.getController();
	
	@FXML
	ListView historyList;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		historyList.getItems().addAll(settings.getHistory());
		
		historyList.setCursor(Cursor.HAND);
	}
}

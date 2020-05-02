package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import ulincsys.pythonics.Pythonics;

public class SettingsPageController implements Initializable {
	
	BrowserSettingsManager settings = BrowserSettingsManager.getController();
	
	@FXML
	AnchorPane root;
	
	@FXML
	ChoiceBox<SearchEngine> searchEngines;
	
	@FXML
	TextField homePage;
	
	@FXML
	CheckBox maxBox;
	
	@FXML
	Button addSearch;
	
	@FXML
	private void setHome() {
		if(homePage.getText().length() == 0) {
			settings.setHome("ubrowser:blank");
			homePage.setText("ubrowser:blank");
		} else {
			settings.setHome(homePage.getText());
		}
	}
	
	@FXML
	private void setMax() {
		settings.setMaximized(!settings.isMaximized());
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		for(SearchEngine engine : settings.engines()) {
			searchEngines.getItems().add(engine);
		}
		
		searchEngines.getSelectionModel().select(settings.selectedEngine());
		
		searchEngines.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				settings.setEngine(Pythonics.Int(arg2));
			}
		});
		
		homePage.setText(settings.home());
		updateHomePageFieldSize();
		homePage.lengthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				updateHomePageFieldSize();
			}
		});
		
		if(settings.isMaximized()) {
			maxBox.fire();;
		}
	}
	
	private void updateHomePageFieldSize() {
		if(getHomePageTextWidth() >= 150) {
			homePage.setPrefWidth(getHomePageTextWidth());
		} else {
			homePage.setPrefWidth(150);
		}
	}
	
	private double getHomePageTextWidth() {
		return TextUtils.computeTextWidth(homePage.getFont(),
                homePage.getText(), 0.0D) + 10;
	}
}

/* -------------------------------------------------------------------------- */

class TextUtils {

    static final Text helper;
    static final double DEFAULT_WRAPPING_WIDTH;
    static final double DEFAULT_LINE_SPACING;
    static final String DEFAULT_TEXT;
    static final TextBoundsType DEFAULT_BOUNDS_TYPE;
    static {
        helper = new Text();
        DEFAULT_WRAPPING_WIDTH = helper.getWrappingWidth();
        DEFAULT_LINE_SPACING = helper.getLineSpacing();
        DEFAULT_TEXT = helper.getText();
        DEFAULT_BOUNDS_TYPE = helper.getBoundsType();
    }

    public static double computeTextWidth(Font font, String text, double help0) {
        helper.setText(text);
        helper.setFont(font);

        helper.setWrappingWidth(0.0D);
        helper.setLineSpacing(0.0D);
        double d = Math.min(helper.prefWidth(-1.0D), help0);
        helper.setWrappingWidth((int) Math.ceil(d));
        d = Math.ceil(helper.getLayoutBounds().getWidth());

        helper.setWrappingWidth(DEFAULT_WRAPPING_WIDTH);
        helper.setLineSpacing(DEFAULT_LINE_SPACING);
        helper.setText(DEFAULT_TEXT);
        return d + 10;
    }
}
















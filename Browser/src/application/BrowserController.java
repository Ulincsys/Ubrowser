package application;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebHistory;
import javafx.stage.Stage;

public class BrowserController implements Initializable, PropertyChangeListener {
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
//	private double currX, currY, prevWidth;
//	
//	private boolean isMaximized;

	private ArrayList<TabManager> openTabs;
	
	private ArrayList<TabManager> closedTabs;
	
	private BrowserSettingsManager settings = BrowserSettingsManager.getController();
	
//	private int lowerWidth = 400, lowerHeight = 600;
	@FXML
	private AnchorPane root;
	@FXML
	private TabPane mainTabs;
	@FXML
	private TextField addressBar;
	@FXML
	private Label loadLabel;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Button newTabButton, backButton, refreshButton;
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	@FXML
	private void handleRefresh(ActionEvent event) {
		refreshPage();
	}
	@FXML
	private void handleSearch(ActionEvent event) {
		if(getCurrentTab().isBuiltin()) {
			getCurrentTab().restore();
		}
		String url = addressBar.getText().trim();
		if(settings.builtin(url)) {
			url = url.toLowerCase();
			if(url.startsWith("ubrowser:settings")) {
				getCurrentTab().displaySettingsPage();
			} else if(url.startsWith("ubrowser:error")) {
				getCurrentTab().displayErrorPage("Error page requested.");
			} else if(url.startsWith("ubrowser:blank")) {
				getCurrentTab().blank();
			} else if(url.startsWith("ubrowser:history")) {
				getCurrentTab().displayHistory();
			} else if(url.startsWith("ubrowser:about")) {
				getCurrentTab().displayAbout();
			}
			return;
		}
		url = formatURL(url);
		setActiveSearch(url);
		settings.addHistory(url);
	}
	@FXML
	private void handleNewTab(ActionEvent event) {
		openTab(settings.home());
		selectLastTab();
	}
	@FXML
	private void handleUndoClose(ActionEvent event) {
		undoClose();
	}
	@FXML
	private void handleBack(ActionEvent event) {
		goBackMain();
	}
	@FXML
	private void handleExit() {
		System.exit(0);
	}
	@FXML
	private void loadHistory() {
		openTab("ubrowser:history");
	}
	@FXML
	private void loadSettings() {
		openTab("ubrowser:settings");
	}
	@FXML
	private void loadAbout() {
		openTab("ubrowser:about");
	}
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		openTabs = new ArrayList<>();
		closedTabs = new ArrayList<>();
		
		addMainListeners();
		
		openTab(formatURL(settings.home()));
		
		setAddressText();
    }
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	public void addMainListeners() {
		ChangeListener<Number> resizeListener = (observable, oldValue, newValue) -> {
	    	updateResize();
		};
	    
    	ChangeListener<? super Tab> changeTabListener = (observable, oldValue, newValue) -> {
    		setAddressText();
    		setStageTitle();
    	};

		mainTabs.widthProperty().addListener(resizeListener);
		mainTabs.heightProperty().addListener(resizeListener); 
		
		mainTabs.getSelectionModel().selectedItemProperty().addListener(changeTabListener);
		
		mainTabs.setOnMousePressed((new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if(event.isMiddleButtonDown() && event.getTarget().toString().contains("tab-header-background")) {
					handleNewTab(null);
				}
			}
		}));
		
		root.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
			if(Main.newTab.match(event)) {
				openTab(settings.home());
			}
		});
	}
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	public boolean isHeaderTarget(String parse) {
		return parse.contains("TabHeader") || parse.contains("tab-header") || parse.contains("Text[text=");
	}
	
	public void setLoadListener(EngineManager engine) {
		Worker<Void> worker = engine.getLoadWorker();
		
		worker.stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
				if(!getCurrentTab().isBuiltin())
					if (newValue == Worker.State.SUCCEEDED) {
						progressBar.setVisible(false);
						loadLabel.setText("");
						setAddressText();
						setStageTitle();
					} else if(newValue == Worker.State.FAILED){
						loadLabel.setText("Failed loading page\n");
						setAddressText();
						progressBar.setVisible(false);
						displayErrorPage();
					} else if(newValue == Worker.State.RUNNING) {
						progressBar.setVisible(true);
						loadLabel.setText("Loading page");
					}
			}
		});
		
		progressBar.progressProperty().bind(worker.progressProperty());
		
		
	}
	
	/* ------------------------------------------------------------------------------------------------------------ */
	
	private void setStageTitle() {
		setStageTitle(getCurrentEngine().getTitle());
	}
	
	private Stage getStage() {
		return (Stage)addressBar.getScene().getWindow();
	}
	
	private void setStageTitle(String title) {
		try {
			getStage().setTitle(title);
		} catch(Exception e) {
			return;
		}
	}
	
	private void setActiveSearch(String URL) {
		getCurrentEngine().load(URL);
	}
	
	private void setAddressText() {
		addressBar.setText(getCurrentAddress());
	}
	
	private String formatURL(String url) {
		if(settings.builtin(url)) {
			return url;
		} else if(url.length() == 0) {
			return "";
		} else if(url.contains(" ") || !url.contains(".")) {
			url = settings.search().getURL() + url.replace(" ", settings.search().getJoin());
		} else {
			if(!(url.toLowerCase().contains("http://") || url.toLowerCase().contains("https://"))) {
				url = "http://" + url;
			}
		}
		
		return url;
	}
	
	private void openTab(String URL) {
		settings.addHistory(URL);
		TabManager tab = new TabManager(URL);
		openTabs.add(tab);
		tab.addListener(this);
		
		tab.setSize(getWidth(), getHeight());
		
		setLoadListener(tab.getEngine());
		
		tab.getTab().setOnCloseRequest(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
            	mainTabs.getTabs().remove(tab.getTab());
            	openTabs.remove(tab);
            	closedTabs.add(tab);
            	if(openTabs.size() == 0) {
            		if(!settings.isMaximized()) {
            			settings.setSize(getWidth(), getHeight());
            		}
            		getStage().close();
            	}
            	
            	tab.blank();
            	
                t.consume();
            }
        });
		
		mainTabs.getTabs().add(tab.getTab());
	}
	
	public void goBackMain() {
		if(currentController().isBuiltin()) {
			currentController().restore();
			loadLabel.setText("");
			return;
		}
		
		WebHistory backwards = getCurrentEngine().getHistory();
		
		if(backwards.getCurrentIndex() == 0)
			return;
		
		backwards.go(-1);
	}
	
	public void refreshPage() {
		getCurrentEngine().reload();
	}
	
	public void undoClose() {
		if(closedTabs.size() < 1) {
			return;
		}
		
		TabManager temp = closedTabs.get(closedTabs.size() - 1);
		
		openTabs.add(temp);
		closedTabs.remove(temp);
		mainTabs.getTabs().add(temp.getTab());
		
		WebHistory backwards = temp.getEngine().getHistory();
		if(backwards.getCurrentIndex() == 0)
			return;
		backwards.go(-1);
		
		temp.getEngine().reload();
	}
	
	private void updateResize() {
		for(TabManager tab : openTabs) {
			tab.setSize(getStage().getWidth(), getStage().getHeight());
		}
	}
	
	private TabManager getCurrentTab() {
		int currentTabIndex = getCurrentIndex();
		if(currentTabIndex < 0) {
			handleExit();
		} else
			return openTabs.get(currentTabIndex);
		
		return null;
	}
	
	private EngineManager getCurrentEngine() {
		int currentTabIndex = getCurrentIndex();
		if(currentTabIndex < 0) {
			handleExit();
		} else
			return openTabs.get(currentTabIndex).getEngine();
		
		return null;
	}
	
	private int getCurrentIndex() {
		return mainTabs.getSelectionModel().getSelectedIndex();
	}
	
	private TabManager currentController() {
		try {
			return openTabs.get(getCurrentIndex());
		} catch (Exception e) {
			handleExit();
			return null;
		}
	}
	
	private String getCurrentAddress() {
		if(getCurrentTab().isBuiltin()) {
			return getCurrentTab().builtinUrl();
		}
		return getCurrentEngine().getLocation();
	}
	
	private double getWidth() {
		return mainTabs.getWidth();
	}
	
	private double getHeight() {
		return mainTabs.getHeight();
	}
	
	private void displayErrorPage() {
		openTabs.get(getCurrentIndex()).displayErrorPage(addressBar.getText());
	}
	
	private void setSelectedTab(int index) {
		if(index < openTabs.size() && index >= 0) {
			mainTabs.getSelectionModel().select(index);
		} else if((index + openTabs.size()) > 0 && (index + openTabs.size()) < openTabs.size()) {
			mainTabs.getSelectionModel().select(index + openTabs.size());
		}
	}
	
	private void selectLastTab() {
		setSelectedTab(-1);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().contains("title")) {
			setStageTitle(evt.getNewValue().toString());
		} else if(evt.getPropertyName().contains("load")) {
			addressBar.setText(evt.getNewValue().toString());
			handleSearch(null);
		}
	}
}


























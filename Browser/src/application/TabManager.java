package application;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;

public class TabManager extends ObserverModel {
	private static BrowserSettingsManager settings = BrowserSettingsManager.getController();
	private GridPane parent;
	private GridPane errorPage;
	private AnchorPane settingsPage;
	private Tab tab;
	private WebView page;
	private EngineManager engine;
	private boolean isBuiltin;
	private String builtinTitle;
	private String builtinMessage;
	private String builtinURL;
	private String lastURL;
	
	public TabManager() {
		this(settings.home());
	}
	
	public TabManager(String url) {
		tab = new Tab();
		setupWebView(url);
		parent = new GridPane();
		parent.add(page, 0, 0);
		parent.setPadding(new Insets(35, 0, 0, 0));
		tab.setContent(parent);
		if(settings.builtin(url)) {
			if(url.startsWith("ubrowser:settings")) {
				displaySettingsPage();
			} else if(url.startsWith("ubrowser:error")) {
				displayErrorPage("Error page requested.");
			} else if(url.startsWith("ubrowser:blank")) {
				blank();
			} else if(url.startsWith("ubrowser:history")) {
				displayHistory();
			} else if(url.startsWith("ubrowser:about")) {
				displayAbout();
			}
			return;
		}
	}
	
	public void close() {
		parent = null;
		tab = null;
		page = null;
		engine = null;
	}
	
	private void setupWebView(String url) {
		page = new WebView();
		engine = new EngineManager(page.getEngine());
		engine.setJavaScriptEnabled(true);
		engine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:69.0) Gecko/20100101 Firefox/69.0");
        setLoadListener();
        
        engine.load(url);
	}
	
	private void setLoadListener() {
		Worker<Void> worker = engine.getLoadWorker();
		
		worker.stateProperty().addListener(new ChangeListener<State>() {

			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
				if(!isBuiltin()) {
					if (newValue == Worker.State.SUCCEEDED) {
						tab.setText(engine.getTitle());
						tab.setTooltip(new Tooltip(engine.getTitle()));
					}
				} else {
					tab.setText(builtinTitle);
					fireEvent("title", builtinTitle);
				}
			}
		});
	}
	
	public Tab getTab() {
		return tab;
	}

	public void setTab(Tab tab) {
		this.tab = tab;
	}

	public WebView getPage() {
		return page;
	}

	public void setPage(WebView page) {
		this.page = page;
	}

	public EngineManager getEngine() {
		return engine;
	}
	
	public double getWidth() {
		return page.getWidth();
	}
	
	public double getHeight() {
		return page.getHeight();
	}
	
	public GridPane getParent() {
		return parent;
	}
	
	public String getTitle() {
		return engine.getTitle();
	}
	
	public void setWidth(double width) {
		parent.setPrefWidth(width);
		page.setPrefWidth(width);
	}
	
	public void setHeight(double height) {
		parent.setPrefHeight(height);
		page.setPrefHeight(height);
	}
	
	public void setSize(double width, double height) {
		setWidth(width);
		setHeight(height);
	}
	
	public void displayAbout() {
		setBuiltin("About", "Information about UBrowser", "ubrowser:about");
		
		AnchorPane aboutPage;
		
		try {
			aboutPage = FXMLLoader.load(Main.parentClass.getResource("AboutPage.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
			displayErrorPage("Unable to load about page.");
			return;
		}
		
		tab.setContent(aboutPage);
		tab.setTooltip(new Tooltip(builtinMessage));
	}
	
	public void displayHistory() {
		setBuiltin("History", "View your browsing history", "ubrowser:history");
		AnchorPane historyPage;
		
		try {
			historyPage = FXMLLoader.load(Main.parentClass.getResource("HistoryPage.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
			displayErrorPage("Unable to load history page.");
			return;
		}
		
		tab.setContent(historyPage);
		tab.setTooltip(new Tooltip(builtinMessage));
		
		ListView historyList = (ListView) historyPage.getChildren().get(0);
		
		historyList.setOnMouseClicked((MouseEvent event) -> {
			int index = historyList.getSelectionModel().getSelectedIndex();
			if(index >= 0) {
				fireEvent("load", historyList.getSelectionModel().getSelectedItem().toString());
			}
		});
	}
	
	public void displayErrorPage(String message) {
		setBuiltin("Failed", "Failed loading page " + message, "ubrowser:error");
		
		
		errorPage = new GridPane();
		Label errorLabel = new Label(builtinMessage);
		
		errorLabel.setStyle("-fx-font: 64px Arial;");
		errorLabel.setAlignment(Pos.CENTER);
		
		tab.setTooltip(new Tooltip(builtinMessage));
		
		errorPage.add(errorLabel, 0, 0);
		errorPage.add(new ImageView(new Image(Main.parentClass.getResourceAsStream("notFound.png"))), 0, 1);
		errorPage.setPrefSize(getWidth(), getHeight());
		errorPage.setAlignment(Pos.CENTER);
		errorPage.setPrefSize(page.getPrefWidth(), page.getPrefWidth());
		
		tab.setContent(errorPage);
	}
	
	public void displaySettingsPage() {
		setBuiltin("Settings", "Adjust your browser settings", "ubrowser:settings");
		
		try {
			settingsPage = FXMLLoader.load(Main.parentClass.getResource("SettingsPage.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
			displayErrorPage("Unable to load settings page.");
			return;
		}
		
		tab.setContent(settingsPage);
		tab.setTooltip(new Tooltip(builtinMessage));
	}
	
	private void setBuiltin(String title, String message, String url) {
		isBuiltin = true;
		builtinTitle = title;
		builtinMessage = message;
		builtinURL = url;
		lastURL = engine.getLocation();
		engine.loadContent("");
	}
	
	public boolean isBuiltin() {
		return isBuiltin;
	}
	
	public void restore() {
		isBuiltin = false;
		tab.setContent(parent);
		engine.load(lastURL);
	}
	
	public void blank() {
		setBuiltin("Blank", "Blank", "ubrowser:blank");
	}
	
	public String builtinTitle() {
		return builtinTitle;
	}
	
	public String builtinMessage() {
		return builtinMessage;
	}
	
	public String builtinUrl() {
		return builtinURL;
	}
}


























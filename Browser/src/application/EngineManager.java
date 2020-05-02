package application;

import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;

public class EngineManager {
	public WebEngine engine;
	
	public EngineManager() {
		this(new WebEngine());
	}
	
	public EngineManager(WebEngine engine) {
		this.engine = engine;
	}
	
	public Worker<Void> getLoadWorker() {
		return engine.getLoadWorker();
	}
	
	public void setJavaScriptEnabled(boolean enabled) {
		engine.setJavaScriptEnabled(enabled);
	}
	
	public void load(String url) {
		engine.load(url);
	}
	
	public void loadContent(String content) {
		engine.loadContent(content);
	}
	
	public void setUserAgent(String userAgent) {
		engine.setUserAgent(userAgent);
	}
	
	public WebHistory getHistory() {
		return engine.getHistory();
	}
	
	public void reload() {
		engine.reload();
	}
	
	public String getLocation() {
		return engine.getLocation();
	}
	
	public String getTitle() {
		return engine.getTitle();
	}
}

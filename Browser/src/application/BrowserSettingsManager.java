package application;

import java.io.File;
import java.util.ArrayList;

import ulincsys.pythonics.Pythonics;
import ulincsys.pythonics.io.IOFormatter;

class BrowserSettingsManager {
	private static BrowserSettingsManager controller;
	
	private String settingsFile;
	private String directory;
	private String enginesFile;
	private String builtinsFile;
	private String historyFile;
	private String aboutFile;
	
	private String home;
	private int searchEngine;
	private ArrayList<SearchEngine> searchEngines;
	private ArrayList<String> builtins;
	private boolean isMax;
	private double width;
	private double height;
	
	private String defaultString =
			"home: https://www.google.com\n"
			+ "Maximized: true\n"
			+ "NonMaxWidth: 800\n"
			+ "NonMaxHeight: 600\n"
			+ "SearchEngine: 0\n";
	
	private String defaultBuiltins = 
			"ubrowser:blank\n" + 
			"ubrowser:error\n" + 
			"ubrowser:settings\n" +
			"ubrowser:newtab" +
			"ubrowser:history" +
			"ubrowser:about";
	
	private BrowserSettingsManager() {
		settingsFile = "UBrowser.ubs";
		enginesFile = "SearchEngines.ubs";
		builtinsFile = "built-ins.ubs";
		historyFile = "history.txt";
		aboutFile = "about.txt";
		home = "https://www.google.com";
		directory = System.getProperty("user.dir") + "/.config/";
		if(System.getProperty("os.name").toLowerCase().contains("windows")) {
			directory = directory.replace("\\", "/");
		}
		isMax = true;
		searchEngine = 0;
		
		searchEngines = new ArrayList<>();
		
		load();
	}
	
	/* ------------------------------------------------------------------------------------------- */
	
	private ArrayList<String> readFile(String fileName) {
		return Pythonics.getLines(directory + fileName, true);
	}
	
	private void writeFile(String fileName, ArrayList<String> lines) {
		IOFormatter format = IOFormatter.valueOf("file=" + directory + fileName, "append=false");
		format.writeLines(lines);
	}
	
	protected ArrayList<String> getHistory() {
		return readFile(historyFile);
	}
	
	protected void addHistory(String url) {
		Pythonics.print(url, "file=" + directory + historyFile);
	}
	
	private ArrayList<String> getSettings() {
		return readFile(settingsFile);
	}
	
	private void setSettings(ArrayList<String> lines) {
		writeFile(settingsFile, lines);
	}
	
	private ArrayList<String> getEngines() {
		return readFile(enginesFile);
	}
	
	private void setEngines(ArrayList<String> lines) {
		writeFile(enginesFile, lines);
	}

	protected void addEngine(String name, String url, String catChar) {
		String newEngine = "<" + catChar + "><" + name + ">" + url;
		ArrayList<String> lines = getEngines();
		lines.add(newEngine);
		setEngines(lines);
	}
	
	/* ------------------------------------------------------------------------------------------- */
	
	private void load() {
		File settingsDir = new File(directory);
		if(!settingsDir.exists()) {
			settingsDir.mkdir();
		}
		
		ArrayList<String> parse = getSettings();
		if(parse == null) {
			Pythonics.print("FATAL ERROR: unable to load settings.");
			System.exit(-1);
		}
		ArrayList<String> engines = readFile(enginesFile);
		builtins = readFile(builtinsFile);
		
		if(parse.size() == 0) {
			for(String item : defaultString.split("\n"))
				parse.add(item);
			setSettings(parse);
		} if(engines.size() == 0) {
			addEngine("Google", "https://www.google.com/search?q=", "+");
		} if(builtins.size() == 0) {
			Pythonics.print(defaultBuiltins, "file=" + directory + builtinsFile);
			builtins = readFile(builtinsFile);
		}
		
		for(String line : parse) {
			if(line.contains("home: ")) {
				home = line.replace("home: ", "");
			} else if(line.contains("SearchEngine: ")) {
				searchEngine = Pythonics.Int(line.replace("SearchEngine: ", ""));
			} else if(line.contains("Maximized: ")) {
				isMax = Pythonics.Bool(line.replace("Maximized: ", ""));
			} else if(line.contains("NonMaxWidth: ")) {
				width = Pythonics.Double(line.replace("NonMaxWidth: ", ""));
			} else if(line.contains("NonMaxHeight: ")) {
				height = Pythonics.Double(line.replace("NonMaxHeight: ", ""));
			}
		}
		
		for(String engine : engines) {
			searchEngines.add(SearchEngine.valueOf(engine));
		}
	}
	
	public static BrowserSettingsManager getController() {
		if(controller == null) {
			controller = new BrowserSettingsManager();
		}
		
		return controller;
	}
	
	/* ------------------------------------------------------------------------------------------- */
	
	public String home() {
		return home;
	}
	
	public SearchEngine search() {
		return validEngine(searchEngine) ? searchEngines.get(searchEngine) : new SearchEngine();
	}
	
	public boolean validEngine(int engine) {
		return ((engine >= 0) && (engine < searchEngines.size())) ? true : false;
	}
	
	public void updateSettings(String setting, String newValue) {
		ArrayList<String> set = getSettings();
		
		for(String line : set) {
			if(line.contains(setting)) {
				set.set(set.indexOf(line), setting + newValue);
				break;
			}
		}
		
		setSettings(set);
	}
	
	public void setHome(String home) {
		updateSettings("home: ", home);
		this.home = home;
	}
	
	public void setEngine(int engine) {
		if(!validEngine(engine)) {
			return;
		}
		
		updateSettings("SearchEngine: ", Pythonics.Str(engine));
		this.searchEngine = engine;
	}
	
	public ArrayList<SearchEngine> engines() {
		return searchEngines;
	}
	
	public ArrayList<String> builtIns() {
		return builtins;
	}
	
	protected boolean builtin(String url) {
		for(String line : builtIns()) {
			if(url.toLowerCase().contains(line)) {
				return true;
			}
		}
		return false;
	}
	
	protected int selectedEngine() {
		return searchEngine;
	}
	
	protected boolean isMaximized() {
		return isMax;
	}
	
	protected void setMaximized(boolean isMax) {
		this.isMax = isMax;
		updateSettings("Maximized: ", Pythonics.Str(isMax));
	}
	
	protected double getWidth() {
		return width;
	}
	
	protected double getHeight() {
		return height;
	}
	
	protected void setSize(double width, double height) {
		this.width = width;
		this.height = height;
		
		updateSettings("NonMaxWidth: ", Pythonics.Str(width));
		updateSettings("NonMaxHeight: ", Pythonics.Str(height));
	}
	
	protected String getAbout() {
		StringBuilder about = new StringBuilder();
		for(String line : readFile(aboutFile)) {
			about.append(line).append("\n");
		}
		
		return about.toString();
	}
}

























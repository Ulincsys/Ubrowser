package application;

public class SearchEngine {
	private String name;
	private String url;
	private String catChar;
	
	public SearchEngine() {
		this("Google", "https://www.google.com/search?q=");
	}
	
	public SearchEngine(String name, String url) {
		this(name, url, "+");
	}
	
	public SearchEngine(String name, String url, String catChar) {
		this.name = name;
		this.url = url;
		this.catChar = catChar;
	}
	
	public static SearchEngine valueOf(String parse) {
		if(parse == null || parse.length() < 7) {
			return null;
		}
		String[] parsed = parse.split("[<>]+");
		
		return new SearchEngine(parsed[2], parsed[3], parsed[1]);
	}
	
	public String getName() {
		return name;
	}
	
	public String getURL() {
		return url;
	}
	
	public String getJoin() {
		return catChar;
	}
	
	public String toString() {
		return getName();
	}
}

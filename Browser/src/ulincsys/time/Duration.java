package ulincsys.time;

public class Duration {
	private long length;
	private boolean isAbsolute;
	private boolean isLocal;
	private int tickTime = 1000;
	
	public Duration() {
		this(0, false);
	}
	
	public Duration(long length) {
		this(length, false);
	}
	
	public Duration(boolean isAbsolute) {
		this(0, isAbsolute);
	}
	
	public Duration(long length, boolean isAbsolute) {
		this.length = length;
		this.isAbsolute = isAbsolute;
	}
	
	public java.time.Duration asJavaTime() {
		return java.time.Duration.ofMillis(length);
	}
	
	public void addMillis(long milliseconds) {
		length += milliseconds;
	}
	
	public void addSecs(long seconds) {
		addMillis(seconds * 1000);
	}
	
	public void addMins(long minutes) {
		addSecs(minutes * 60);
	}
	
	public void addHours(long hours) {
		addMins(hours * 60);
	}
	
	public void addDays(int days) {
		addHours(days * 24);
	}
	
	public void addCalendarMonths(int months) {
		addDays(months * 30);
	}
	
	public void subMillis(long milliseconds) {
		addMillis(-1 * milliseconds);
	}
	
	public void subSecs(long seconds) {
		addSecs(-1 * seconds);
	}
	
	public void subMins(long minutes) {
		addMins(-1 * minutes);
	}
	
	public void subHours(long hours) {
		addHours(-1 * hours);
	}
	
	public void subDays(int days) {
		addDays(-1 * days);
	}
	
	public void subCalendarMonths(int months) {
		addCalendarMonths(-1 * months);
	}
	
	public void tick() {
		length += tickTime;
	}
	
	public long get() {
		if(isAbsolute && length < 0) {
			return -1 * length;
		} else {
			return length;
		}
	}
}















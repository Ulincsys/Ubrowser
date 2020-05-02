package ulincsys.math.vector;
import java.math.*;

public class Component {
	private double part;
	
	
	public Component() {
		part = 0;
	}
	
	public Component(int part) {
		this.part = part;
	}
	
	public Component(String part) {
		if(part.contains("pi")) 
			this.part = Math.PI;
		else if(part.contains("e"))
			this.part = Math.E;
	}
	
	public Component(double part) {
		this.part = part;
	}
	
	public double add(double part2) {
		return part + part2;
	}
	
	public double add(Component part2) {
		return part + part2.get();
	}
	
	public double get() {
		return part;
	}
	
	public void set(double part) {
		this.part = part;
	}
	
	public double square() {
		return Math.pow(part, 2);
	}
}

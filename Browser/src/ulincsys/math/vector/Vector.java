package ulincsys.math.vector;

public class Vector {
	Component[] directions;
	
	public Vector() {
		directions = new Component[3];
	}
	
	public Vector(double... directions) {
		this.directions = new Component[directions.length];
		
		for(int i = 0; i < directions.length; ++i) {
			this.directions[i] = new Component(directions[i]);
		}
	}
	
	public Vector(Component... directions) {
		this.directions = directions;
	}
}
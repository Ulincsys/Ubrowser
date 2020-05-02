package ulincsys.pythonics.lists;

import ulincsys.pythonics.Types;

public class BiNode extends Node {
	public BiNode up;
	public BiNode down;
	
	public BiNode(BiNode up, BiNode down, Node prev, Node next, Object dat) {
		super(prev, next, dat);
		
		this.up = up;
		this.down = down;
	}
	
	public BiNode(BiNode up, BiNode down, Node prev, Node next) {
		this(up, down, prev, next, Types.EMPTY);
	}

	public BiNode() {
		data = Types.EMPTY;
		prev = next = up = down = null;
	}
}

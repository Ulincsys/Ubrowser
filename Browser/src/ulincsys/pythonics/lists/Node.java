package ulincsys.pythonics.lists;

import java.util.HashMap;

import ulincsys.pythonics.Pythonics;
import ulincsys.pythonics.Types;

/**
	* Plist Node. Bi-directional data container.
	* @author Ulincsys
	*/
public class Node implements Pnode {
	public Node next;
	public Node prev;
	public Object data;

	/**
	* Initializes empty Node. O(1)
	*/
	public Node() {
		next = null;
		prev = null;
		data = Types.EMPTY;
	}
	
	/**
	* Initializes Node with given (Object)data. O(1)
	*/
	public Node(Object dat) {
		next = null;
		prev = null;
		data = dat;
	}

	/**
	* Initializes Node with given (Object)data, previous and next Nodes. O(1)
	*/
	public Node(Node prev, Node next, Object dat) {
		this.next = next;
		this.prev = prev;
		data = dat;
	}
	
	public Node(Node prev, Node next) {
		this(prev, next, Types.EMPTY);
	}
	
	/**
	* Returns string representation of (Object)data. O(1)
	*/
	public String toString() {
		if(data == null) {
			return "null";
		} else if(data == Types.EMPTY) {
			return "";
		}
		return data.toString();
	}
	
	public long lengthFromNext(HashMap<Node, Object> nodes) {
		Node cursor = this;
		long count = 1;
		
		nodes = hashCheck(nodes);
		cursor = cursor.next;
		while(!nodes.containsKey(cursor)) {
			++count;
			cursor = cursor.next;
		}
		
		return count;
	}
	
	public long lengthFromPrev(HashMap<Node, Object> nodes) {
		Node cursor = this;
		long count = 1;
		
		nodes = hashCheck(nodes);
		cursor = cursor.prev;
		while(!nodes.containsKey(cursor)) {
			++count;
			cursor = cursor.prev;
		}
		
		return count;
	}
	
	public StringBuilder toStringFromNext(HashMap<Node, Object> nodes) {
		return toStringFromNext(nodes, Long.MAX_VALUE);
	}
	
	public StringBuilder toStringFromNext(HashMap<Node, Object> nodes, Long breakLn) {
		if(breakLn <= 0) {
			return null;
		}
		long count = 0;
		Node cursor = this;
		StringBuilder build = new StringBuilder("[ " + toString() + " ] ");
		
		nodes = hashCheck(nodes);
		
		cursor = cursor.next;
		while(!nodes.containsKey(cursor)) {
			if(++count % breakLn == 0) {
				count = 0; build.append("\n");
			}
			build.append("[ " + cursor.toString() + " ] ");
			cursor = cursor.next;
		}
		
		return build;
	}
	
	public StringBuilder toStringFromPrev(HashMap<Node, Object> nodes) {
		return toStringFromPrev(nodes, Long.MAX_VALUE);
	}
	
	public StringBuilder toStringFromPrev(HashMap<Node, Object> nodes, Long breakLn) {
		if(breakLn <= 0) {
			return null;
		}
		long count = 0;
		Node cursor = this;
		StringBuilder build = new StringBuilder("[ " + toString() + " ] ");
		
		nodes = hashCheck(nodes);

		cursor = cursor.prev;
		while(!nodes.containsKey(cursor)) {
			if(++count % breakLn == 0) {
				count = 0; build.append("\n");
			}
			build.append("[ " + cursor.toString() + " ] ");
			cursor = cursor.prev;
		}
		
		return build;
	}
	
	private HashMap<Node, Object> hashCheck(HashMap<Node, Object> nodes) {
		if(nodes == null || nodes.size() == 0) {
			nodes = new HashMap<Node, Object>();
			nodes.put(null, null);
			nodes.put(this, null);
		} if(!nodes.containsKey(null)) {
			nodes.put(null, null);
		} if(!nodes.containsKey(this)) {
			nodes.put(this, null);
		}
		
		return nodes;
	}
	
	/**
	* Returns true if Node data is instance of (int). O(1)
	*/
	public boolean isInt() {
		return (data instanceof Integer);
	}

	/**
	* Returns true if Node data is instance of (String). O(1)
	*/
	public boolean isStr() {
		return (data instanceof String);
	}
	
	public boolean isType(Class<? extends Object> test) {
		return (test == getType());
	}

	/**
	* Returns the internal reflective Class of (Object)data. O(n)
	*/
	public Class<? extends Object> getType() {
		try {
			return data.getClass();
		} catch(Exception e) {
			return null;
		}
	}
	
	public String identityString() {
		return (data == null) ? "null" :
				new StringBuilder().append(data.getClass()).append("::").append(data.toString()).toString();
	}
	
	public boolean equals(Object e) {
		return ((System.identityHashCode(data) == System.identityHashCode(e)) || 
				((data == null) ? e == null : data.equals(e)) || 
				(Pythonics.cast(e) == Pythonics.cast(data))); 
	}
	
	@SuppressWarnings("unchecked")
	public <Any> Any cast() {
		return (data != null) ? (Any)data.getClass().cast(data) : null;
	}
}
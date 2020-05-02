package ulincsys.pythonics.lists;

import static ulincsys.pythonics.Pythonics.print;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import ulincsys.pythonics.Pythonics;
import ulincsys.pythonics.Types;

@SuppressWarnings("rawtypes")
public class TrianglePlist implements PythonicList {
	private int rows;
	private BiNode head;
	private BiNode tail;
	private BiNode rowCursor;
	private Node cursor;
	private BiNode prevRow;
	private Node prevCursor;
	
	public TrianglePlist() {
		init();
	}
	
	public void init() {
		head = new BiNode();
		tail = new BiNode(head, null, null, null, null);
		cursor = prevCursor = rowCursor = prevRow = head;
		
		head.next = tail;
		rows = 0;
	}
	
	public int rows() {
		return rows;
	}
	
	public BigInteger len() {
		long total = 0;
		
		for(int rows = this.rows; rows > 0; total += rows--);
		
		return BigInteger.valueOf(total);
	}
	
	public void addRow(Object... objects) {
		BiNode temp = new BiNode(tail.up, tail, null, null);
		tail.up.down = tail.up = temp;
		
		Node cursor = temp;
		cursor.data = (objects.length != 0) ? objects[0] : Types.EMPTY;
		
		for(int i = 1; i <= rows; ++i) {
			cursor.next = new Node(cursor, null, (i < objects.length) ? objects[i] : Types.EMPTY);
			cursor = cursor.next;
		}
		
		++rows;
	}
	
	public Plist removeRow() {
		if(isEmpty()) {
			return null;
		}
		
		Plist temp = new Plist();
		
		unwind();
		
		tail.up = rowCursor.up;
		rowCursor.up.down = tail;
		
		rowCursor.up = rowCursor.down = null;
		
		temp.addNodes(rowCursor);
		
		cursor = rowCursor;
		
		--rows;
		
		return temp;
	}
	
	public void set(int row, int column, Object data) {
		Node temp = nodeAt(row, column);
		if(temp != null) {
			temp.data = data;
		}
	}
	
	public Object set(int index, Object data) {
		Node temp = nodeAt(index, index);
		if(temp != null) {
			
			temp.data = data;
			return true;
		}
		
		return null;
	}
	
	public <Any> Any at(int row, int column) {
		Node temp = nodeAt(row, column);
		if(temp != null) {
			return temp.cast();
		}
		
		return null;
	}
	
	public <Any> Any at(int index) {
		return at(index, index);
	}
	
	public Node nodeAt(int row, int column) {
		if(isEmpty()) {
			return null;
		} if(row < 0 && rows + row >= 0) {
			row += rows;
		} else if(row < 0) {
			return null;
		} if(column < 0 && column + row >= 0) {
			column += row;
		} else if(column < 0) {
			return null;
		} else {
			--column;
		} if(row >= rows || column > row) {
			return null;
		}
		
		rewind();
		
		while(--row >= 0) {
			rowCursor = rowCursor.down;
		}
		
		Node temp = rowCursor;
		
		while(column-- >= 0) {
			temp = temp.next;
		}
		
		restore();
		
		return temp;
	}
	
	public boolean isEmpty() {
		return (rows == 0) ? true : false;
	}
	
	public String toString() {
		rewind();
		StringBuilder build = new StringBuilder("Triangle Plist with " + rows + " rows:\n");
		if(rows == 0) {
			return build.append("[ ]\n").toString();
		}
		
		HashMap<Node, Object> nodes = new HashMap<>();
		
		nodes.put(null, null);
		
		while(rowCursor != tail) {
			build.append(rowCursor.toStringFromNext(nodes)).append("\n");
			rowCursor = rowCursor.down;
		}
		restore();
		return build.toString();
	}
	
	public void read() {
		print(toString());
	}

	@Override
	public Iterator iterator() {
		rewind();
		return new TrianglePliterator(this);
	}

	@Override
	public <Any> Any getFirstInstance(Class<? extends Object> typ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <Any> Any getLastInstance(Class<? extends Object> typ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <Any> Any rem(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sortType(Class<? extends Object> typ, Comparator<? super Object> c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> ArrayList<T> removeType(Class<? extends Object> typ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node[] drop() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasNext() {
		return (cursor != tail) && !isEmpty();
	}

	@Override
	public boolean hasPrev() {
		return cursor != head && !isEmpty();
	}
	
	protected void rewind() {
		prevCursor = cursor;
		prevRow = rowCursor;
		cursor = rowCursor = head.down;
	}
	
	protected void unwind() {
		prevCursor = cursor;
		prevRow = rowCursor;
		cursor = rowCursor = tail.up;
	}
	
	protected void restore() {
		cursor = prevCursor;
		rowCursor = prevRow;
	}

	@Override
	public <Any> Any getNext() {
		if(hasNext()) {
			if(cursor.next != null) {
				cursor = cursor.next;
				return cursor.prev.cast();
			} else {
				Node temp = cursor;
				rowCursor = rowCursor.down;
				cursor = rowCursor;
				return temp.cast();
			}
		}
		return null;
	}

	@Override
	public <Any> Any getPrev() {
		if(hasPrev()) {
			if(cursor.prev != null) {
				cursor = cursor.prev;
				return cursor.next.cast();
			} else {
				Node temp = cursor;
				rowCursor = rowCursor.up;
				cursor = rowCursor;
				return temp.cast();
			}
		}
		return null;
	}

	@Override
	public boolean contains(Object o) {
		for(Object item : this) {
			if(Pythonics.isEqual(item, o)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean containsAll(Collection c) {
		for(Object item : c) {
			if(!contains(item)) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public boolean add(Object data) {
		// TODO Auto-generated method stub
		return false;
	}
}


@SuppressWarnings("rawtypes")
class TrianglePliterator implements Iterator {
	TrianglePlist data;
	
	TrianglePliterator(TrianglePlist data) {
		this.data = data;
	}
	
	@Override
	public boolean hasNext() {
		return data.hasNext();
	}

	@Override
	public Object next() {
		return data.getNext();
	}
	
}





























package ulincsys.pythonics.lists;

import static ulincsys.extras.Randoms.randInt;
import static ulincsys.pythonics.Pythonics.Int;
import static ulincsys.pythonics.Pythonics.Str;
import static ulincsys.pythonics.Pythonics.print;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import ulincsys.pythonics.Pythonics;

/**
	* A typeless, doubly-traversable linked list with extended features.
	* Supports random fill, sorting and printing.
	* Returns data as typecast (Any)Object;
	* @author Ulincsys
	*/
@SuppressWarnings("rawtypes")
public class Plist implements List, PythonicList {
	private HashMap<String, Integer> database;
	private Node head;
	private Node tail;
	private Node cursor;
	private Node store;
	private int len;
	protected int index;
	private int preIndex;
	
	/**
	* Initializes empty plist. O(1)
	*/
	public Plist() {
		head = new Node(null, null, null);
		tail = new Node(head, null, null);
		head.next = tail;
		store = cursor = head;
		len = index = preIndex = 0;
		database = new HashMap<String, Integer>();
	}
	
	/**
	* Initializes plist with the given number of (k) objects, or with an Object[] array of (k) objects. O(k)
	*/
	public Plist(Object... objects) {
		this();
		
		for(Object item : objects) {
			add(item);
		}
	}
	
	public void updateAdd(Object data) {
		if(!database.containsKey(Pythonics.identityString(data))) {
			database.put(Pythonics.identityString(data), Integer.valueOf(1));
		} else {
			database.put(Pythonics.identityString(data), database.get(Pythonics.identityString(data)) + 1);
		}
		++len;
	}
	
	public void updateRem(Object data) {
		if(database.containsKey(Pythonics.identityString(data))) {
			if(!database.remove(Pythonics.identityString(data), 1)) {
				database.put(Pythonics.identityString(data), database.get(Pythonics.identityString(data)) - 1);
			}
			--len;
		}
	}
	
	/**
	* Returns the number of items contained within parent list. O(1)
	*/
	@Override
	public int size() {
		return len;
	}
	
	public BigInteger len() {
		return BigInteger.valueOf(size());
	}

	/**
	* Returns true if the head of the list points to the tail, else false. O(1)
	*/
	public boolean isEmpty() {
		return (head.next == tail) ? true : false;
	}

	/**
	* Adds all items in given Collection of (k) length to list.
	* Takes any Collection. O(K)
	*/
	
	@Override
	public boolean addAll(Collection items) {
		try {
			for(Object item : items) {
				add(item);
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	protected void addNode(Node item) {
		if(item == null) {
			return;
		}
		
		item.next = tail;
		item.prev = tail.prev;
		tail.prev.next = tail.prev = item;
		
		updateAdd(item.data);
	}
	
	protected void addNodes(Node item) {
		if(item == null) {
			return;
		}
		
		item.prev = tail.prev;
		tail.prev.next = item;
		
		updateAdd(item.data);
		
		while(item.next != null) {
			cursor = item;
			item = item.next;
			if(item.prev != cursor) {
				item.prev = cursor;
			}
			updateAdd(item.data);
		}
		
		tail.prev = item;
		item.next = tail;
	}
	
	/**
	* Adds item to end of list. Takes any (Object)data with no restrictions. O(1)
	*/
	@Override
	public boolean add(Object data) {
		if(isFull()) {
			return false;
		}
		
		Node temp = new Node(tail.prev, tail, data);
		temp.prev.next = tail.prev = temp;
		updateAdd(data);
		return true;
	}
	
	protected void addAtCursor(Object e) {
		if(isFull() || e == this) {
			return;
		}
		
		Node temp = new Node(cursor, cursor.next, e);
		
		cursor.next = cursor.next.prev = temp;
		
		updateAdd(e); ++index;
	}

	@Override
	public boolean addAll(int index, Collection c) {
		if(!checkIndex(index) || c.contains(this) || willOverfill(c.size())) {
			return false;
		}
		
		Node temp = nodeAt(index);
		
		for(Object item : c) {
			temp.prev.next = temp.prev = new Node(temp.prev, temp, item);
			if(currentIndex() >= index) {
				this.index++;
			}
			updateAdd(item);
		}
		return false;
	}
	
	/**
	* Adds all given (k) items to list. Takes any (Object)data. O(k)
	*/
	public void add(Object... items) {
		for(Object item : items) {
			add(item);
		}
	}

	@Override
	public boolean contains(Object o) {
		return database.containsKey(Pythonics.identityString(o));
	}
	
	public boolean containsExactly(Object o) {
		for(Object item : this) {
			if(Pythonics.isEqual(item, o)) {
				restore();
				return true;
			}
		}
		restore();
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
	
	public boolean containsAllExactly(Collection c) {
		for(Object item : c) {
			if(!containsExactly(item)) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public Object get(int index) {
		Node temp = nodeAt(index);
		return (temp == null) ? temp : temp.data;
	}
	
	@Override
	public Object set(int index, Object element) {
		if(index >= size()) {
			return null;
		}
		
		Node temp = nodeAt(index);
		
		updateRem(temp.data);
		updateAdd(element);
		
		Object result = temp.data;
		temp.data = element;
		
		return result;
	}
	
	/**
	* Returns the item at given index as typecast (Any)Object. O(n)
	*/
	public <Any> Any at(int index) {
		if(!checkIndex(index)) {
			return null;
		}
		
		Node temp = nodeAt(index);
		
		return temp.cast();
	}
	
	public <Any> Any getFirstInstance(Class<? extends Object> typ) {
		rewind();
		
		while(!cursor.isType(typ) && hasNext()) {
			next();
		} if(!hasNext()) {
			return null;
		}
		
		Node temp = cursor;
		restore();
		
		return temp.cast();
	}
	
	public <Any> Any getLastInstance(Class<? extends Object> typ) {
		unwind();
		
		while(!cursor.isType(typ) && hasPrev()) {
			prev();
		} if(!hasPrev()) {
			return null;
		}
		
		Node temp = cursor;
		restore();
		
		return temp.cast();
	}
	
	public boolean isFull() {
		return len == Integer.MAX_VALUE;
	}
	
	public boolean willOverfill(int length) {
		return ((Integer.MAX_VALUE - length) < len);
	}
	
	/**
	* Resets list cursor to list head, stores previous location for restore(). O(1)
	*/
	protected void rewind() {
		store = cursor;
		cursor = head.next;
		preIndex = index;
		index = 0;
	}
	
	/**
	* Resets list cursor to list tail, stores previous location for restore(). O(1)
	*/
	protected void unwind() {
		store = cursor;
		cursor = tail.prev;
		preIndex = index;
		index = size() - 1;
	}
	
	/**
	* Restores list cursor to previous location given by rewind(). O(1)
	*/
	protected void restore() {
		cursor = store;
		index = preIndex;
	}
	
	/**
	* Returns false if cursor next Node is tail of list or null, else true. O(1)
	*/
	public boolean hasNext() {
		return (cursor != tail || ((cursor == head) && (cursor.next != tail))) ? true : false;
	}
	
	/**
	* Returns false if cursor previous Node is head of list or null, else true. O(1)
	*/
	public boolean hasPrev() {
		return (cursor != head || ((cursor == tail) && (cursor.next != head))) ? true : false;
	}
	
	/**
	* Returns cursor next Node if hasNext(), else null. O(1)
	*/
	protected Node next() {
		if(hasNext()) {
			cursor = cursor.next;
			++index;
			return cursor.prev;
		}
		return null;
	}
	
	public <Any> Any getNext() {
		if(hasNext()) {
			return next().cast();
		}
		
		return null;
	}
	
	public <Any> Any getPrev() {
		if(hasPrev()) {
			return prev().cast();
		}
		
		return null;
	}
	
	/**
	* Returns cursor previous Node if hasPrev(), else null. O(1)
	*/
	protected Node prev() {
		if(hasPrev()) {
			cursor = cursor.prev;
			--index;
			return cursor.next;
		}
		return null;
	}
	
	public Object current() {
		return cursor.data;
	}
	
	protected int currentIndex() {
		return index - 1;
	}
	
	/**
	* Removes Node at index and returns data as (Any)Object, else null. Invokes nodeAt(index). O(n)
	*/
	public <Any> Any rem(int index) {
		if(!checkIndex(index)) {
			return null;
		}
		Node temp = nodeAt(index);
		updateRem(temp.data);
		stripNode(temp);
		
		return temp.cast();
	}

	/**
	* Removes given node from its list unless it is immovable.
	* Returns true if removal is successful, else false. O(1)
	*/
	protected boolean stripNode(Node item) {
		if(immovable(item)) {
			return false;
		}
		
		if(cursor == item) {
			if(store == cursor) {
				store = head;
			}
			if(next() == null) {
				if(prev() == null) {
					rewind();
				}
			}
		}

		item.prev.next = item.next;
		item.next.prev = item.prev;
		item.next = item.prev = null;
		return true;
	}
	
	/**
	* Returns Node at index, else null.
	* Can take negative index to reference list in reverse, as long as len() + index is >= 0. O(n)
	*/
	public Node nodeAt(int index) {
		if(!checkIndex(index)) {
			return null;
		} else if(index < 0) {
			index += size();
		}
		int i;
		
		if(index < size()/2) {
			rewind();
			i = 0;
			while(++i <= index) {
				next();
			}
		} else {
			unwind();
			i = size() - 1;
			while(--i >= index) {
				prev();
			}
		}
		
		Node temp = cursor;
		restore();
		return temp;
	}
	
	/**
	* Returns Object array of data items, else null if list is empty. O(n)
	*/
	public Object[] toArray() {
		if(isEmpty()) {
			return null;
		}
		
		rewind();
		
		Object[] temp = new Object[size()];
		for(int i = 0; i < size(); ++i) {
			temp[i] = current();
			next();
		}
		
		restore();
		return temp;
	}

	@Override
	public Object[] toArray(Object[] a) {
		if(a.length >= size()) {
			int i = 0;
			rewind();
			while(i < a.length) {
				a[i] = current();
				next();	++i;
			}
			restore();
		} else {
			return toArray();
		}
		return a;
	}
	
	/**
	* Swaps data items between two Nodes, given that neither of them are immovable() and that they are not the same Node.
	* Returns false if the swap fails, or true if the swap succeeds. O(1)
	*/
	protected boolean swap(Node one, Node two) {
		if(immovable(one) || immovable(two) || one == two) {
			return false;
		}
		Object temp = one.data;
		one.data = two.data;
		two.data = temp;
		return true;
	}

	/**
	* Returns true if the given Node is equal to this.head or this.tail.
	* Returns true if the given Node has null next or previous Nodes, else false. O(1)
	*/
	protected boolean immovable(Node test) {
		return (test == head || test == tail || test == null) ? true : false;
	}
	
	/**
	* Swaps data items between two nodes at the given indices, given that neither of them are immovable() and that they are not the same node.
	* Returns false if the swap fails, or true if the swap succeeds.
	* Invokes nodeAt(index). O(n)
	*/
	public boolean swap(int one, int two) {
		if((checkIndex(one) || checkIndex(two)) || one == two) {
			return false;
		}
		
		Node n1 = nodeAt(one);
		Node n2 = nodeAt(two);
		return swap(n1, n2);
	}
	
	/**
	* Converts list to Object array and returns it.
	* Clears list of all items.
	* Invokes toArray() and drop(). O(n)
	*/
	public Object[] reset() {
		Object[] temp = toArray();
		clear();
		return temp;
	}

	/**
	* Removes all references to data items on list, and resets len(). O(1)
	*/
	
	@Override
	public void clear() {
		head.next = tail;
		store = cursor = tail.prev = head;
		len = index = preIndex = 0;
		database.clear();
	}
	
	/**
	* Returns false if the given index is greater than or equal to len(), or if len() + index is less than 0, else true. O(1)
	*/
	protected boolean checkIndex(int index) {
		if(index >= len) {
			return false;
		} else if((len + index) < 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	* Adds given amount (k) of random numbers between 1 and 1000 to list. O(k)
	*/
	public Plist fill(int amt) {
		while(--amt >= 0) {
			add(randInt(0, 1000));
		}
		
		return this;
	}
	
	/**
	* Prints formatted string representations of each item separated by brackets. 
	* Inserts newlines at Integer.MAX_VALUE. O(n)
	*/
	public void read() {
		read(Integer.MAX_VALUE);
	}
	
	/**
	* Prints formatted string representations of each item separated by brackets.
	* Inserts newlines at the given length. O(n)
	* @param breakLn Gives the length at which to insert newlines.
	*/
	public void read(int breakLn) {
		print(toString(breakLn));
	}
	
	@Override
	public String toString() {
		return toString(-1);
	}
	
	public String toString(int breakLn) {
		if(breakLn <= 0) {
			breakLn = Integer.MAX_VALUE;
		}
		StringBuilder list = new StringBuilder("Plist with " + size() + " items:\n");
		
		if(size() == 0) {
			return list.append("[ ]").toString();
		}
		
		rewind();
		
		HashMap<Node, Object> nodes = new HashMap<>();
		
		nodes.put(null, null);
		nodes.put(tail, null);
		
		list.append(cursor.toStringFromNext(nodes, (long)breakLn)).append("\n");
		
		restore();
		
		return list.toString();
	}
	
	/** Extracts all Objects of type Integer from the list and sorts them using
	 * the built-in Arrays.sort(). Adds the sorted items to the end of the list 
	 * after all non-integer Objects. O(n<sup>2</sup>)
	 */
	public void sortInts() {
		ArrayList<Object> sorted = removeType(Integer.class);
		sorted.sort(new compareInts());
		addAll(sorted);
	}
	
	/** Extracts all Objects of type String from the list and sorts them using
	 * the built-in Arrays.sort(). Adds the sorted items to the end of the list 
	 * after all non-String Objects. O(n<sup>2</sup>)
	 * @apiNote Sorts with compareToIgnoreCase().
	 */
	public void sortAlpha() {
		ArrayList<Object> sorted = removeType(String.class);
		sorted.sort(new compareStrings());
		addAll(sorted);
	}
	
	/** Extracts all Objects of the given type from the list and sorts them using
	 * the built-in Arrays.sort(). Adds the sorted items to the end of the list 
	 * after all non-type Objects. O(n<sup>2</sup>)
	 * @apiNote Requires a Comparator class which accepts any Object superclass.
	 * To use with your own Objects, you must cast from Object to subclass.
	 * @param typ The class used to determine what Objects are sortable.
	 * @param c The Object comparator class used to sort the given type.
	 */
	public void sortType(Class<? extends Object> typ, Comparator<? super Object> c) {
		ArrayList<Object> sorted = removeType(typ);
		sorted.sort(c);
		addAll(sorted);
	}
	
	/** Removes all Objects of a given type from the list and returns an 
	 * ArrayList populated with the items. O(n)
	 * @param typ The class used to determine what Objects are removable.
	 * @apiNote Does not test superclass, must match exactly.
	 * @return ArrayList-Object
	 */
	@SuppressWarnings("unchecked")
	public <T> ArrayList<T> removeType(Class<? extends Object> typ) {
		if(size() == 0) {
			return null;
		}
		
		ArrayList<T> removed = new ArrayList<T>();
		rewind();
		while(hasNext()) {
			if(cursor.getType() == typ) {
				removed.add((T)cursor.data);
				stripNode(cursor);
				updateRem(cursor.data);
			} else {
				next();
			}
		}
		
		restore();
		return removed;
	}

	@Override
	public void add(int index, Object element) {
		if(!checkIndex(index) || element == this) {
			return;
		}
		Node temp = nodeAt(index);
		
		Node added = new Node(temp.prev, temp, element);
		
		temp.prev.next = temp.prev = added;
		
		updateAdd(element);
	}
	
	public void cursorTo(int index) {
		rewind();
		cursor = nodeAt(index);
		this.index = index;
	}
	
	@Override
	public Plist clone() {
		Plist temp = new Plist();
		
		rewind();
		
		while(hasNext()) {
			temp.add(getNext());
		}
		
		return temp;
	}
	
	public Node[] drop() {
		if(isEmpty()) {
			return null;
		}
		
		Node[] Nodes = new Node[2];
		
		Nodes[0] = head.next;
		Nodes[0].prev = null;
		Nodes[1] = tail.prev;
		Nodes[1].next = null;
		
		clear();
		
		return Nodes;
	}
	
	protected Node cursor() {
		if(size() < 1) {
			return null;
		} else if(!hasPrev()) {
			return head.next;
		} else if(!hasNext()) {
			return tail.prev;
		}
		return cursor;
	}

	@Override
	public int indexOf(Object o) {
		for(Object item : this) {
			if(Pythonics.isEqual(item, o)) {
				return currentIndex();
			}
		}
		return -1;
	}

	@Override
	public Iterator iterator() {
		rewind();
		return new Pliterator(this);
	}

	@Override
	public int lastIndexOf(Object o) {
		unwind();
		
		while(hasPrev()) {
			if(Pythonics.isEqual(getPrev(), o)) {
				restore();
				return currentIndex();
			}
		}
		restore();
		return -1;
	}

	@Override
	public ListIterator listIterator() {
		rewind();
		return new PlistIterator(this);
	}

	@Override
	public ListIterator listIterator(int index) {
		if(!checkIndex(index)) {
			return null;
		}
		cursorTo(index);
		
		return new PlistIterator(this);
	}

	@Override
	public boolean remove(Object o) {
		if(isFull()) {
			return false;
		}
		rewind();
		
		while(hasNext()) {
			if(cursor().equals(o)) {
				stripNode(cursor());
				updateRem(cursor.data);
				return true;
			}
			next();
		}
		return false;
	}

	@Override
	public Object remove(int index) {
		if(index >= size()) {
			return null;
		}
		Node temp = nodeAt(index);
		stripNode(temp);
		updateRem(temp.data);
		return temp.data;
	}

	@Override
	public boolean removeAll(Collection c) {
		boolean changed = false;
		if(size() == 0) {
			return changed;
		}
		rewind();
		
		while(hasNext()) {
			if(c.contains(cursor().data)) {
				if(stripNode(cursor())) {
					updateRem(cursor().data);
					changed = true;
				}
			} else {
				next();
			}
		}
		return changed;
	}

	@Override
	public boolean retainAll(Collection c) {
		boolean changed = false;
		if(size() == 0) {
			return changed;
		}
		rewind();
		
		while(hasNext()) {
			if(!c.contains(cursor().data)) {
				stripNode(cursor());
				updateRem(cursor().data);
				System.out.println((cursor == head) + ", " + (cursor == tail));
				changed = true;
			} else {
				next();
			}
		}
		return changed;
	}

	@Override
	public List subList(int fromIndex, int toIndex) {
		if(checkIndex(fromIndex) && checkIndex(toIndex) && (fromIndex <= toIndex)) {
			Plist sublist = new Plist();
			cursorTo(fromIndex);
			toIndex -= fromIndex;
			while(--toIndex >= 0) {
				sublist.add(next().data);
			}
			restore();
			return sublist;
		}
		return null;
	}
	
	
}




class compareInts implements Comparator<Object> {
	public int compare(Object a, Object b) {
		if(Int(a) > Int(b)) {
			return 1;
		} else if(Int(a) < Int(b)) {
			return -1;
		}
		return 0;
	}
}

class compareStrings implements Comparator<Object> {
	public int compare(Object a, Object b) {
		return Str(a).compareToIgnoreCase(Str(b));
	}
}

@SuppressWarnings("rawtypes")
class Pliterator implements Iterator {
	Plist data;
	
	Pliterator(Plist data) {
		this.data = data;
	}
	
	@Override
	public boolean hasNext() {
		return data.hasNext();
	}

	@Override
	public Object next() {
		return data.next().cast();
	}
	
}

@SuppressWarnings("rawtypes")
class PlistIterator implements ListIterator {
	Plist data;
	
	PlistIterator(Plist data) {
		this.data = data;
	}
	
	@Override
	public boolean hasNext() {
		return data.hasNext();
	}

	@Override
	public Object next() {
		return data.next().cast();
	}

	@Override
	public boolean hasPrevious() {
		return data.hasPrev();
	}

	@Override
	public Object previous() {
		return data.prev().cast();
	}

	@Override
	public int nextIndex() {
		return (hasNext()) ? data.currentIndex() + 1 : -1;
	}

	@Override
	public int previousIndex() {
		return data.currentIndex() - 1;
	}

	@Override
	public void remove() {
		data.stripNode(data.cursor());
	}

	@Override
	public void set(Object e) {
		if(data.cursor() != null) {
			data.stripNode(data.cursor());
		}
	}

	@Override
	public void add(Object e) {
		if(data.size() == 0) {
			data.add(e);
		} else {
			data.addAtCursor(e);
		}
	}
	
}










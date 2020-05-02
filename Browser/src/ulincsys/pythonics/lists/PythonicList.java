package ulincsys.pythonics.lists;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

@SuppressWarnings("rawtypes")
public interface PythonicList extends Iterable {
	<Any> Any getFirstInstance(Class<? extends Object> typ);
	<Any> Any getLastInstance(Class<? extends Object> typ);
	<Any> Any rem(int index);
	<Any> Any at(int index);
	void read();
	void sortType(Class<? extends Object> typ, Comparator<? super Object> c);
	<T> ArrayList<T> removeType(Class<? extends Object> typ);
	Node[] drop();
	boolean isEmpty();
	boolean hasNext();
	boolean hasPrev();
	<Any> Any getNext();
	<Any> Any getPrev();
	boolean contains(Object o);
	boolean containsAll(Collection c);
	BigInteger len();
	boolean add(Object data);
	public Object set(int index, Object element);
}

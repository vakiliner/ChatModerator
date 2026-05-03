package vakiliner.chatcomponentapi.util;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

public class ParseCollection<Output, Input> extends AbstractCollection<Output> {
	protected final Collection<? extends Input> impl;
	protected final Function<Input, Output> i2o;

	public ParseCollection(Collection<? extends Input> impl, Function<Input, Output> i2o) {
		this.impl = impl;
		this.i2o = i2o;
	}

	public Collection<? extends Input> getImpl() {
		return impl;
	}

	public Iterator<Output> iterator() {
		return new ParseIterator<>(this.impl.iterator(), this.i2o);
	}

	public int size() {
		return this.impl.size();
	}

	public boolean isEmpty() {
		return this.impl.isEmpty();
	}

	public boolean add(Output e) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		this.impl.clear();
	}
}
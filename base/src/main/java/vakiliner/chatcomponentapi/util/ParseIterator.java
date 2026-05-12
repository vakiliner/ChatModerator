package vakiliner.chatcomponentapi.util;

import java.util.Iterator;
import java.util.function.Function;

public class ParseIterator<Output, Input> implements Iterator<Output> {
	protected final Iterator<? extends Input> impl;
	protected final Function<Input, Output> i2o;

	public ParseIterator(Iterator<? extends Input> impl, Function<Input, Output> i2o) {
		this.impl = impl;
		this.i2o = i2o;
	}

	public Iterator<? extends Input> getImpl() {
		return this.impl;
	}

	public boolean hasNext() {
		return this.impl.hasNext();
	}

	public Output next() {
		return this.i2o.apply(this.impl.next());
	}

	public void remove() {
		this.impl.remove();
	}
}
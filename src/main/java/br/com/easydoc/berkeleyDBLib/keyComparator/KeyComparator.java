package br.com.easydoc.berkeleyDBLib.keyComparator;

import java.io.Serializable;
import java.util.Comparator;

public abstract class KeyComparator<K> implements Comparator<byte[]>, Serializable {

	private static final long serialVersionUID = -7945729462329246263L;

}

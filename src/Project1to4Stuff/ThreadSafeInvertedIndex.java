package Project1to4Stuff;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Thread-safe subclass of InvertedIndex. Allows concurrent modifications and access of index using
 * a custom lock-object.
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {

	/** Custom lock object for safe InvertedIndex thread interaction (reading/writing operations)*/
	private final ReadWriteLock lock;
	
	/**
	 * Calls the parent constructor and initializes 
	 * a new ReadWriteLock.
	 */
	public ThreadSafeInvertedIndex() {
		super();
		lock = new ReadWriteLock();
	}
	
	/**  {@inheritDoc} */
	@Override
	public boolean add(String word, String pathname, Integer position) {
		lock.lockReadWrite();
		try {
			return super.add(word, pathname, position);
		}
		finally {
			lock.unlockReadWrite();
		}
	}
	
	/**  {@inheritDoc} */
	@Override
	public void outputToJSONFile(Path output) {
		lock.lockReadOnly();
		try {
			super.outputToJSONFile(output);
		}
		finally {
			lock.unlockReadOnly();
		}
	}
	
	/**  {@inheritDoc} 
	 * @return */
	@Override
	public ArrayList<SearchResult> partialSearch(String[] line) {
		lock.lockReadOnly();
		try {
			return super.partialSearch(line);
		}
		finally {
			lock.unlockReadOnly();
		}
	}

	/**  {@inheritDoc} 
	 * @return */
	@Override
	public void addAll(InvertedIndex miniMap) {
		lock.lockReadWrite();
		try {
			super.addAll(miniMap);
		}
		finally {
			lock.unlockReadWrite();
		}
	}
	
	/**  {@inheritDoc} */
	@Override
	public String toString() {
		lock.lockReadOnly();
		try {
			return super.toString();
		}
		finally {
			lock.unlockReadOnly();
		}
	}
}

package Project1to4Stuff;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * This class is useful for reading a file containing query words for use in a 
 * search program using multithreading.
 */
public class ConcurrentQueryFileParser extends AbstractQueryFileParser {

	/** A mapping of SearchResults paired to query words*/
	private final LinkedHashMap<String, ArrayList<SearchResult>> results;
	
	private final ThreadSafeInvertedIndex invertedIndex;
	
	/** Custom lock object for safe InvertedIndex thread interaction (reading/writing operations)*/
	private final ReadWriteLock lock;
	
    /** Work queue used to handle multithreading for this class. */
    private final WorkQueue workers;
    
    /**
     * Main constructor that initializes a new LinkedHashMap and new Workqueue
     */
	public ConcurrentQueryFileParser(int numThreads, ThreadSafeInvertedIndex invertedIndex) {
		super();
		results = new LinkedHashMap<String, ArrayList<SearchResult>>();
		this.invertedIndex = invertedIndex;
		lock = new ReadWriteLock();
		workers = new WorkQueue(numThreads);
	}
	
	/**
	 * Overloaded constructor that takes in a WorkQueue object
	 */
	public ConcurrentQueryFileParser(WorkQueue reuseablequeue, ThreadSafeInvertedIndex invertedIndex) {
		super();
		results = new LinkedHashMap<String, ArrayList<SearchResult>>();
		this.invertedIndex = invertedIndex;
		lock = new ReadWriteLock();
		this.workers = reuseablequeue;
	}
	
	/**
	 * This method takes in an uncleaned, non-empty line of a query file
	 * and creates work to find and store all partial matches in the passed 
	 * in InvertedIndex object.
	 * 
	 * @param line
	 * 			a query line
	 * @param index
	 * 			an instance of ThreadSafeInvertedIndex
	 */
	public void parseLine(String line) {
		lock.lockReadWrite();
		try{
			results.put(line, null);
		}
		finally{
			lock.unlockReadWrite();
		}
		workers.execute(new QueryMinion(line));
	}
	
    /** Shuts down the work queue after all pending work is finished. 
     *  WorkQueue handles pending work. */
    public void shutdown() {
    	workers.shutdown();
	}
	
    /**
     * The QueryMinion takes in a single line of a query file
     * and calls the appropriate methods for filling the map of the
     * parent class @see {@link QueryFileParser}
     */
	private class QueryMinion implements Runnable {

		/** Query line for use in partial search */
		private final String line;
		/** Stores a reference to Driver's index*/
		private final ThreadSafeInvertedIndex indexActual;
		
		/** Query Minion Constructor */
		public QueryMinion(String line) {
			this.line = line;
			this.indexActual = invertedIndex;
		}
		@Override
		public void run() {
			//the partialSearch() is what makes multithreading this worth it
			//keep the synchronization of partialSearch() outside the synchronization of our hashmap
			ArrayList<SearchResult> tempList = indexActual.partialSearch(
					FiletoIndexBuilder.clean(line).split(" "));
					
			lock.lockReadWrite();
			try{
				results.put(line, tempList);
			}
			finally{
				lock.unlockReadWrite();
			}
		}
	}
	
	/**
	 * Calls to JSON.toJSON() for partial search
	 * 
	 * @param output
	 * 			file name for JSON building
	 */
	public void toJSON(String output){
		JSONWriter.toJSON(output, results);
	}
}

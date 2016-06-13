package Project1to4Stuff;
import java.nio.file.Path;

/**
 * This class is useful for taking in a directory and recursively finding all
 * text files within that directory and all nested sub-directories and adding all found
 * words to an index.
 */
public class ConcurrentFiletoIndexBuilder {

    /** Work queue used to handle multithreading for this class. */
    private final WorkQueue workers;

	/** Main constructor that takes in a number of threads and initializes a new WorkQueue */
    public ConcurrentFiletoIndexBuilder(int numThreads) {
        this.workers = new WorkQueue(numThreads);
    }
    
    /** Constructor that takes in an already instantiated WorkQueue object reference */
    public ConcurrentFiletoIndexBuilder(WorkQueue reuseablequeue) {
        workers = reuseablequeue;
    }

    /**
     * Takes in a path and creates a new @FileMinion for every file within
     * 
     * @param directory
     * 				a Path; can be either a file or directory
     * @param index
     * 				an instance of InvertedIndex
     */
    public void readDirectory(Path directory, ThreadSafeInvertedIndex index) {
        for (Path path : DirectoryTraverser.traverse(directory)) {
        	workers.execute(new FileMinion(path, index));
        }
    }

    /**
     * Shutsdown the work queue after all pending work is finished. After this
     * point, all additional calls to {@link #parseTextFiles(Path, String)} will
     * no longer work.
     */
    public void shutdown() {
    	workers.shutdown();
	} 
    
    /**
     * This runnable object handles a single valid path and creates an inverted index
     * instance out of all found words with their position and the current
     */
	private class FileMinion implements Runnable {

		/** Stores a pathname for this minion */
		private final Path pathname;
		/** Stores a reference to Driver's InvertedIndex */
		private final ThreadSafeInvertedIndex indexActual;
		/** A Local instance of InvertedIndex */
		private final InvertedIndex localIndex;

		/** FileMinion Constructor */
		public FileMinion(Path pathname, ThreadSafeInvertedIndex index) {
			this.pathname = pathname;
			localIndex = new InvertedIndex();
			indexActual = index;
		}
		
		@Override
		public void run() {
			FiletoIndexBuilder.readFile(pathname.toString(), localIndex);
			indexActual.addAll(localIndex);
		}
	}
}

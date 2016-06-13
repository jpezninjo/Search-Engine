package Project1to4Stuff;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * This class is used for recursively crawling a single passed in link and all nested links
 * found within that link and following links. Each link is added to an instance of InvertedIndex,
 * and the total number of links to be crawled by a single instance of WebCrawler is determined by
 * the variable maxNumLinks;
 *
 */
public class WebCrawler {

    /** Work queue used to handle multithreading for this class. */
    private final WorkQueue workers;
	
	/** Custom lock object for safe InvertedIndex thread interaction (reading/writing operations)*/
	private final ReadWriteLock lock;
    
	/** A list of links that have already been processed*/
	private ArrayList<String> seenlinksList;
	
    /** Stores number of current workers. */
    private int pending;
    
    /** This variable stores the maximum amount of links that a single WebCrawler instance
     * should handle */
    private static final int maxNumLinks = 50;
	
    /**
     * Initializes a new ReadWriteLock, ArrayList of type String, and a new WorkQueue.
     */
	public WebCrawler() {
		lock = new ReadWriteLock();
		seenlinksList = new ArrayList<String>();
		workers = new WorkQueue();
	}
	
	/**
	 * Synchronized check for if a link is already in the class variable
	 * seenLinksList.
	 * 
	 * @param link
	 * 			a URL string
	 * @return
	 * 			true if the URL is in seenLinksList, false if not.
	 */
	public boolean contains(String link){
		lock.lockReadOnly();
		try{ 
			return seenlinksList.contains(link);
		}
		finally{
			lock.unlockReadOnly();
		}
		
	}
	
	/**
	 * Crawls a passed in link for html text. Will start new work on any valid nested
	 * links encountered, and then add cleaned html text to the passed in InvertedIndex
	 * 
	 * @param link
	 * 			a URL link to crawl
	 * @param index
	 * 			an instance of InvertedIndex
	 */
	public void crawlLink(String link, ThreadSafeInvertedIndex index){
		ArrayList<String> localLinks;
		
		if(!seenlinksList.contains(link)){
			seenlinksList.add(link);
		}
		
		String HTML = HTMLCleaner.fetchHTML(link);
		localLinks = LinkParser.listLinks(HTML);
		
		HTML = HTMLCleaner.cleanHTML(HTML);
		int position = 1;
		InvertedIndex localIndex = new InvertedIndex();
		ArrayList<String> newList = HTMLCleaner.parseWords(HTML);		
		
		for (String parsedHTMLword :newList){
			localIndex.add(parsedHTMLword, link, position++);
		}
		index.addAll(localIndex);
		
		for (String subLinks : localLinks){
			try{
				lock.lockReadOnly();
				if (seenlinksList.size() >= maxNumLinks){
					return;
				}
			}
			finally{
				lock.unlockReadOnly();
			}
			
			if(subLinks.startsWith("#")){
				continue;
			}
			if(!subLinks.startsWith("http")){
				try {
					URL base = new URL(link);
					URL absolute = new URL(base, subLinks);
					subLinks = absolute.toString();
				} catch (MalformedURLException e) {
				}
			}
			if(!contains(subLinks)){
				synchronized (seenlinksList) {
					seenlinksList.add(subLinks);
				}
				workers.execute(new WebCrawlerMinion(subLinks, index));
			}
		}
	}
	
	/**
     * Shutsdown the work queue after all pending work is finished. After this
     * point, all additional calls to {@link #parseTextFiles(Path, String)} will
     * no longer work.
     */
    public synchronized void shutdown() {
    	while(pending > 0){
    		try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	workers.shutdown();
	}
	
    /**
     * This runnable object handles a single valid path and creates an inverted index
     * instance out of all found words with their position and the current
     */
	private class WebCrawlerMinion implements Runnable {
		
		private String link;
		ThreadSafeInvertedIndex index;
		
		public WebCrawlerMinion(String link, ThreadSafeInvertedIndex index) {
			this.link = link;
			this.index = index;
			incrementPending();
		}
		@Override
		public void run() {
			crawlLink(link, index);
			decrementPending();
		}
	}
	
	/**
	 * Indicates that we now have additional "pending" work to wait for. We
	 * need this since we can no longer call join() on the threads. (The
	 * threads keep running forever in the background.)
	 *
	 * We made this a synchronized method in the outer class, since locking
	 * on the "this" object within an inner class does not work.
	 */
	private synchronized void incrementPending() {
		pending++;
	}

	/**
	 * Indicates that we now have one less "pending" work, and will notify
	 * any waiting threads if we no longer have any more pending work left.
	 */
	private synchronized void decrementPending() {
		pending--;
		if (pending <= 0) {
			this.notifyAll();
		}
	}
}

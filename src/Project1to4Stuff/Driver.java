package Project1to4Stuff;
import java.nio.file.Path;
import java.nio.file.Paths;

import ServerStuff.JettyServerClass;

/**
 * This software driver class provides a consistent entry point for the search
 * engine. Based on the arguments provided to {@link #main(String[])}, it
 * creates the necessary objects and calls the necessary methods to build an
 * inverted index, process search queries, configure multithreading, and launch
 * a web server (if appropriate).
 */
public class Driver {

    /**
     * Flag used to indicate the following value is an input directory of text
     * files to use when building the inverted index.
     * 
     * @see "Projects 1 to 5"
     */
    public static final String INPUT_FLAG = "-input";

    /**
     * Flag used to indicate the following value is the path to use when
     * outputting the inverted index to a JSON file. If no value is provided,
     * then {@link #INDEX_DEFAULT} should be used. If this flag is not provided,
     * then the inverted index should not be output to a file.
     * 
     * @see "Projects 1 to 5"
     */
    public static final String INDEX_FLAG = "-index";

    /**
     * Flag used to indicate the following value is a text file of search
     * queries.
     * 
     * @see "Projects 2 to 5"
     */
    public static final String QUERIES_FLAG = "-query";

    /**
     * Flag used to indicate the following value is the path to use when
     * outputting the search results to a JSON file. If no value is provided,
     * then {@link #RESULTS_DEFAULT} should be used. If this flag is not
     * provided, then the search results should not be output to a file.
     * 
     * @see "Projects 2 to 5"
     */
    public static final String RESULTS_FLAG = "-results";

    /**
     * Flag used to indicate the following value is the number of threads to use
     * when configuring multithreading. If no value is provided, then
     * {@link #THREAD_DEFAULT} should be used. If this flag is not provided,
     * then multithreading should NOT be used.
     * 
     * @see "Projects 3 to 5"
     */
    public static final String THREAD_FLAG = "-threads";

    /**
     * Flag used to indicate the following value is the seed URL to use when
     * building the inverted index.
     * 
     * @see "Projects 4 to 5"
     */
    public static final String SEED_FLAG = "-seed";

    /**
     * Flag used to indicate the following value is the port number to use when
     * starting a web server. If no value is provided, then
     * {@link #PORT_DEFAULT} should be used. If this flag is not provided, then
     * a web server should not be started.
     */
    public static final String PORT_FLAG = "-port";

    /**
     * Default to use when the value for the {@link #INDEX_FLAG} is missing.
     */
    public static final String INDEX_DEFAULT = "index.json";

    /**
     * Default to use when the value for the {@link #RESULTS_FLAG} is missing.
     */
    public static final String RESULTS_DEFAULT = "results.json";

    /**
     * Default to use when the value for the {@link #THREAD_FLAG} is missing.
     */
    public static final int THREAD_DEFAULT = 5;
    
    /**
     * Default to use when the value for the {@link #PORT_FLAG} is missing.
     */
    public static final int PORT_DEFAULT = 8080;
    
    /**
     * Parses the provided arguments and, if appropriate, will build an inverted
     * index from a directory or seed URL, process search queries, configure
     * multithreading, and launch a web server.
     * 
     * @param args
     *            set of flag and value pairs
     */
    public static void main(String[] args) {    
        
    	/* Default setters */
        String indexOutput = INDEX_DEFAULT;
        String searchOutput = RESULTS_DEFAULT;
        int	numThreads = THREAD_DEFAULT;
        
        ArgumentParser newParser = new ArgumentParser();
        newParser.parseArguments(args);
        
        ThreadSafeInvertedIndex index = new ThreadSafeInvertedIndex();
        QueryFileParser queryParser = new QueryFileParser(index);
        
        /* Concurrent Child Classes */
        ConcurrentFiletoIndexBuilder newIndexBuilder;
        ConcurrentQueryFileParser concurrentQueryParser = null;
        
        WebCrawler newFetcher;
        
        if (newParser.hasFlag(PORT_FLAG)){
        	int port = PORT_DEFAULT;
        	if (newParser.hasValue(PORT_FLAG)){
        		try{
        			port = Integer.parseInt(newParser.getValue(PORT_FLAG));
        		} catch(NumberFormatException e){
        			System.out.println("Provided port number is not acceptable");
        			port = PORT_DEFAULT;
        		}
        	}
        	JettyServerClass newServer = new JettyServerClass(port);
        	newServer.launch();
        	
        } else{
	        /* Inverted Index Builder */
	        if ((newParser.hasFlag(INPUT_FLAG) && newParser.hasValue(INPUT_FLAG))|| newParser.hasFlag(SEED_FLAG)) {
		        if (newParser.hasFlag(THREAD_FLAG)) {
		        	if (newParser.hasValue(THREAD_FLAG)){
		        		try {
		        			numThreads = Integer.parseInt(newParser.getValue(THREAD_FLAG));
		        		} catch(NumberFormatException e){
		        			System.out.println("Provided number of threads is not acceptable");
		        			numThreads = THREAD_DEFAULT;
		        		}
		        	}
		        	if (newParser.hasFlag(SEED_FLAG) && newParser.hasValue(SEED_FLAG)){
		                String indexUrl = newParser.getValue(SEED_FLAG);
		        		newFetcher = new WebCrawler();
		        		newFetcher.crawlLink(indexUrl, index);
		        		newFetcher.shutdown();
		        	} else{
		        		newIndexBuilder = new ConcurrentFiletoIndexBuilder(numThreads);
		        		newIndexBuilder.readDirectory(Paths.get(newParser.getValue(INPUT_FLAG)), index);
		        		newIndexBuilder.shutdown();
		        	}
		        } else{
		            for (Path path : DirectoryTraverser.traverse(
		            		Paths.get(newParser.getValue(INPUT_FLAG)))) {
		            	FiletoIndexBuilder.readFile(path.toString(), index);
		            }
		        }
	        }
	        
	        /* Search Query Parser */
	        if (newParser.hasFlag(QUERIES_FLAG) && newParser.hasValue(QUERIES_FLAG)) {
	        	String queryPath = newParser.getValue(QUERIES_FLAG);
	        	if (newParser.hasFlag(THREAD_FLAG)){
	        		concurrentQueryParser = new ConcurrentQueryFileParser(numThreads, index);
	        		concurrentQueryParser.parseQueryFile(queryPath);
	        		concurrentQueryParser.shutdown();
	        	} else{
	        		queryParser.parseQueryFile(queryPath);
	        	}
	        }
	        
	        /* Inverted Index JSON Output */
	        if (newParser.hasFlag(INDEX_FLAG)) {
	        	if (newParser.hasValue(INDEX_FLAG)) {
	        		indexOutput = newParser.getValue(INDEX_FLAG);
	        	}
	       		index.outputToJSONFile(Paths.get(indexOutput));
	        }
	        
	        /* Search Query JSON Output */
	        if (newParser.hasFlag(RESULTS_FLAG)) {
	        	if (newParser.hasValue(RESULTS_FLAG)) {
	                searchOutput = newParser.getValue(RESULTS_FLAG);
	        	}
	        	if (newParser.hasFlag(THREAD_FLAG)|| newParser.hasFlag(SEED_FLAG)){
	        		concurrentQueryParser.toJSON(searchOutput);
	        	} else{
	        		queryParser.toJSON(searchOutput);
	        	}
	        }
        }
    }
}

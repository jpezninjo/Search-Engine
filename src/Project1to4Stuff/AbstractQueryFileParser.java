package Project1to4Stuff;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Abstract class pattern for @ConcurrentQueryFileParser and @QueryFileParser
 * This class cannot hold defined methods for toJSON() and addUncleaned() because they
 * require use of the private data LinkedHashMap originalstoSearchResults.
 */
public abstract class AbstractQueryFileParser {

	/**
	 * Searches a text file for query words
	 * 
	 * @param pathname
	 * 				name of file name
	 * @param originals
	 * 				a provided arrayList for holding original uncleaned query words
	 * @return	a set of cleaned query words for use in our search
	 */
	public void parseQueryFile(String queryPath){
		
		String line;
		
		try (BufferedReader r = Files.newBufferedReader(Paths.get(queryPath))) {

			while ((line = r.readLine()) != null) {
				parseLine(line);
			} 
		} catch (IOException ioe) { 
			System.out.println("Error: Could not find file " + queryPath);
		}
	}
	
	/**
	 * Tests if a line is empty. If not, calls partial search on index with 
	 * the line and then adds the resulting ArrayList<> into originalstoSearchResults */
	public abstract void parseLine(String line);
	
	/** Calls to JSON.toJSON() for partial search */
	public abstract void toJSON(String output);
	
}

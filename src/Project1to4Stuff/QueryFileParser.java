package Project1to4Stuff;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * This class is useful for reading a file containing query words for use in a 
 * search program.
 */
public class QueryFileParser extends AbstractQueryFileParser{
	
	/** A mapping of SearchResults paired to query words*/
	private final HashMap<String, ArrayList<SearchResult>> results;
	
	private final InvertedIndex invertedIndex;
	
	/**  Initializes a new LinkedHashMap */
	public QueryFileParser(InvertedIndex invertedIndex){
		results = new LinkedHashMap<String, ArrayList<SearchResult>>();
		this.invertedIndex = invertedIndex;
	}
	
	/**
	 * Tests if a line is empty. If not, calls partial search on index with 
	 * the line and then adds the resulting ArrayList<> into originalstoSearchResults
	 * 
	 * @param line
	 * 			line to parse
	 * @param index
	 * 			inverted index to do a partial search on using line
	 */
	public void parseLine(String line) {
		if (!line.isEmpty()){
			results.put(line, invertedIndex.partialSearch(
					FiletoIndexBuilder.clean(line).split(" ")));
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

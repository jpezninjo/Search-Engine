package Project1to4Stuff;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * The Index class contains a double-nested datatype of type TreeMap that stores
 * unique words as well as their locations (file name) and positions within
 * those locations using a nested TreeMap.
 */
public class InvertedIndex {

    /**
     * Our data type for holding mappings of words to their file locations and
     * of file locations to their positions of word.
     */
    private TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;

    /**
     * Constructor for Index. Initializes a new
     * TreeMap<String, TreeMap<String, TreeSet<Integer>>>.
     */
    public InvertedIndex() {
        index = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
    }

    /**
     * This function adds entries to our Index instance
     * 
     * @param word
     *            name of word for Index entry
     * @param pathname
     *            name of file where word was found
     * @param position
     *            a single position where word was found in pathname
     * @return true if add process was successful
     */
    public boolean add(String word, String pathname, Integer position) {

        if (!index.containsKey(word)) {
            index.put(word, new TreeMap<String, TreeSet<Integer>>());
        } 
        if (index.get(word).get(pathname) == null) {
            index.get(word).put(pathname, new TreeSet<Integer>());
        }
        return index.get(word).get(pathname).add(position);
    }
    
    /**
     * Partial search method takes in a query line for parsing, traverses the index
     * for all matches, and stores all found relevant data in an ArrayList of type SearchResult
     * to be returned 
     * 
     * @param line
     * 			query line use for searching
     * @return	an ArrayList of SearchResults found for the provided query line
     */
    public ArrayList<SearchResult> partialSearch(String[] line) {
    	
	 	ArrayList<SearchResult> newList = new ArrayList<SearchResult>();
		HashMap<String, SearchResult> newMap = new HashMap<String, SearchResult>();
		
		for (String searchWord : line){
			if (!searchWord.isEmpty()){
				for (String key : index.tailMap(searchWord, true).keySet()){
					if (key.startsWith(searchWord)){
						for (String locationKeys : index.get(key).keySet()){
							int frequency = index.get(key).get(locationKeys).size();  
							int firstPosition = index.get(key).get(locationKeys).first();
							
							if (!newMap.containsKey(locationKeys)){
								SearchResult result = new SearchResult(locationKeys, frequency, firstPosition);
								newMap.put(locationKeys, result);
							} else{
								newMap.get(locationKeys).updateFirstPosition(firstPosition);
								newMap.get(locationKeys).updateFrequency(frequency);
							}
						}
					} else{
						break;
					}
				}
			}
		}
		
		newList.addAll(newMap.values());
		return newList;
    }

    /**
     * Basically an addAll() method for InvertedIndex types
     * 
     * @param otherIndex
     * 				other index to be incorporated into this
     * @return
     */
    public void addAll(InvertedIndex otherIndex) {
    	for (String key : otherIndex.index.keySet()){
    		if (!this.index.containsKey(key)){
    			this.index.put(key, otherIndex.index.get(key));
    		} else{
    			for(String locations : otherIndex.index.get(key).keySet()){
    				if(!this.index.get(key).containsKey(locations)){
    					this.index.get(key).put(locations, otherIndex.index.get(key).get(locations));
    				}else{
    					this.index.get(key).get(locations).addAll(
    							otherIndex.index.get(key).get(locations));
    				}
    			}
    		}
    	}
    }
    
    /**
     * @returns the value returned by index.toString().
     * @see Objects class method toString().
     */
    @Override
    public String toString() {
        return index.toString();
    }

    /**
     * Calls the JSONWriter method toJSON() Used to create a JSON file using
     * the provided instance of Index
     * 
     * @param output
     *            File which JSON will be written to
     */
    public void outputToJSONFile(Path output) {
        JSONWriter.toJSON(output, index);
    }
}

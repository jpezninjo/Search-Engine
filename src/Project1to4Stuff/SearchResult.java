package Project1to4Stuff;
/**
 * This class is used for keeping track of search queries using location, frequencies, and first position
 * @see InvertexIndex
 * @see Project2
 *
 */
public class SearchResult implements Comparable<SearchResult> {
	
	/** Stores file where a search query word was found	 */
	private final String location;
	
	/** Stores the number of times a specific query word was found in location */
	private int frequency;
	  
	/** Stores the first position where a specific query word was found in location */
	private int firstPosition;	
	
	/**
	 * Initializes a SearchResult instance
	 * 
	 * @param location	@see location
	 * @param frequency	@see frequency
	 * @param firstPosition	@see firstPosition
	 */
	public SearchResult(String location, int frequency, int firstPosition) {
		this.location = location;
		this.firstPosition = firstPosition;
		this.frequency = frequency;
	}
    
	/** returns the value of location */
	public String getLocation() {
		return location;
	}

	/** returns the value of frequency */
	public int getFrequency() {
		return frequency;
	}

	/** returns the value of firstPosition */
	public int getFirstPosition() {
		return firstPosition;
	}
	
	/** 
	 * Adds the given newFreq to current count of frequency 
	 * 
	 * @param newFreq
	 * 			an integer value to add to current frequency
	 */
	public void updateFrequency(int newFreq) {
		this.frequency += newFreq;
	}
	
	/**
	 * Checks if the given position comes before the
	 * stored first position. If true, replaces values
	 * 
	 * @param position	
	 * 				new position to check current position against
	 * @return true	if firstPosition was changed
	 */
	public boolean updateFirstPosition(int position) {
		if (position < this.firstPosition) {
			this.firstPosition = position;
			return true;
		}
		return false;
	}

	/**
	 * Compares by frequency, then firstPosition if frequencies match
	 * If both are the same, compare by the location Strings
	 * 
	 * @return	-1 if local SearchResult is less than compared SearchResult,
	 * 			1 if local SearchResult is greater than compared SearchResult.	
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SearchResult other) {
		
		if (Integer.compare(this.frequency, other.getFrequency()) == 0){
			if (Integer.compare(this.firstPosition, other.getFirstPosition()) == 0){
				return this.location.compareTo(other.getLocation());
			} else{
				return Integer.compare(this.firstPosition, other.firstPosition);
			}
		} else{
			return Integer.compare(other.getFrequency(), this.frequency);
		}
	}
	
	/**
	 * Overridden inherited Object.toString() method.
	 */
	@Override
	public String toString(){
		return "location- " + location + ",frequency- " + 
				frequency + ",first position- " + firstPosition;
	}
}

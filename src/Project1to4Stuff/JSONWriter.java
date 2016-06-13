package Project1to4Stuff;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * This class contains static methods for creating and writing to
 * a JSON file given a valid Path and an appropriate TreeMap nested data structure.
 */
public class JSONWriter {
	
	/**
	 * Creates a JSON file out of the provided Path and 
	 * TreeMap<String, TreeMap<String, TreeSet<Integer>>>>
	 * 
	 * @param output
	 * 			path of JSON file to be created
	 * @param index
	 * 			the inverted index double nested data structure to be read 
	 * 			and used for JSON output
	 */
	public static void toJSON(Path output, TreeMap<String, TreeMap<String, TreeSet<Integer>>> index) {

		try (BufferedWriter bw = Files.newBufferedWriter(output, Charset.forName("UTF-8"))) {
			bw.write("{");
			if (!index.isEmpty()) {
				Entry<String, TreeMap<String, TreeSet<Integer>>> firstWordEntry = index.firstEntry();
				bw.newLine();
				bw.write(indent(1) + quote(firstWordEntry.getKey()) + ": {");
				TreeMap<String, TreeSet<Integer>> firstWordMap = firstWordEntry.getValue();
				Entry<String, TreeSet<Integer>> firstWordMapFirstFile = firstWordMap.firstEntry();
				bw.newLine();
				bw.write(indent(2) + quote(firstWordMapFirstFile.getKey()) + ": [");

				TreeSet<Integer> firstWordMapFilePositions = firstWordMapFirstFile.getValue();
				Integer firstWordfirstFilefirstPos = firstWordMapFilePositions.first();
				bw.newLine();
				bw.write(indent(3) + firstWordfirstFilefirstPos);
				Set<Integer> firstWordfirstFileMapOtherPos = firstWordMapFilePositions.tailSet(firstWordfirstFilefirstPos, false);
				if (!firstWordfirstFileMapOtherPos.isEmpty()) {
					for (Integer positions : firstWordfirstFileMapOtherPos) {
						bw.write(",");
						bw.newLine();
						bw.write(indent(3) + positions);
					}
				}
				bw.newLine();
				bw.write(indent(2) + "]");
				Map<String, TreeSet<Integer>> firstWordOtherMaps = firstWordMap.tailMap(firstWordMapFirstFile.getKey(),
						false);
				for (String filename : firstWordOtherMaps.keySet()) {
					bw.write(",");
					bw.newLine();
					bw.write(indent(2) + quote(filename) + ": [");
					Integer firstWordotherWordMapsfirstPos = firstWordOtherMaps.get(filename).first();
					bw.newLine();
					bw.write(indent(3) + firstWordotherWordMapsfirstPos);
					Set<Integer> firstWotherMOtherPos = firstWordOtherMaps.get(filename).tailSet(firstWordotherWordMapsfirstPos,
							false);
					for (Integer ints : firstWotherMOtherPos) {
						bw.write(",");
						bw.newLine();
						bw.write(indent(3) + ints);
					}
					bw.newLine();
					bw.write(indent(2) + "]");
				}
				
				Map<String, TreeMap<String, TreeSet<Integer>>> otherWordEntries = index.tailMap(firstWordEntry.getKey(), false);
				for (String words : otherWordEntries.keySet()) {
					bw.newLine();
					bw.write(indent(1) + "},");
					bw.newLine();
					bw.write(indent(1) + quote(words) + ": {");

					Entry<String, TreeSet<Integer>> otherWordsFirstFile = otherWordEntries.get(words).firstEntry();
					bw.newLine();
					bw.write(indent(2) + quote(otherWordsFirstFile.getKey()) + ": [");
					Integer otherWordsFirstFileFirstPos = otherWordsFirstFile.getValue().first();
					bw.newLine();
					bw.write(indent(3) + otherWordsFirstFileFirstPos);
					for (Integer positions : otherWordsFirstFile.getValue()
							.tailSet(otherWordsFirstFileFirstPos, false)) {
						bw.write(",");
						bw.newLine();
						bw.write(indent(3) + positions);
					}
					bw.newLine();
					bw.write(indent(2) + "]");
					Map<String, TreeSet<Integer>> otherWordsOtherFiles = otherWordEntries.get(words)
							.tailMap(otherWordsFirstFile.getKey(), false);

					for (String keys : otherWordsOtherFiles.keySet()) {
						bw.write(",");
						bw.newLine();
						bw.write(indent(2) + quote(keys) + ": [");
						Integer otherWordsOtherFilesFirstPos = otherWordsOtherFiles.get(keys).first();
						bw.newLine();
						bw.write("" + otherWordsOtherFilesFirstPos);
						Set<Integer> otherWordsOtherFilesOtherPos = otherWordsOtherFiles.get(keys)
								.tailSet(otherWordsOtherFilesFirstPos, false);
						for (Integer ints : otherWordsOtherFilesOtherPos) {
							bw.write(",");
							bw.newLine();
							bw.write(indent(3) + ints);
						}
						bw.newLine();
						bw.write(indent(2) + "]");
					}
				}
				bw.newLine();
				bw.write(indent(1) + "}");
				bw.newLine();
				bw.write("}");
			}
		} catch (IOException e) {
			System.out.println("Invalid path provided, could not use " + output);
		}
	}
	
	/**
	 * Writes out the given Map to a JSON file using
	 * a buffered writer and style UTF-8
	 * 
	 * @param output
	 * 				file to output to
	 * @param searchResultsOriginal
	 * 				a collection of original words
	 * @param finalMapping
	 * 				map to be read over and output to a JSON file
	 */
	public static void toJSON(String output, HashMap<String, ArrayList<SearchResult>> finalMapping){
		try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(output), Charset.forName("UTF-8"))) {

			String firstWord = (String) finalMapping.keySet().toArray()[0];
			
	        bw.write("{");
	        for (String originalwords : finalMapping.keySet()){
	        	if (originalwords != firstWord){
	        		bw.write(",");
	        	}
	        	bw.newLine();
	        	bw.write(JSONWriter.indent(1) + JSONWriter.quote(originalwords) + ": [");
	            if (finalMapping.containsKey(originalwords)){
	            	SearchResult firstResult;
	            	ArrayList<SearchResult> tempArray = finalMapping.get(originalwords);
	            	Collections.sort(tempArray);
	            	finalMapping.replace(originalwords, tempArray);
	            	
	            	if (!finalMapping.get(originalwords).isEmpty()){
	            		firstResult = finalMapping.get(originalwords).get(0);
	            	
		                for (SearchResult s : finalMapping.get(originalwords)){
		                	if (!s.equals(firstResult)){
		                		bw.write(",");
		                	}
		                	bw.newLine();
		                	bw.write(indent(2) + "{\n");
		                	bw.write(indent(3) + quote("where") + ": " + quote(s.getLocation()) +",\n");
		                	bw.write(indent(3) + quote("count") + ": " + s.getFrequency() + ",\n");
		                	bw.write(indent(3) + quote("index") + ": " + s.getFirstPosition() + "\n");
		                	bw.write(indent(2) + "}");
		                }
	                }   
	            }
	            bw.newLine();
	            bw.write(indent(1) + "]");
	        }
	        bw.newLine();
	        bw.write("}");

		} catch (IOException e) {
			System.out.println("Could not output to " + output);
		}   	
	}

	/**
	 * Returns a String of text surrounded in quotes
	 * 
	 * @param text
	 *            the word to be quoted
	 * @return String of text surrounded in quotes
	 */
	public static String quote(String text) {
		return "\"" + text + "\"";
	}

	/**
	 * Returns a String version of a certain number of indentations, each
	 * represented as a double space ("  ")
	 * 
	 * @param times
	 *            number of indentations
	 * @return a String version of indentation(s)
	 */
	public static String indent(int times) {
		return times > 0 ? String.format("%" + (times * 2) + "s", " ") : "";
	}
}

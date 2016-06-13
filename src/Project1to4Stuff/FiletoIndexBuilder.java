package Project1to4Stuff;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Contains static methods for reading in a file and adding members
 * to an instance of type Index 
 */
public class FiletoIndexBuilder {
	
	/**
	 * Uses BufferedReader to read in a file and adds the necessary found
	 * information to the provided instance of index
	 * 
	 * @param pathname
	 *            name of file to be read in
	 * @param index
	 *            reference to instance of Index
	 */
	public static void readFile(String pathname, InvertedIndex index) {
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(pathname), StandardCharsets.UTF_8)) {
			String line = null;
			Integer position = 1;
			
			while ((line = reader.readLine()) != null) {
				for (String eachWord : clean(line).split(" ")) {
					eachWord = FiletoIndexBuilder.clean(eachWord);
					if (!eachWord.isEmpty()) {
						index.add(eachWord, pathname, position);
						position++;
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Unable to parse file " + pathname + "and build invertex index");
		}
	}

	/**
	 * Converts text into lowercase, replaces special characters with an empty
	 * string, and trims whitespace at the start and end of the string. Special
	 * characters include any non-alphanumeric character or whitespace. 
	 * Unallowed characters include "_", "-", "@", ".", and so on.
	 * 
	 * @param text
	 *            input to clean
	 * @return cleaned text
	 */
	public static String clean(String text) {
		return text.toLowerCase().replaceAll("(?U)[^\\p{Alnum}\\p{Space}]+", "").trim();
	}
}
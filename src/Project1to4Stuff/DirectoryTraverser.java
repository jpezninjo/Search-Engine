package Project1to4Stuff;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Contains static methods for traversing a path and locating/storing all found
 * text files
 */
public class DirectoryTraverser {

    /**
     * Recursively traverses a provided Path and checks if it is a directory or
     * path. If it is a directory, call function again
     * 
     * @param path
     *            Path to be traversed; can be a directory or a file
     * @param fileArray
     *            Stores all found text files within the original call's Path
     */
    private static void traverse(Path path, ArrayList<Path> fileArray) {
        try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
            for (Path file : listing) {
                if (file.toString().toLowerCase().endsWith(".txt")) {
                    fileArray.add(file);
                } else if (Files.isDirectory(file)) {
                    traverse(file, fileArray);
                }
            }
        } catch (IOException e) {
            System.out.println("Invalid path: " + path);
        }
    }

    /**
     * Creates a new ArrayList of type Path and passes it to
     * a second version of the overloaded method traverse()
     * 
     * @param directory 
     *              Path to be traversed; can be a directory or a file
     * @returns a list of paths found in the provided directory
     */
    public static ArrayList<Path> traverse(Path directory) {
        ArrayList<Path> filenames = new ArrayList<Path>();
        traverse(directory, filenames);
        return filenames;
    }
}
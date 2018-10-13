package tk.dmanstrator.filehasher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

/**
 * Generates SHA-512 Hashes. Works for all files of a given Folder or for a given File.
 * 
 * @author DManstrator
 *
 */
public class Hasher {
    
    /**
     * Encoding for the File in case a filename or the Directory has Umlauts.
     */
    private static final String ENCODING = "UTF-8";
    
    /**
     * Hashing Method. Creates an Output File and returns the Path to it.
     * 
     * @param mainFolderName
     *            Folder to hash
     * @throws IllegalArgumentException
     *             When the Path is not a folder
     * @throws FileNotFoundException
     *             When the Output File couldn't be created
     * @throws UnsupportedEncodingException
     *             When the Encoding for the Output File is Unsupported
     */
    public String hash(String mainFolderName) throws IllegalArgumentException, FileNotFoundException, UnsupportedEncodingException {
        File mainPath = new File(mainFolderName);
        if (!mainPath.isDirectory())  {
            throw new IllegalArgumentException(String.format("Given Folder '%s' is not a folder, re-check that!", mainFolderName));
        }
        
        Map<File, String> filesWithHashes = getHashesOfFiles(mainFolderName);

        String tmpFolderName = getFolderName(mainFolderName);  // tmp for using folderName in Stream#map
        String folderName = tmpFolderName != null ? tmpFolderName : "null";
        
        StringBuilder builder = new StringBuilder("Path to scan: " + mainFolderName + System.lineSeparator());
        String hashes = filesWithHashes.entrySet().stream()
            .map(entry -> String.format("%s: %s", entry.getKey().getPath().substring(mainFolderName.length() - folderName.length()),
                    entry.getValue()))
            .collect(Collectors.joining(System.lineSeparator()));

        String output = builder.append(hashes).toString();
        
        String dateTime = getDateTime();
        File outputFile = new File(String.format("%s-Hashes_%s.txt", folderName, dateTime));
        try {
            PrintWriter writer = new PrintWriter(outputFile.getName(), ENCODING);
            writer.write(output);
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(String.format("An error occured while creating the output file '%s', "
                    + "make sure the program has the rights to do so!", outputFile.getAbsolutePath()));
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedEncodingException(String.format("An error occured because '%s' is an Unsupported Encoding!", ENCODING));
        }
        return outputFile.getAbsolutePath();
    }
    
    /**
     * Computes a SHA-512 Hash for every file of the given folder path.
     * 
     * @param mainFolderName
     *            Path to the starting Directory
     * @return a Map with the File as the Key and the SHA-512 Hash as the Value
     */
    public Map<File, String> getHashesOfFiles(String mainFolderName)  {
        return getHashesOfFiles(new File(mainFolderName));
    }
    
    /**
     * Computes a SHA-512 Hash for every file of the given folder path.
     * 
     * @param mainFolder
     *            Path as File to the starting Directory
     * @return a Map with the File as the Key and the SHA-512 Hash as the Value
     */
    public Map<File, String> getHashesOfFiles(File mainFolder)  {
        if (!mainFolder.isDirectory())  {
            return new HashMap<>();
        }
        Collection<File> listFiles = FileUtils.listFiles(mainFolder, null, true);
        return listFiles.stream().collect(Collectors.toMap(p -> p, p -> getHashOfFile(p)));
    }
    
    /**
     * Generates a SHA-512 Hash of a File.
     * 
     * @param filename
     *            Name of the File to get the Hash from
     * @return a SHA-512 Hash of given File or <code>null</code> if something
     *         went wrong
     */
    public String getHashOfFile(String filename) {
        return getHashOfFile(new File(filename));
    }

    /**
     * Generates a SHA-512 Hash of a File.
     * 
     * @param file
     *            File to get the Hash from
     * @return a SHA-512 Hash of given File or <code>null</code> if something
     *         went wrong
     * @see <a href=
     *      "https://stackoverflow.com/a/33085670">https://stackoverflow.com/a/33085670</a>
     */
    public String getHashOfFile(File file) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] digest = md.digest(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< digest.length; i++){
               sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            return null;
        }
    }
    
    /**
     * Gets the deepest Folder Name from a given Path.
     * 
     * @param mainFolderName
     *            Main Folder Name
     * @return the deepest Folder Name from a given Path or <code>null</code> if
     *         no folder could be retrieved from the given Folder Name.
     */
    private String getFolderName(String mainFolderName) {
        int lastIndex = mainFolderName.lastIndexOf('/');
        if (lastIndex == -1)  {  // Maybe Windows
            int lastIndexWindows = mainFolderName.lastIndexOf('\\');
            if (lastIndexWindows != -1)  {
                lastIndex = lastIndexWindows;
            }  else  {
                return null; // No slashes at all
            }
        }
        return mainFolderName.substring(lastIndex + 1, mainFolderName.length());
    }
    
    /**
     * Gets the current Time in Format <code>yyyy-MM-dd_HH-mm-ss</code>.
     * 
     * @return the current Time as a readable String
     */
    private String getDateTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(cal.getTime()).toString();
    }

}
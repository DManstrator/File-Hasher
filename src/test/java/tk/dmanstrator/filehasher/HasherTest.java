package tk.dmanstrator.filehasher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import tk.dmanstrator.filehasher.Hasher;

/**
 * Test Class for testing the Hasher. Used the following websites to compare the Hashes:<br />
 * <a href="https://emn178.github.io/online-tools/sha512_file_hash.html">https://emn178.github.io/online-tools/sha512_file_hash.html</a><br />
 * <a href="https://hash.online-convert.com/sha512-generator">https://hash.online-convert.com/sha512-generator</a>
 * 
 * @author DManstrator
 *
 */
public class HasherTest {
    
    /**
     * Map with Filepaths and SHA-512 Hashes to compare.
     */
    private final static Map<String, String> HASHES = new HashMap<String, String>() {
        private static final long serialVersionUID = 5636217836120806223L;
        {
            put("src\\test\\resources\\folder1\\Testfile.txt",
                    "861844d6704e8573fec34d967e20bcfef3d424cf48be04e6dc08f2bd58c729743371015ead891cc3cf1c9d34b49264b510751b1ff9e537937bc46b5d6ff4ecc8");
            put("src\\test\\resources\\folder1\\Another-Testfile.txt",
                    "f11a72665f07e477918c6c8cef29f30942ea7d4ac7dd15bb61b0f6d52e284c4b31978d3238878994a30b7ddcf22cb4ffdad82cb407ecd418a92d0ce78c1a49dc");
            put("src\\test\\resources\\folder2\\Test File.txt",
                    "861844d6704e8573fec34d967e20bcfef3d424cf48be04e6dc08f2bd58c729743371015ead891cc3cf1c9d34b49264b510751b1ff9e537937bc46b5d6ff4ecc8");
            put("src\\test\\resources\\folder2\\Testfile with Ümläuts.txt",
                    "c966988e7254a05ae05bb921a2955899dc47c209120b26faf96a6701cd884b258a3eb6bd5c035ed4e3e3865a84719332a05c458dad2da655a46ca29fa6d2dbc9");
        }
    };
    
    /**
     * Tests if the IllegalArgumentException is successfully thrown.
     * 
     * @throws IllegalArgumentException
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testHashingFoldersNotValidPath() throws IllegalArgumentException, FileNotFoundException, UnsupportedEncodingException  {
        Hasher hasher = new Hasher();
        hasher.hash("not/valid/path");
    }
    
    /**
     * Tests if the program works normally and creates an output file. Also used "Windows Slashes" (Backslashes).
     * 
     * @throws IllegalArgumentException
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    @Test
    public void testHashingFoldersRun() throws IllegalArgumentException, FileNotFoundException, UnsupportedEncodingException  {
        Hasher hasher = new Hasher();
        String hash = hasher.hash("src\\test\\resources\\folder1");
        Assert.assertNotNull(hash);
    }
    
    /**
     * Tests if the SHA-512 Hashes are successfully computed.
     */
    @Test
    public void testHashingFoldersNormal() {
        Hasher hasher = new Hasher();
        Map<File, String> hashesOfFiles = hasher.getHashesOfFiles("src/test/resources/folder1");
        for (Map.Entry<File, String> entry : hashesOfFiles.entrySet())  {
            boolean found = false;
            for (Map.Entry<String, String> hashEntry : HASHES.entrySet())  {
                if (entry.getKey().equals(new File(hashEntry.getKey())))  {
                    found = true;
                    Assert.assertEquals(entry.getValue(), hashEntry.getValue());
                    break;
                }
            }
            Assert.assertTrue(found);
        }
    }
    
    /**
     * Tests if the SHA-512 Hashes are successfully computed even there are Umlauts in the File Names.
     */
    @Test
    public void testHashingFoldersAdvanced() {
        Hasher hasher = new Hasher();
        Map<File, String> hashesOfFiles = hasher.getHashesOfFiles("src/test/resources/folder2");
        for (Map.Entry<File, String> entry : hashesOfFiles.entrySet())  {
            boolean found = false;
            for (Map.Entry<String, String> hashEntry : HASHES.entrySet())  {
                if (entry.getKey().equals(new File(hashEntry.getKey())))  {
                    found = true;
                    Assert.assertEquals(entry.getValue(), hashEntry.getValue());
                    break;
                }
            }
            Assert.assertTrue(found);
        }
    }
    
    /**
     * Checks if the hash of a normal file gets successfully computed.
     */
    @Test
    public void testHashingFileNormal()  {
        Hasher hasher = new Hasher();
        String hashOfFile = hasher.getHashOfFile("src/test/resources/folder1/Testfile.txt");
        Assert.assertEquals("861844d6704e8573fec34d967e20bcfef3d424cf48be04e6dc08f2bd58c729743371015ead891cc3cf1c9d34b49264b510751b1ff9e537937bc46b5d6ff4ecc8",
                hashOfFile);
    }
    
    /**
     * Checks if the hash of a file with Umlauts in the name gets successfully computed.
     */
    @Test
    public void testHashingFileAdvanced()  {
        Hasher hasher = new Hasher();
        String hashOfFile = hasher.getHashOfFile("src/test/resources/folder2/Testfile with Ümläuts.txt");
        Assert.assertEquals("c966988e7254a05ae05bb921a2955899dc47c209120b26faf96a6701cd884b258a3eb6bd5c035ed4e3e3865a84719332a05c458dad2da655a46ca29fa6d2dbc9",
                hashOfFile);
    }
    
    /**
     * Checks if an invalid folder path returns an empty map.
     */
    @Test
    public void testEmptyMap()  {
        Hasher hasher = new Hasher();
        Map<File, String> hashesOfFiles = hasher.getHashesOfFiles("not/valid/path");
        Assert.assertTrue(hashesOfFiles.isEmpty());
    }

}
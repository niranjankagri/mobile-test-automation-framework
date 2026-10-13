package com.mobile.automation.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.Logger;

import com.github.javafaker.Faker;
import com.mobile.automation.ui.selenium.DriverActions;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import org.apache.logging.log4j.LogManager;

/**
 * Static helpers without a driver: waits, random test data, dates, OS type, files and CSV.
 */
public class Util {

	public static Logger logger = LogManager.getLogger(Util.class.getName());

	/**
	 * Enum to hold the various OSType
	 */
	public enum OsType {
    	WINDOWS,
	    MACOS,
	   	OTHER
	}

	/** The faker for fake data generation*/
    private static Faker faker = new Faker();

    /**
     * Standard durations in seconds: *_WAIT for {@link #sleepInSec(int)}, *_TIMEOUT for
     * waits such as {@link DriverActions#isElementPresent(String, int)}. Prefer a wait for a
     * condition over a fixed sleep.
     */
	// Fixed pauses (seconds)
	public static int S_WAIT = 1;
	public static int M_WAIT = 3;
	public static int L_WAIT = 5;

	// Upper limits for waits (seconds)
	public static int S_TIMEOUT = 5;
	public static int M_TIMEOUT = 10;
	public static int L_TIMEOUT = 20;
	public static int XL_TIMEOUT = 30;
	public static int XXL_TIMEOUT = 60;

    /**
     * Sleep for the mentioned time in seconds
     *
     * @param timeInSecond
     * @param info - any message that needs to be displayed while sleeping
     */
    public static void sleepInSec(int timeInSecond, String info) {
        // beware: this takes time in seconds
        if (info != null) {
            logger.info("sleeping for: " + timeInSecond + ",info: " + info);
        }

        try {
            Thread.sleep(timeInSecond * 1000L);
        } catch (InterruptedException e) {
            // Keep the interrupt flag, so TestNG can still stop the thread
            Thread.currentThread().interrupt();
        }
    }

	/**
	 * Sleep for the mentioned time in seconds
	 *
	 * @param timeInSecond
	 */
	public static void sleepInSec(int timeInSecond) {
	    sleepInSec(timeInSecond, null);
	}

    /**
     * Gets an alpha numeric string of mentioned length
     *
     * @param len - desired length
     * @return the alpha numeric string
     */
    public static String getAlphaNumeric(int len) {
		final String ALPHA_NUM =
			"0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuffer sb = new StringBuffer(len);
		for (int i=0;  i<len;  i++) {
			int ndx = (int)(Math.random()*ALPHA_NUM.length());
			sb.append(ALPHA_NUM.charAt(ndx));
		}
		return sb.toString();
   }

    /**
     * Gets an upper-case letter string of mentioned length
     *
     * @param len - desired length
     * @return the letters
     */
    public static String getAlpha(int len) {
        final String ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuffer sb = new StringBuffer(len);
        for (int i=0;  i<len;  i++) {
            int ndx = (int)(Math.random()*ALPHA.length());
            sb.append(ALPHA.charAt(ndx));
        }
        return sb.toString();
   }

    /**
     * Random lower-case word of letters and digits, starting with "z"
     *
     * @param charCount - number of random characters after the "z"
     * @return the word
     */
    public static String getUniqueWord(int charCount) {
    	String name = "z"+getAlphaNumeric(charCount).toLowerCase();
    	logger.info("Constructed Name :"+ name);
    	return name;
    }

    /**
     * Random lower-case name of letters only, starting with "z"
     *
     * @param charCount - number of random letters after the "z"
     * @return the name
     */
    public static String getUniqueName(int charCount) {
        String name = "z"+getAlpha(charCount).toLowerCase();
        logger.info("Constructed Name :"+ name);
        return name;
    }

    /**
     * Gets the unique name with character count 10
     *
     * @return the unique name
     */
    public static String getUniqueName() {
        return getUniqueName(10);
    }

    /**
     * Gets the unique word with character count 10
     *
     * @return the unique word
     */
    public static String getUniqueWord() {
        return getUniqueWord(10);
    }

    /**
     * Gets a random 10-digit number as text.
     *
     * @return the digits
     */
    public static String getRandomNumber() {
  	  return getRandomNumber(10);
    }

    /**
     * Gets a random number with the given number of digits, as text (may start with 0).
     *
     * @param len - number of digits
     * @return the digits
     */
    public static String getRandomNumber(int len) {
        final String NUMERIC = "0123456789";
        StringBuffer sb = new StringBuffer(len);
        for (int i=0;  i<len;  i++) {
            int ndx = (int)(Math.random()*NUMERIC.length());
            sb.append(NUMERIC.charAt(ndx));
        }
        return sb.toString();
    }


    /**
     * Makes a unique Gmail address from an existing one with a "+tag"
     * (name@gmail.com becomes name+zabc...@gmail.com; mails still reach name@gmail.com).
     *
     * @param email a Gmail address
     * @return the unique address
     */
    public static String generateUniqueEmail(String email) {
        String generatedEmail = email.replace("@gmail.com", "+"  + getUniqueName() + "@gmail.com");
        return generatedEmail;
    }

   /**
    * Returns String list of given Strings.
    *
    * @param items - the items
    * @return the list
    */
   public static List<String> buildListOfStrings(String... items){
	   List<String> list = new ArrayList<String>();
	   for (String item : items){
		   list.add(item);
	   }
	   return list;
   }

   /**
    * Increment days to current date
    *
    * @param incrementDays - number of days to increment
    * @return Date - the new date
    */
	public static Date incrementDateByDays(int incrementDays) {
		LocalDate newDate = LocalDate.now().plusDays(incrementDays);
		return Date.from(newDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

    /**
     * Subtract days from the current date
     *
     * @param decrementDays - number of days to go back
     * @return Date - the new date, at the start of the day
     */
     public static Date decrementDateByDays(int decrementDays) {
         LocalDate newDate = LocalDate.now().minusDays(decrementDays);
         return Date.from(newDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
     }

	/**
    * get current time stamp
    *
    * @return time
    */
	public static LocalDateTime getCurrentTimeStamp() {
		LocalDateTime now = LocalDateTime.now();
		return now;
	}

	/**
	 * Unique name from a random word and the current nano time.
	 *
	 * @return the unique name with time
	 */
	public static String getUniqueNameWithTime() {
        return "z" + faker.lorem().word() + "_" + String.valueOf(System.nanoTime());
    }

    /**
     * Gets the OS type.
     *
     * @return the OS type-MacOS, Windows or Other
     */
    public static OsType getOsType() {
        String os = System.getProperty("os.name", "other").toLowerCase(Locale.ENGLISH);
        if (os.contains("mac") || os.contains("darwin")) {
            return OsType.MACOS;
        } else if (os.contains("win")) {
            return OsType.WINDOWS;
        } else {
            return OsType.OTHER;
        }
    }

    /**
     * Generates a random word
     *
     * @return the string
     */
    public static String getRandomWord() {
    	return String.valueOf(faker.lorem().word());
    }

    /**
     * Delete file
     *
     * @param filePath - the file path
     */
    public static void deleteFile(String filePath) {
        try {
            if(Files.deleteIfExists(Paths.get(filePath))) {
                logger.info("File deleted: " + filePath);
            } else {
                logger.info("Nothing to delete: " + filePath);
            }
        } catch (IOException e) {
            logger.error("Could not delete " + filePath, e);
        }
    }

    /**
     * Delete file from downloads folder of the system
     * @param fileName
     */
    public static void deleteFileFromDownloads(String fileName) {
        deleteFile(System.getProperty("user.home") + "/Downloads/"+fileName);
    }

    /**
     * Read a semicolon-separated CSV file
     *
     * @param filePath - the file path
     * @return List&lt;String[]&gt; - one array per row; empty when the file cannot be read
     */
    public static List<String[]> readCSVFile(String filePath) {
        List<String[]> csvFileData = new ArrayList<>();
        // Semicolon-separated, as exported by Excel in many European locales
        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
        // try-with-resources closes the file even when parsing fails
        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(filePath)).withCSVParser(parser).build()) {
            csvFileData = csvReader.readAll();
        } catch (Exception e) {
            logger.error("Could not read CSV " + filePath, e);
        }
        return csvFileData;
    }

    /**
     * Is CSV file contains the given data
     *
     * @param filePath - the file path
     * @param data - the file content
     * @return boolean - is content available in csv file
     */
    public static boolean isCSVFileContainData(String filePath, String data) {
        boolean isCSVFileContainData = false;
        List<String[]> csvFileData = readCSVFile(filePath);
        for (String[] row : csvFileData) {
            for (String cell : row) {
                if(cell.contains(data)){
                    isCSVFileContainData = true;
                    break;
                }
            }
        }
        return isCSVFileContainData;
    }

    /**
     * Checks whether a folder contains a file with exactly this name.
     *
     * @param directoryPath the folder
     * @param fileName      the file name
     * @return true when the file is there (false also when the folder does not exist)
     */
     public static boolean checkFilesExist(String directoryPath, String fileName )  {
        File files = new File(directoryPath);
        String[] listOfFiles=files.list();
        // list() returns null when the folder does not exist
        if (listOfFiles == null) {
            return false;
        }
        // For each name in the path array
        boolean flag=false;
        for (int i = 0; i < listOfFiles.length; i++) {
            if ( listOfFiles[i].contentEquals(fileName) ) {
                flag=true;
                break;
            }
        }
        return flag;
     }

     /**
      * Checks whether the user's Downloads folder contains the file.
      *
      * @param fileName the file name
      * @return true when the file is there
      */
      public static boolean checkDownloadFilesExist(String fileName) {
          return checkFilesExist(System.getProperty("user.home") + "/Downloads", fileName);
      }

      /**
       * Get the count of files from the directory path.
       *
       * @param directoryPath - the directory path
       * @return integer - the number of files (0 when the folder does not exist)
       */
      public static int getFilesCount(String directoryPath) {
          String[] listOfFiles = new File(directoryPath).list();
          // list() returns null when the folder does not exist
          return listOfFiles == null ? 0 : listOfFiles.length;
      }

      /**
       * Get the count of files in the users Downloads folder
       *
       *  @return integer
       */
      public static int getDownloadFilesCount() {
          return getFilesCount(System.getProperty("user.home") + "/Downloads");
      }

      /**
       * Deletes every file directly inside a folder (TestListener empties the screenshot folder).
       *
       * @param folderPath the folder
       * @return true when the folder existed and was emptied, false when it does not exist
       */
       public static boolean deleteAllFilesFromFolder(String folderPath)  {
          File folder = new File(folderPath);
          String[] listOfFiles = folder.list();
          // list() returns null when the folder does not exist, e.g. on a fresh CI checkout
          if (listOfFiles == null) {
              return false;
          }
          for (String fileName : listOfFiles) {
              // Delete inside the given folder (it used to always use resources/screenshots)
              deleteFile(new File(folder, fileName).getPath());
          }
          return true;
       }
}

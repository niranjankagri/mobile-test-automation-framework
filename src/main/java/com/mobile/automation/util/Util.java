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
 * Utility class
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
     * 
     * Variables used as input to sleep functions, OR for various UI functions 
     * Example : sleep(S_MS_WAIT)  OR {@link DriverActions#waitForElementPresent(String)}
     * 
     * **/
    
	public static int S_WAIT = 1;
	public static int M_WAIT = 3;
	public static int L_WAIT = 5;
	
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
            Thread.sleep(timeInSecond * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
     * Gets an alpha string of mentioned length
     *
     * @param len - desired length
     * @return the alpha numeric string
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
     * Get Unique name of a particular count
     *
     * @param charCount - desired char count
     * @return the unique name
     */
    public static String getUniqueWord(int charCount) {
    	String name = "z"+getAlphaNumeric(charCount).toLowerCase();
    	logger.info("Constructed Name :"+ name);
    	return name;
    }
    
    /**
     * Get Unique name of a particular count
     *
     * @param charCount - desired char count
     * @return the unique name
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
     * Gets the unique name with character count 10
     *
     * @return the unique name
     */
    public static String getUniqueWord() {
        return getUniqueWord(10);
    }
    
    /**
     * Gets a Random Number b/w 1 and 10.
     *
     * @return the random number
     */
    public static String getRandomNumber() {
  	  return getRandomNumber(10);
    }
    
    /**
     * Gets Unique Random Number within a given range.
     *
     * @param min - the min
     * @param max - the max
     * @return the random number
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
     * GetRandom Email Address.
     *
     * @param emailAccount the email account. Example : {@link TafWebAppConstants#GMAIL_EMAIL_USER_NAME
     * @return the string
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
     * Decrement days to current date
     * 
     * @param incrementDays - number of days to increment
     * @return Date - the new date
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
	 * Ge Unique name with time.
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
                logger.info("File deleted successfully"); 
            } else { 
                logger.info("Failed to delete the file"); 
            }
        } catch (IOException e) {
            e.printStackTrace();
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
     * Read CSV file
     *
     * @param filePath - the file path
     * @return List<String[]> - file data in list format
     */
    public static List<String[]> readCSVFile(String filePath) {
        List<String[]> csvFileData = new ArrayList<>();
        try {
            FileReader filereader = new FileReader(filePath);
            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
            CSVReader csvReader = new CSVReaderBuilder(filereader) 
                    .withCSVParser(parser) 
                    .build(); 
            csvFileData = csvReader.readAll();
        } catch (Exception e) {
            e.printStackTrace();
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
     * Check our specific file is exist or not in directorys.
     * @param directoryPath
     * @param fileName
     * @return
     */
     public static boolean checkFilesExist(String directoryPath, String fileName )  {
        File files = new File(directoryPath);
        String[] listOfFiles=files.list();
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
      * Send file name and path to checkFileExist method
      * @param fileName
      * @return
      */
      public static boolean checkDownloadFilesExist(String fileName) {
          return checkFilesExist(System.getProperty("user.home") + "/Downloads", fileName);
      }
      
      /**
       * Get the count of files from the directory path.
       *
       * @param directoryPath - the directory path
       * @return integer - the number of files
       */
      public static int getFilesCount(String directoryPath) {
          File files = new File(directoryPath);
          return files.list().length;
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
       * Check our specific file is exist or not in directorys.
       * @param directoryPath
       * @param fileName
       * @return
       */
       public static boolean deleteAllFilesFromFolder(String folderPath)  {
          File files = new File(folderPath);
          String[] listOfFiles=files.list();
          // For each name in the path array
          boolean flag=false;
          for (int i = 0; i < listOfFiles.length; i++) {
              deleteFile(System.getProperty("user.dir") + "/resources/screenshots/" + listOfFiles[i]);
          }
          return flag;
       }
}

package com.mobile.automation.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Part;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Can be used to read emails
 *
 */

public class ReadEmails {
    String host = "pop.gmail.com";
    String mailStoreType = "pop3";
    public Logger logger = LogManager.getLogger(ReadEmails.class.getName());

    /**
     * Read email from folder
     * 
     * @param email
     * @param pwd
     * @param folderName
     * @return
     */
    public String readEmailFromFolder(String email, String pwd, String folderName, boolean retry) {
        Properties props = new Properties();
        String message = null;
        try {
            props.load(new FileInputStream(new File("resources/config/smtp.prop")));
            Session session = Session.getDefaultInstance(props);
            Store store = session.getStore("imaps");
            store.connect("smtp.gmail.com", email, pwd);
            Folder inbox = store.getFolder(folderName);
            inbox.open(Folder.READ_WRITE);

            int messageCount = inbox.getMessageCount();
            logger.info("Total Messages :- " + messageCount);

            if (messageCount > 0) {
                Message[] messages = inbox.getMessages();
                logger.info("Latest Email Subject : " + messages[messageCount - 1].getSubject());
                message = getContent(messages[messageCount - 1]);

                messages[messageCount - 1].setFlag(Flags.Flag.DELETED, true);
            }

            inbox.expunge();
            inbox.close(false);
            store.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (retry && message == null)
            readEmailFromFolder(email, pwd, folderName, retry);
        return message;
    }

    /**
     * Get COntent method to read content of emails
     * 
     * @param part
     * @return
     */
    public String getContent(Part part) {
        String emailText = "";
        try {
            // check if the content is plain text
            if (part.isMimeType("text/plain")) {
                logger.info("This is plain text");
                logger.info("---------------------------");
                emailText = ((String) part.getContent());
            }
            // check if the content has attachment
            else if (part.isMimeType("multipart/*")) {
                logger.info("This is a Multipart");
                logger.info("---------------------------");
                Multipart mp = (Multipart) part.getContent();
                int count = mp.getCount();
                for (int i = 0; i < count; i++)
                    emailText = emailText.concat(getContent(mp.getBodyPart(i)));
            }
            // check if the content is a nested message
            else if (part.isMimeType("message/rfc822")) {
                logger.info("This is a Nested Message");
                logger.info("---------------------------");
                emailText = emailText.concat(getContent((Part) part.getContent()));
            } else if (part.isMimeType("text/html")) {
                emailText = ((String) part.getContent());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return emailText;
    }

    public String getPasswordFromEmail(String email, String pwd) {
        return getPasswordFromEmail(email, pwd, true);
    }
    
    public String getPasswordFromEmail(String email, String pwd, boolean retry) {
        String password = null;
        String emailContent = readEmailFromFolder(email, pwd, "inbox", retry);
        if (emailContent != null) {
            List<String> messageList = Arrays.asList(emailContent.split("<p>"));
            for (String part : messageList) {
                if (part.startsWith("Password : ")) {
                    password = part.replace("Password : ", "").replace("</p>", "").replaceAll("\\s", "");
                    break;
                }
            }
        }
        return password;
    }
}
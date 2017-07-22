package wrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author Adithya
 */
public class FeedConnProperties {

    private String feedname, hostname, username, password, fileType, fileSrcExtension, fileTrgExtension, srcSep, trgSep, remotePath, remoteModTime, filePath, trgFileheader, srcFileheader, startDate, cronTime, attachlog, mailpassto, mailpasscc, passreptofail, mailfailto, mailfailcc, statusFilePath, mailConfiguration;

    private Properties loadConn;
    private FileInputStream propFile;
    private final String dateFormat;
    private final String timeFormat;
    private File connectionFile;

    public FeedConnProperties() throws FileNotFoundException, IOException, IOException {
        loadConn = new Properties();
        connectionFile = new File("conn.properties");
        propFile = new FileInputStream(connectionFile);
        loadConn.load(propFile);
        System.out.println("File: " + connectionFile.getAbsolutePath());
        feedname = loadConn.getProperty("feedname");
        hostname = loadConn.getProperty("hostname");
        username = loadConn.getProperty("username");
        password = loadConn.getProperty("password");
        fileType = loadConn.getProperty("fileType");
        fileSrcExtension = loadConn.getProperty("fileSrcExtension");
        fileTrgExtension = loadConn.getProperty("fileTrgExtension");
        srcSep = loadConn.getProperty("srcSep");
        trgSep = loadConn.getProperty("trgSep");
        remotePath = loadConn.getProperty("remotePath");
        remoteModTime = loadConn.getProperty("remoteModTime");
        filePath = loadConn.getProperty("filePath");
        srcFileheader = loadConn.getProperty("srcFileheader");
        trgFileheader = loadConn.getProperty("trgFileheader");
        startDate = loadConn.getProperty("startDate");
        cronTime = loadConn.getProperty("cronTime");
        attachlog = loadConn.getProperty("attachlog");
        mailpassto = loadConn.getProperty("mailpassto");
        mailpasscc = loadConn.getProperty("mailpasscc");
        passreptofail = loadConn.getProperty("passreptofail");
        mailfailto = loadConn.getProperty("mailfailto");
        mailfailcc = loadConn.getProperty("mailfailcc");
        statusFilePath = loadConn.getProperty("statusFilePath");
        dateFormat = loadConn.getProperty("dateFormat");
        timeFormat = loadConn.getProperty("timeFormat");
        mailConfiguration = loadConn.getProperty("mailConfiguration");

        propFile.close();

        if (!Files.exists(Paths.get(statusFilePath), LinkOption.NOFOLLOW_LINKS)) {
            Files.createDirectory(Paths.get(statusFilePath));

        }

    }

    public String getMailConfiguration() {
        return mailConfiguration;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public String getStatusFilePath() {
        return statusFilePath;
    }

    public String getFeedname() {
        return feedname;
    }

    public String getHostname() {
        return hostname;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFileType() {
        return fileType;
    }

    public String getFileSrcExtension() {
        return fileSrcExtension;
    }

    public String getFileTrgExtension() {
        return fileTrgExtension;
    }

    public String getSrcSep() {
        return srcSep;
    }

    public String getTrgSep() {
        return trgSep;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public String getRemoteModTime() {
        return remoteModTime;
    }

    public String getLocalFilePath() {

//        String storeFilePath = filePath + remotePath.substring(remotePath.lastIndexOf("/"), remotePath.lastIndexOf(".")) + "_" + remoteModTime + remotePath.substring(remotePath.lastIndexOf("."), remotePath.length());
        return filePath;
    }

    public String getSrcFileheader() {
        return srcFileheader;
    }

    public String getTrgFileheader() {
        return trgFileheader;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getCronTime() {
        return cronTime;
    }

    public String getAttachlog() {
        return attachlog;
    }

    public String getMailpassto() {
        return mailpassto;
    }

    public String getMailpasscc() {
        return mailpasscc;
    }

    public String getPassreptofail() {
        return passreptofail;
    }

    public String getMailfailto() {
        return mailfailto;
    }

    public String getMailfailcc() {
        return mailfailcc;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getConnectionFile() {
        return connectionFile.getAbsolutePath();
    }

}

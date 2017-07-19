/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fact;

import engines.FTPEngine;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import wrapper.CSVThreadModeler;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import mail.Mail;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import wrapper.FeedConnProperties;

/**
 *
 * @author Ravindra
 */
public class FACTStartPoint implements Job {

    private FeedConnProperties feedConnProperties;
    private FTPEngine fTPEngine;

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {

        try {
            //Property File Loading
            feedConnProperties = new FeedConnProperties();
            //calling starts here
            System.out.println("Demo");
            //FTP code
            fTPEngine = new FTPEngine(feedConnProperties.getHostname(), 21, feedConnProperties.getUsername(), feedConnProperties.getPassword());
            File statusFile = new File(feedConnProperties.getStatusFilePath() + "/" + feedConnProperties.getFeedname() + "_status.properties");
            System.out.println("File Name: " + statusFile.getName());
            if (statusFile.exists()) {
//                System.out.println("Checked");
                Properties outSource = new Properties();
                outSource.load(new FileInputStream(statusFile));
                Date storeModTimeStamp = new SimpleDateFormat("yyyyMMddHHmmss").parse(outSource.getProperty("newModTime"));

                Date currentModTimeStamp = new SimpleDateFormat("yyyyMMddHHmmss").parse(fTPEngine.getRemoteModTimeStamp(feedConnProperties.getRemotePath()));
                System.out.println("Old: " + outSource.getProperty("newModTime") + " : " + storeModTimeStamp);
                System.out.println("New: " + fTPEngine.getRemoteModTimeStamp(feedConnProperties.getRemotePath()) + " : " + currentModTimeStamp);
                //CSVThreadModeler
                if (currentModTimeStamp.after(storeModTimeStamp)) {
//                    System.out.println("Checked 1");
                    String remotePath = feedConnProperties.getRemotePath();
                    String localPath = feedConnProperties.getLocalFilePath() + remotePath.substring(remotePath.lastIndexOf("/"), remotePath.lastIndexOf(".")) + "_" + fTPEngine.getRemoteModTimeStamp(feedConnProperties.getRemotePath()) + remotePath.substring(remotePath.lastIndexOf("."), remotePath.length());
                    if (fTPEngine.getFile(remotePath, localPath)) {
//                        System.out.println("Checked 2");
                        callModeler(localPath, fTPEngine.getRemoteModTimeStamp(feedConnProperties.getRemotePath()));
                    }

                } else {
                    System.err.println("New File Not found");
                }
            } else {
                System.out.println("Not Checked");
                String remotePath = feedConnProperties.getRemotePath();
                String localPath = feedConnProperties.getLocalFilePath() + remotePath.substring(remotePath.lastIndexOf("/"), remotePath.lastIndexOf(".")) + "_" + fTPEngine.getRemoteModTimeStamp(feedConnProperties.getRemotePath()) + remotePath.substring(remotePath.lastIndexOf("."), remotePath.length());
                if (fTPEngine.getFile(remotePath, localPath)) {
                    Properties outSourceProp = new Properties();
                    outSourceProp.setProperty("feedName", feedConnProperties.getFeedname());
                    outSourceProp.setProperty("newModTime", fTPEngine.getRemoteModTimeStamp(feedConnProperties.getRemotePath()));
                    outSourceProp.setProperty("oldModTime", fTPEngine.getRemoteModTimeStamp(feedConnProperties.getRemotePath()));
                    outSourceProp.setProperty("originalSrcFile", localPath);
                    outSourceProp.setProperty("originalTrgFile", localPath);
                    outSourceProp.setProperty("status", "Initial File Fetched");

                    outSourceProp.save(new FileOutputStream(statusFile), new Date().toString());
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(FACTStartPoint.class.getName()).log(Level.SEVERE, null, ex);
            sendExecptionEmail(feedConnProperties.getMailpassto() + "," + feedConnProperties.getMailfailto(), feedConnProperties.getMailpasscc() + "," + feedConnProperties.getMailfailcc(), "Comparision Failed with Execption", "Hi, \n" + ex.toString());
        }

    }

    public void callModeler(String newLocalPath, String currentModTime) throws Exception {
        System.out.println("Call Modeler");
        Properties outSourceProp = null;

        File outSourceFile = new File(feedConnProperties.getStatusFilePath() + "/" + feedConnProperties.getFeedname() + "_status.properties");

        if (!outSourceFile.exists()) {
            System.out.println("Status File Not Found");
        } else {
            System.out.println("Updating the Properties");
            outSourceProp = new Properties();
            outSourceProp.load(new FileInputStream(outSourceFile));
            String oldModTime = outSourceProp.getProperty("newModTime");
            outSourceProp.setProperty("oldModTime", oldModTime);
            outSourceProp.setProperty("newModTime", currentModTime);
            outSourceProp.setProperty("originalSrcFile", newLocalPath);
            outSourceProp.setProperty("originalTrgFile", feedConnProperties.getLocalFilePath() + newLocalPath.substring(newLocalPath.lastIndexOf("/"), newLocalPath.lastIndexOf("_")) + "_" + oldModTime + newLocalPath.substring(newLocalPath.lastIndexOf("."), newLocalPath.length()));
            outSourceProp.setProperty("diffFile", newLocalPath.split(".csv")[0] + "_Results.xls");//Check Where to Store the File
            outSourceProp.setProperty("status", "FTP File Download Success");
            outSourceProp.save(new FileOutputStream(outSourceFile), new Date().toString());
            System.out.println("Src File: " + outSourceProp.getProperty("originalSrcFile"));
            System.out.println("Trg File: " + outSourceProp.getProperty("originalTrgFile"));
        }

        String srcName = newLocalPath.substring(newLocalPath.lastIndexOf("/") + 1, newLocalPath.lastIndexOf("."));
        String trgName = outSourceProp.getProperty("originalTrgFile").substring(outSourceProp.getProperty("originalTrgFile").lastIndexOf("/") + 1, outSourceProp.getProperty("originalTrgFile").lastIndexOf("."));

        String srcqry = "select * from " + srcName;
        String trgqry = "select * from " + trgName;

        CSVThreadModeler csvtm = new CSVThreadModeler(newLocalPath, outSourceProp.getProperty("originalTrgFile"), feedConnProperties.getSrcFileheader(), feedConnProperties.getTrgFileheader(), feedConnProperties.getSrcSep(), feedConnProperties.getTrgSep(), feedConnProperties.getFileSrcExtension(), feedConnProperties.getFileTrgExtension(), outSourceProp.getProperty("diffFile"));
        List basics = csvtm.writeDiff(srcqry, trgqry, srcName, trgName);

        System.out.println("Basic Info :" + basics);
        setBasicstoStatus(outSourceProp, outSourceFile, basics);

        sendEmail(basics, outSourceProp.getProperty("diffFile"));

    }

    public void setBasicstoStatus(Properties connProperties, File saveFile, List basics) throws IOException {
        connProperties.setProperty("srcCount", basics.get(0).toString());
        connProperties.setProperty("trgCount", basics.get(1).toString());
        connProperties.setProperty("srcDiffCount", basics.get(2).toString());
        connProperties.setProperty("trgDiffCount", basics.get(3).toString());
        connProperties.setProperty("status", "Feed Process Finished");
        connProperties.save(new FileOutputStream(saveFile), new Date().toString());
    }

    public void sendExecptionEmail(String toMail, String ccMail, String subject, String body) {
        try {
            Mail mail = new Mail();
            mail.createSession();
            mail.sendExecptionEmail(toMail, ccMail, subject, body);
        } catch (IOException ex) {
            Logger.getLogger(FACTStartPoint.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(FACTStartPoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendEmail(List basics, String attachFile) throws IOException, MessagingException {
        Mail mail = new Mail();
        mail.createSession();
        if (basics.get(3).equals("0") && basics.get(4).equals("0")) {
            mail.sendEmail(feedConnProperties.getMailpassto(), feedConnProperties.getMailpasscc(), createMailSubject(feedConnProperties.getFeedname(), basics), createBody(basics, feedConnProperties.getFeedname()), "");
        } else {
            mail.sendEmail(feedConnProperties.getMailfailto(), feedConnProperties.getMailfailcc(), createMailSubject(feedConnProperties.getFeedname(), basics), createBody(basics, feedConnProperties.getFeedname()), attachFile);
        }
    }

    public String createMailSubject(String feedName, List basics) {
        String subject;

        if (basics.get(3).equals("0") && basics.get(4).equals("0")) {
            subject = "File Comparision Passed for " + feedName;
        } else {
            subject = "File Comparision Failed for " + feedName;
        }

        return subject;
    }

    public String createBody(List basics, String feedName) {
        StringBuilder body = new StringBuilder();

        body.append("Hi,\n");
        body.append("Please find the comparision counts for feed " + feedName + "\n");
        body.append("Source Count: " + basics.get(0) + "\n");
        body.append("Target Count: " + basics.get(1) + "\n");
        body.append("Source UnMatched Count: " + basics.get(2) + "\n");
        body.append("Target UnMatched Count: " + basics.get(3) + "\n");

        return body.toString();

    }
}

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

            //FTP code
            fTPEngine = new FTPEngine(feedConnProperties.getHostname(), 21, feedConnProperties.getUsername(), feedConnProperties.getPassword());
            File statusFile = new File(feedConnProperties.getStatusFilePath() + "/" + feedConnProperties.getFeedname() + "_status.properties");
            System.out.println("File Name: " + statusFile.getName());
            if (statusFile.exists()) {

                Properties outSource = new Properties();
                outSource.load(new FileInputStream(statusFile));
                Date storeModTimeStamp = new SimpleDateFormat("yyyyMMddHHmmss").parse(outSource.getProperty("newModTime"));

                Date currentModTimeStamp = new SimpleDateFormat("yyyyMMddHHmmss").parse(fTPEngine.getRemoteModTimeStamp(feedConnProperties.getRemotePath()));
                System.out.println("Old: " + outSource.getProperty("newModTime") + " : " + storeModTimeStamp);
                System.out.println("New: " + fTPEngine.getRemoteModTimeStamp(feedConnProperties.getRemotePath()) + " : " + currentModTimeStamp);
                //CSVThreadModeler
                if (currentModTimeStamp.after(storeModTimeStamp)) {
                    String remotePath = feedConnProperties.getRemotePath();
                    String localPath = feedConnProperties.getLocalFilePath() + remotePath.substring(remotePath.lastIndexOf("/"), remotePath.lastIndexOf(".")) + "_" + fTPEngine.getRemoteModTimeStamp(feedConnProperties.getRemotePath()) + remotePath.substring(remotePath.lastIndexOf("."), remotePath.length());
                    if (fTPEngine.getFile(remotePath, localPath)) {
                        System.out.println("Processing the FTP file");
                        callModeler(localPath, fTPEngine.getRemoteModTimeStamp(feedConnProperties.getRemotePath()));

                    }

                } else {
                    System.err.println("New File Not found");
                    Properties outSourceProp = new Properties();
                    outSourceProp.load(new FileInputStream(statusFile));
                    String localPath = outSourceProp.getProperty("originalnewFile");
                    String remotePath = outSourceProp.getProperty("originaloldFile");
                    String newModTime = outSourceProp.getProperty("newModTime");
                    String oldModTime = outSourceProp.getProperty("oldModTime");
                    outSourceProp.setProperty("newModTime", newModTime);
                    outSourceProp.setProperty("oldModTime", oldModTime);
                    outSourceProp.setProperty("originalnewFile", localPath);
                    outSourceProp.setProperty("originaloldFile", remotePath);
                    String diffFile = outSourceProp.getProperty("diffFile");
                    if (diffFile == null || diffFile.isEmpty()) {
                        outSourceProp.setProperty("status", "Intial File Fetched. Still new file not arrived");
                    } else {
                        outSourceProp.setProperty("status", "No new File found to Compare");
                    }
                    outSourceProp.setProperty("connFile", feedConnProperties.getConnectionFile());
                    outSourceProp.setProperty("mailConfig", feedConnProperties.getMailConfiguration());
                    outSourceProp.store(new FileOutputStream(statusFile), new Date().toString());
                    sendExecptionEmail(feedConnProperties.getMailfailto(), feedConnProperties.getMailfailcc(), "No New File Found to compare on " + new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss a").format(currentModTimeStamp), "Hi, \n No updated file found for date: " + new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss a").format(currentModTimeStamp));
                }
            } else {
                System.out.println("Creating the Prop File");
                String remotePath = feedConnProperties.getRemotePath();
                String localPath = feedConnProperties.getLocalFilePath() + remotePath.substring(remotePath.lastIndexOf("/"), remotePath.lastIndexOf(".")) + "_" + fTPEngine.getRemoteModTimeStamp(feedConnProperties.getRemotePath()) + remotePath.substring(remotePath.lastIndexOf("."), remotePath.length());
                if (fTPEngine.getFile(remotePath, localPath)) {
                    Properties outSourceProp = new Properties();
                    outSourceProp.setProperty("feedName", feedConnProperties.getFeedname());
                    outSourceProp.setProperty("newModTime", fTPEngine.getRemoteModTimeStamp(feedConnProperties.getRemotePath()));
                    outSourceProp.setProperty("oldModTime", fTPEngine.getRemoteModTimeStamp(feedConnProperties.getRemotePath()));
                    outSourceProp.setProperty("originalnewFile", localPath);
                    outSourceProp.setProperty("originaloldFile", localPath);
                    outSourceProp.setProperty("status", "Initial File Fetched");
                    outSourceProp.setProperty("connFile", feedConnProperties.getConnectionFile());
                    outSourceProp.setProperty("mailConfig", feedConnProperties.getMailConfiguration());
                    outSourceProp.save(new FileOutputStream(statusFile), new Date().toString());
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(FACTStartPoint.class.getName()).log(Level.SEVERE, null, ex);
            sendExecptionEmail(feedConnProperties.getMailfailto(), feedConnProperties.getMailfailcc(), "Comparision Failed with Execption", "Hi, \n Comparision Failed wiht Execption, PFA the execption below,\n" + ex.toString());
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
//
//            outSourceProp.save(new FileOutputStream(outSourceFile), new Date().toString());
            System.out.println("New File: " + outSourceProp.getProperty("originalnewFile"));
            System.out.println("Old File: " + outSourceProp.getProperty("originaloldFile"));
            outSourceProp.setProperty("oldModTime", oldModTime);
            outSourceProp.setProperty("newModTime", currentModTime);
            String srcName = newLocalPath.substring(newLocalPath.lastIndexOf("/") + 1, newLocalPath.lastIndexOf("."));
//            System.out.println("SRC: " + srcName);
//            System.out.println("Old File: " + outSourceProp.getProperty("originalnewFile").substring(outSourceProp.getProperty("originalnewFile").lastIndexOf("/") + 1,outSourceProp.getProperty("originalnewFile").length()));
            String trgName = outSourceProp.getProperty("originalnewFile").substring(outSourceProp.getProperty("originalnewFile").lastIndexOf("/") + 1, outSourceProp.getProperty("originalnewFile").lastIndexOf("."));

            System.out.println("TRG: " + trgName);

            String srcqry = "select * from " + srcName;
            String trgqry = "select * from " + trgName;

            CSVThreadModeler csvtm = new CSVThreadModeler(newLocalPath, outSourceProp.getProperty("originaloldFile"), feedConnProperties.getSrcFileheader(), feedConnProperties.getTrgFileheader(), feedConnProperties.getSrcSep(), feedConnProperties.getTrgSep(), feedConnProperties.getFileSrcExtension(), feedConnProperties.getFileTrgExtension(), newLocalPath.split(".csv")[0] + "_Results.xls");
            List basics = csvtm.writeDiff(srcqry, trgqry, srcName, trgName);

            System.out.println("Basic Info :" + basics);
            setBasicstoStatus(outSourceProp, outSourceFile, basics, newLocalPath, outSourceProp.getProperty("newModTime"), currentModTime);

            sendEmail(basics, outSourceProp.getProperty("diffFile"));
        }

    }

    public void setBasicstoStatus(Properties connProperties, File saveFile, List basics, String newLocalPath, String oldModTime, String currentModTime) throws IOException {
        connProperties.setProperty("newCount", basics.get(0).toString());
        connProperties.setProperty("oldCount", basics.get(1).toString());
        connProperties.setProperty("newDiffCount", basics.get(2).toString());
        connProperties.setProperty("oldDiffCount", basics.get(3).toString());
        if (basics.get(2).toString() != "0" || basics.get(3).toString() != "0") {
            connProperties.setProperty("status", "Old and New file has different records");
        } else {
            connProperties.setProperty("status", "Old and New file has same records");
        }
        connProperties.setProperty("diffFile", newLocalPath.split(".csv")[0] + "_Results.xls");//Check Where to Store the File
        connProperties.setProperty("connFile", feedConnProperties.getConnectionFile());
        connProperties.setProperty("mailConfig", feedConnProperties.getMailConfiguration());

        connProperties.setProperty("originalnewFile", newLocalPath);
        connProperties.setProperty("originaloldFile", feedConnProperties.getLocalFilePath() + newLocalPath.substring(newLocalPath.lastIndexOf("/"), newLocalPath.lastIndexOf("_")) + "_" + oldModTime + newLocalPath.substring(newLocalPath.lastIndexOf("."), newLocalPath.length()));
//            outSourceProp.setProperty("diffFile", newLocalPath.split(".csv")[0] + "_Results.xls");//Check Where to Store the File
        connProperties.setProperty("status", "FTP File Download Success");
        connProperties.setProperty("connFile", feedConnProperties.getConnectionFile());
        connProperties.setProperty("mailConfig", feedConnProperties.getMailConfiguration());
        connProperties.save(new FileOutputStream(saveFile), new Date().toString());
    }

    public void sendExecptionEmail(String toMail, String ccMail, String subject, String body) {
        System.out.println("Execption Email Triggered");
        try {
            Mail mail = new Mail(feedConnProperties.getMailConfiguration());
            mail.createSession();
            mail.sendExecptionEmail(toMail, ccMail, subject, body);
        } catch (IOException | MessagingException ex) {
            Logger.getLogger(FACTStartPoint.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(ex);
        }
    }

    public void sendEmail(List basics, String attachFile) throws IOException, MessagingException {
        Mail mail = new Mail(feedConnProperties.getMailConfiguration());
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
        StringBuffer body = new StringBuffer();

        body.append("Hi,\n");
        body.append("Please find the comparision counts for feed " + feedName + "\n");
        body.append("Source Count: " + basics.get(0) + "\n");
        body.append("Target Count: " + basics.get(1) + "\n");
        body.append("Source UnMatched Count: " + basics.get(2) + "\n");
        body.append("Target UnMatched Count: " + basics.get(3) + "\n");

        return body.toString();

    }
}

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
import java.text.SimpleDateFormat;
import java.util.Date;
import wrapper.CSVThreadModeler;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
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
                System.out.println("Checked");
                Properties outSource = new Properties();
                outSource.load(new FileInputStream(statusFile));
                Date storeModTimeStamp = new SimpleDateFormat("yyyyMMddHHmmss").parse(outSource.getProperty("newModTime"));

                Date currentModTimeStamp = new SimpleDateFormat("yyyyMMddHHmmss").parse(fTPEngine.getRemoteModTimeStamp(feedConnProperties.getRemotePath()));
                System.out.println("Old: " + outSource.getProperty("newModTime") + " : " + storeModTimeStamp);
                System.out.println("New: " + fTPEngine.getRemoteModTimeStamp(feedConnProperties.getRemotePath()) + " : " + currentModTimeStamp);
                //CSVThreadModeler
                if (currentModTimeStamp.after(storeModTimeStamp)) {
                    System.out.println("Checked 1");
                    String remotePath = feedConnProperties.getRemotePath();
                    String localPath = feedConnProperties.getLocalFilePath() + remotePath.substring(remotePath.lastIndexOf("/"), remotePath.lastIndexOf(".")) + "_" + fTPEngine.getRemoteModTimeStamp(feedConnProperties.getRemotePath()) + remotePath.substring(remotePath.lastIndexOf("."), remotePath.length());
                    if (fTPEngine.getFile(remotePath, localPath)) {
                        System.out.println("Checked 2");
                        callModeler(feedConnProperties, localPath, fTPEngine.getRemoteModTimeStamp(feedConnProperties.getRemotePath()));
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
                    outSourceProp.setProperty("newModTime", fTPEngine.getRemoteModTimeStamp(feedConnProperties.getRemotePath()));
                    outSourceProp.setProperty("oldModTime", fTPEngine.getRemoteModTimeStamp(feedConnProperties.getRemotePath()));
                    outSourceProp.setProperty("originalSrcFile", localPath);
                    outSourceProp.setProperty("originalTrgFile", localPath);

                    outSourceProp.save(new FileOutputStream(statusFile), new Date().toString());
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(FACTStartPoint.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    public void callModeler(FeedConnProperties feedConnProperties, String newLocalPath, String currentModTime) throws Exception {
        System.out.println("Call Modeler");
        Properties outSourceProp = null;

        File outSourceFile = new File(feedConnProperties.getStatusFilePath() + "/" + feedConnProperties.getFeedname() + "_status.properties");
        FileInputStream fis = new FileInputStream(outSourceFile);
        FileOutputStream fos = new FileOutputStream(outSourceFile);
        if (!outSourceFile.exists()) {
            System.out.println("Status File Not Found");
        } else {
            System.out.println("Updating the Properties");
            outSourceProp = new Properties();
            outSourceProp.load(fis);
            String oldModTime = outSourceProp.getProperty("newModTime");
            outSourceProp.setProperty("oldModTime", oldModTime);
            outSourceProp.setProperty("newModTime", currentModTime);
            outSourceProp.setProperty("originalSrcFile", newLocalPath);
            outSourceProp.setProperty("originalTrgFile", feedConnProperties.getLocalFilePath() + newLocalPath.substring(newLocalPath.lastIndexOf("/"), newLocalPath.lastIndexOf("_")) + "_" + oldModTime + newLocalPath.substring(newLocalPath.lastIndexOf("."), newLocalPath.length()));
            outSourceProp.setProperty("diffFile", newLocalPath.split(".csv")[0] + "_Results.xls");//Check Where to Store the File
            outSourceProp.save(fos, new Date().toString());
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
        setBasicstoStatus(outSourceProp, fos, basics);
        fis.close();

    }

    public void setBasicstoStatus(Properties connProperties, FileOutputStream saveFileStream, List basics) throws IOException {
        connProperties.setProperty("srcCount", basics.get(0).toString());
        connProperties.setProperty("trgCount", basics.get(1).toString());
        connProperties.setProperty("srcDiffCount", basics.get(2).toString());
        connProperties.setProperty("trgDiffCount", basics.get(3).toString());
        connProperties.save(saveFileStream, new Date().toString());
        saveFileStream.close();
    }
}

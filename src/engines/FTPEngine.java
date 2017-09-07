package engines;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPCmd;

public class FTPEngine {

    private FTPClient ftpClient;

    public FTPEngine(String hostname, int portno, String username, String password) throws IOException {

        ftpClient = new FTPClient();
        ftpClient.connect(hostname, portno);
        ftpClient.login(username, password);
    }

//    public boolean getFile(String remotePathWithFilename, String localPathWithFilename) throws FileNotFoundException, IOException {
//        System.out.println("Processing the Data");
//        File localfile = new File(localPathWithFilename);
//
//        if (!localfile.getParentFile().exists()) {
//
//            Files.createDirectory(Paths.get(localfile.getParent()));
//        }
//        InputStream fis = ftpClient.retrieveFileStream(remotePathWithFilename);
//        FileOutputStream fos = new FileOutputStream(localfile);
//
//        byte[] bytesArray = new byte[5120];
//        int bytesRead = -1;
//        while ((bytesRead = fis.read(bytesArray)) != -1) {
//            fos.write(bytesArray, 0, bytesRead);
//        }
//        
//
//        fis.close();
//        fos.close();
//        boolean status = ftpClient.completePendingCommand();
////        System.out.println("Println: " + status);
//
//        return status;
//
//    }
    public boolean getFile(String remotePathWithFilename, String localPathWithFilename) throws FileNotFoundException, IOException {
        System.out.println("Processing the Data");
        File localfile = new File(localPathWithFilename);

        if (!localfile.getParentFile().exists()) {

            Files.createDirectory(Paths.get(localfile.getParent()));
        }

        boolean status = false;

        status = ftpClient.retrieveFile(remotePathWithFilename, new FileOutputStream(localPathWithFilename));
        
        System.out.println("Download Process Done");
        return status;

    }

    public String getRemoteModTimeStamp(String fileName) throws IOException {
        String timeStamp = ftpClient.getModificationTime(fileName);
        return timeStamp.substring(timeStamp.indexOf(" ") + 1, timeStamp.length()).trim();
    }

    public static void main(String[] args) {

        FTPEngine ftpobj = null;

        try {

            ftpobj = new FTPEngine("mdc1vr1002", 21, "scripts", "st@rs1");
            boolean success = ftpobj.getFile("/data/aggregator/test_preview/aggregator_output/site/category.csv", "E:/category12.csv");
            System.out.println("Time : " + ftpobj.getRemoteModTimeStamp("/data/aggregator/test_preview/aggregator_output/site/category.csv"));
            if (!success) {
                System.out.println("Ftp to download the file unsuccessful.");
            } else {
                System.out.println("Ftp to download the file successful.");
            }

        } catch (IOException ex) {
            Logger.getLogger(FTPEngine.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {

                if (ftpobj.ftpClient.isConnected()) {
                    ftpobj.ftpClient.logout();
                    ftpobj.ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }
}

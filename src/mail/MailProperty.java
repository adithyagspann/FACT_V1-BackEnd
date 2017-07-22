/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Adithya
 */
public class MailProperty {

    private String fromMail, password, host, socketProperty, port, socketFactory_class, mail_smtp_auth;
    private Properties mailProperties,connectionProperties;
    

    public MailProperty(String mailPath) throws FileNotFoundException, IOException {
        mailProperties = new Properties();
        FileInputStream fis = new FileInputStream(mailPath);
        mailProperties.load(fis);
        fromMail = mailProperties.getProperty("fromEmail");
        password = mailProperties.getProperty("password");
        host = mailProperties.getProperty("mail.smtp.host");
        socketProperty = mailProperties.getProperty("mail.smtp.socketFactory.port");
        port = mailProperties.getProperty("mail.smtp.port");
        socketFactory_class = "javax.net.ssl.SSLSocketFactory"; //SSL Factory Class
        mail_smtp_auth = "true";
        connectionProperties = new Properties();
        connectionProperties.put("mail.smtp.port", port);
        connectionProperties.put("mail.smtp.socketFactory.port", socketProperty);
        connectionProperties.put("mail.smtp.host", host);
        connectionProperties.put("mail.smtp.socketFactory.class", socketFactory_class);
        connectionProperties.put("mail.smtp.auth", mail_smtp_auth);
    }

    public String getFromMail() {
        return fromMail;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public String getSocketProperty() {
        return socketProperty;
    }

    public String getPort() {
        return port;
    }

    public String getSocketFactory_class() {
        return socketFactory_class;
    }

    public String getMail_smtp_auth() {
        return mail_smtp_auth;
    }

    public Properties getMailProperties() {
        System.out.println("Properties: "+connectionProperties);
        return connectionProperties;
    }

}

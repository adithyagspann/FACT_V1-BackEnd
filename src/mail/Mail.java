/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mail;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author Adithya
 */
public class Mail {

    private MailProperty mailProperty;
    private Session session;

    public Mail(String mailPath) throws IOException {
        mailProperty = new MailProperty(mailPath);
        System.out.println("Username: " + mailProperty.getFromMail());
    }

    public void createSession() {
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailProperty.getFromMail(), mailProperty.getPassword());
            }
        };
//        System.out.println("Properties: " + mailProperty.getMailProperties());
        session = Session.getDefaultInstance(mailProperty.getMailProperties(), auth);

    }

    public void sendEmail(String toMail, String ccMail, String subject, String body, String attachMentFile) throws MessagingException, UnsupportedEncodingException {
        System.out.println("Email Sending");
        MimeMessage msg = new MimeMessage(session);
        System.out.println("Basic Mail info done");
        msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
        msg.addHeader("format", "flowed");
        msg.addHeader("Content-Transfer-Encoding", "8bit");
        
        msg.setFrom(new InternetAddress(mailProperty.getFromMail(), "NoReply-ID"));
        
//        msg.setReplyTo(InternetAddress.parse("gspann151@gmail.com", false));
        msg.setSubject(subject, "UTF-8");

        msg.setSentDate(new Date());

//        msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccMail, false));
        if (!attachMentFile.isEmpty() && !attachMentFile.equals("")) {
            System.out.println("File attachment in progess");
            // Create the message body part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            messageBodyPart.setText(body);

            // Create a multipart message for attachment
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Second part is attachment
            messageBodyPart = new MimeBodyPart();

            DataSource source = new FileDataSource(attachMentFile);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(attachMentFile.substring(attachMentFile.lastIndexOf("/"), attachMentFile.length()));
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            msg.setContent(multipart);
        } else {
            System.out.println("Mail with body is processing");
            msg.setText(body);

        }
        System.out.println("To Mail: "+toMail);
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toMail, false));
        msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccMail, false));

// Send message
        Transport.send(msg);
        System.out.println("EMail Sent Successfully with attachment!!");

    }

    public void sendExecptionEmail(String toMail, String ccMail, String subject, String body) throws MessagingException, UnsupportedEncodingException {
        System.out.println("Exception email Processing");
        MimeMessage msg = new MimeMessage(session);
        msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
        msg.addHeader("format", "flowed");
        msg.addHeader("Content-Transfer-Encoding", "8bit");

        msg.setFrom(new InternetAddress(mailProperty.getFromMail(), "NoReply-ID"));

        msg.setSubject(subject, "UTF-8");

        msg.setSentDate(new Date());

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toMail, false));
        msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccMail, false));

        // Create the message body part
        // Fill the message
        msg.setText(body);

        // Send message
        Transport.send(msg);
        System.out.println("Exception EMail Sent Successfully with attachment!!");
    }

    public static void main(String args[]) throws IOException, MessagingException {
        Mail m = new Mail("C:/Users/Admin/Documents/NetBeansProjects/FACT_V1/mailConfig.properties");
        m.createSession();
        System.out.println("");
        m.sendEmail("adithya.pathipaka@gspann.com, adithya_pathipaka@hotmail.com", "adithyapathipaka@gmail.com", "Test", "Test Normal", "");
        m.sendExecptionEmail("adithya.pathipaka@gspann.com", "adithyapathipaka@gmail.com", "Test", "Test Execption");

    }
}

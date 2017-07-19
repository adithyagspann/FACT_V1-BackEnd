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

    public Mail() throws IOException {
        mailProperty = new MailProperty();
        System.out.println("Username: "+mailProperty.getFromMail());
    }

    public void createSession() {
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailProperty.getFromMail(), mailProperty.getPassword());
            }
        };

        session = Session.getDefaultInstance(mailProperty.getMailProperties(), auth);

    }

    public void sendEmail(String toMail, String ccMail, String subject, String body, String attachMentFile) throws MessagingException, UnsupportedEncodingException {
        MimeMessage msg = new MimeMessage(session);
        msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
        msg.addHeader("format", "flowed");
        msg.addHeader("Content-Transfer-Encoding", "8bit");

        msg.setFrom(new InternetAddress(mailProperty.getFromMail(), "NoReply-ID"));
       
//        msg.setReplyTo(InternetAddress.parse("gspann151@gmail.com", false));

        msg.setSubject(subject, "UTF-8");

        msg.setSentDate(new Date());

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toMail, false));
        msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccMail, false));

        // Create the message body part
        BodyPart messageBodyPart = new MimeBodyPart();

        // Fill the message
        messageBodyPart.setText(body);

        if (!attachMentFile.isEmpty() || !attachMentFile.equals("")) {
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
        }
        // Send message
        Transport.send(msg);
        System.out.println("EMail Sent Successfully with attachment!!");

    }
    
    public void sendExecptionEmail(String toMail, String ccMail, String subject, String body) throws MessagingException, UnsupportedEncodingException
    {
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
        BodyPart messageBodyPart = new MimeBodyPart();

        // Fill the message
        messageBodyPart.setText(body);
     
        // Send message
        Transport.send(msg);
        System.out.println("EMail Sent Successfully with attachment!!");
    }

    public static void main(String args[]) throws IOException, MessagingException
    {
        Mail m = new Mail();
        m.createSession();
        System.out.println("");
        m.sendEmail("adithya.pathipaka@gmail.com", "adithyapathipaka@gmail.com", "Test", "Test", "");
    }
}

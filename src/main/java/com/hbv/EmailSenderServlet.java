package com.hbv;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class EmailSenderServlet extends Thread {

    private  Path pdfFile;
    private String recipient;
    private String subject;
    private String body;
    private String bodyPart;

    public EmailSenderServlet(String recipient, String subject, String body) {
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
    }

    public EmailSenderServlet(String recipient, String subject, String bodyPart, Path pdfFile) {
        this.recipient = recipient;
        this.bodyPart = bodyPart;
        this.subject = subject;
        this.pdfFile = pdfFile;
    }

    @Override
    public void run() {
        try {
            MyLogger.info("initialized Thread");
            MyLogger.info("SMTP Server Process with Threading");

            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.port", "587");

            String mailSender = "coronaappljma@gmail.com";
            String mailSenderPassword = "fenjfqbjsxgthyha";
            Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mailSender, mailSenderPassword);
                }
            });
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailSender));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject("Email confirmation");
            message.setSubject(subject);
            message.setText(body);

            MimeMultipart multipart = new MimeMultipart();
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(bodyPart);

            byte[] pdfBytes = Files.readAllBytes(pdfFile);
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            attachmentBodyPart.setContent(pdfBytes,"application/pdf");
            attachmentBodyPart.setFileName("Confirmation.pdf");

            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentBodyPart);

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("Email confirmation sent to " + recipient);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

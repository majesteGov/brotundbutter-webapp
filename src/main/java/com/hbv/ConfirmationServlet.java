package com.hbv;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

public class ConfirmationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Verification de l'existence de la session
        HttpSession session = request.getSession(false);
        if (session == null) {
            // Si la session n'existe pas, redirection vers la page de connexion
            response.sendRedirect("signIn.html");
        } else {

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"user_info.pdf\"");

            // Recuperation des informations de session
            String username = (String) request.getSession().getAttribute("username");
            String firstName = (String) request.getSession().getAttribute("firstName");
            String lastName = (String) request.getSession().getAttribute("lastName");
            String email = (String) request.getSession().getAttribute("email");
            String city = (String) request.getSession().getAttribute("city");
            String postalCode = (String) request.getSession().getAttribute("postalCode");
            LocalDate date1 = LocalDate.parse((String) request.getSession().getAttribute("date1"));
            LocalTime heure1 = LocalTime.parse((String) request.getSession().getAttribute("heure1"));
            LocalDate date2 = LocalDate.parse((String) request.getSession().getAttribute("date2"));
            LocalTime heure2 = LocalTime.parse((String) request.getSession().getAttribute("heure2"));
            String vaccine = (String) request.getSession().getAttribute("vaccine");

            MyLogger.info("Session has been initialized");

            PdfDocument pdfDocument = new PdfDocument();
            Document document = new Document();

            try {
                PdfWriter.getInstance(document, new FileOutputStream("Confirmation.pdf"));
                document.open();
                Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
                Paragraph paragraph = new Paragraph("Appointment Reservation for " + firstName + " " + lastName);
                paragraph.setFont(titleFont);
                document.add(paragraph);

                document.add(new Paragraph("Firstname: " + lastName));
                document.add(new Paragraph("Lastname: " + firstName));
                document.add(new Paragraph("Email: " + email));
                document.add(new Paragraph("City: " + city));
                document.add(new Paragraph("Postal Code: " + postalCode));

                Paragraph firstHeader = new Paragraph("First Appointment:");
                firstHeader.setSpacingBefore(20);
                document.add(firstHeader);
                document.add(new Paragraph("Date" + date1 + "Hour" + heure1));
                document.add(new Paragraph("Vaccine: " + vaccine));

                Paragraph secondHeader = new Paragraph("Second Appointment:");
                document.add(secondHeader);
                secondHeader.setSpacingBefore(20);
                document.add(new Paragraph("Date" + date2 + "Hour" + heure2));
                document.add(new Paragraph("Vaccine: " + vaccine));

                Paragraph confirmation = new Paragraph("Thanks you for your Appointment Reservation! Make sure you take you ID card on your Vaccination day.");
                confirmation.setAlignment(Element.ALIGN_CENTER);
                confirmation.setSpacingBefore(20);
                document.add(confirmation);

            } catch (DocumentException de) {
                de.printStackTrace();
            } finally {
                pdfDocument.close();
            }

            // Creer la partie corps du message

            String messageBodyPart = "Sehr geehrte/r " + "<b>" + firstName + " " + lastName + "</b>"
                    + ",<br/><br/>anbei erhalten Sie die Terminbestätigung für Ihren Impftermin. "
                    + "Bitte bringen Sie das Dokument zum Impfzentrum mit.<br/><br/>Mit freundlichen Grüßen<br/>"
                    + "Ihr Impfzentrum";

            // Cr�er le corps de la pi�ce jointe
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            MimeBodyPart.setFileName(username + "Reservation.pdf");
//                    attachmentBodyPart.setDataHandler(new javax.activation.DataHandler(new javax.mail.util.ByteArrayDataSource(baos.toByteArray(), "application/pdf")));

            // Cr�ez le message multipart et ajoutez-y les parties du corps.
            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentBodyPart);

            // D�finir le message multipartite comme le message e-mail
            message.setContent(multipart);

            Transport.send(message);

            File attachment =;
            EmailSenderServlet emailSenderServlet = new EmailSenderServlet(email, subject, messageBodyPart, attachment);
            emailSenderServlet.start();

            System.out.println("Le mail a ete envoye avec succes.");
        } catch(MessagingException e){
            throw new RuntimeException(e);
        }

    }
});

        mailsend.start();

        //verification du THread

        try{
        mailsend.join();
        }catch(Exception e){
        // TODO: handle exception
        System.out.println("Thread is unreachable");
        }


        // set the content type
        response.setContentType("text/html");

        // Write a success message to the response output stream
        response.getWriter().write("<h1>Die Terminbestätigung wurde erfolgreich an Ihre E-Mail-Adresse gesendet!</h1>");
        }
        }
        }

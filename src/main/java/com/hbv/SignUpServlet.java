package com.hbv;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class SignUpServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get the request Parameter
        String username = request.getParameter("username");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String city = request.getParameter("city");
        String postalCode = request.getParameter("postalCode");

        // Validation des entrees

        if (username != null && username.isEmpty() ) {
            response.getWriter().println("<h1>Le nom d'utilisateur est vide</h1>");
            return;
        }
        else if (firstName != null && firstName.isEmpty() ) {

            response.getWriter().println("<h1>Veuillez entrez votre prenom.</h1>");

            return;
        }
        else if (lastName != null && lastName.isEmpty() ) {

            response.getWriter().println("<h1>Veuillez entrez votre nom</h1>");

            return;
        }
        else if (password != null && password.isEmpty() ) {

            response.getWriter().println("<h1><Veuillez entrez un mot de passe</h1>");

            return;
        }
        else if (email != null && email.isEmpty() ) {

            response.getWriter().println("<h1>Veuillez entrer une adresse mail<h1>");

            return;
        }
        // Chargement du Driver JDBC
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {

            System.out.println("La classe Driver n'existe pas");

            e.printStackTrace();
        }

        // Creation de la connexion a la base de donnees
        try (
                Connection connection = MyConnectionPool.borrowConnection()) {

            // Verification si le mom d'utilisateur n'est pas encore pri
            String sql = "SELECT * FROM usersApp WHERE username=?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    response.sendRedirect("register.html?error=usernameExists");
                    return;
                }
            }
            // Verification si l'adresse mail n'est pas deja utilisee

            sql = "SELECT * FROM usersApp WHERE email=?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, email);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    response.sendRedirect("register.html?error=emailExists");
                    return;
                }
            }

            // Insertion dans la base de Donnees

            sql = "INSERT INTO usersApp (username, first_name, last_name, password, email, city, postal_code) VALUES (?,?,?,?,?,?,?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, username);
                statement.setString(2, firstName);
                statement.setString(3, lastName);
                statement.setString(4, password);
                statement.setString(5, email);
                statement.setString(6, city);
                statement.setString(7, postalCode);

                int ex = statement.executeUpdate();

                // Verification si l'insertion a reussi
                if (ex > 0) {
                    String to = email; // adresse e-mail du destinataire
                    String from = "coronaappljma@gmail.com"; // adresse e-mail de l'exp�diteur
                    String passwordmail = "fenjfqbjsxgthyha"; // mot de passe de l'expediteur
                    // Configuration des proprietes pour se connecter au serveur SMTP de Gmail
                    Properties properties = new Properties();
                    properties.put("mail.smtp.host", "smtp.gmail.com");
                    properties.put("mail.smtp.auth", "true");
                    properties.put("mail.smtp.port", "587");
                    properties.put("mail.smtp.starttls.enable", "true");

                    // Creation d'une session pour l'envoi de l'e-mail
                    Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(from, passwordmail);
                        }
                    });

                    /*Je créé un nouveau thread pour l'envoi de l'e-mail, afin de ne pas bloquer le thread
                     * principal. Cela permet d'améliorer la réactivité de l'application pour l'utilisateur.
                     */

                    // Création d'un thread pour l'envoi de l'e-mail
                    Thread emailThread = new Thread(new Runnable() {
                        @Override
                        public void run()
                        {
                            try {
                                // Creation d'un objet MimeMessage
                                MimeMessage message = new MimeMessage(session);

                                // Definition des details de l'e-mail
                                message.setFrom(new InternetAddress(from));
                                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                                message.setSubject("Einrichtung des Kontos für die Buchung von Terminen für die Covid-19-Impfung.");
                                message.setText("Hallo Frau/Herr" + lastName + "," + "<br> Ihr KOnto wurde erfolgreich erstellt. Klicken Sie "
                                        + "auf den folgenden Link um Ihnen enloggen zu können: " + " http://localhost:8084/App/login.html");

                                // Envoi de l'e-mail
                                Transport.send(message);
                            } catch (MessagingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    emailThread.start();

                    response.getWriter().println(
                            "Ihr Konto wurde erfolgreich erstellt. Eine Bestätigungs-E-Mail wird an Ihre E-Mail-Adresse gesendet.");

                } else {
                    response.sendRedirect("register.html?error=InsertionError");
                }

            }
        } catch (SQLException e) {

            System.out.println("Fehler");

            e.printStackTrace();
        }
    }
}


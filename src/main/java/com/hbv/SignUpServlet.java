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

        if (username != null && username.isEmpty()) {
            response.getWriter().println("<h1>Le nom d'utilisateur est vide</h1>");
            return;
        } else if (firstName != null && firstName.isEmpty()) {

            response.getWriter().println("<h1>Veuillez entrez votre prenom.</h1>");

            return;
        } else if (lastName != null && lastName.isEmpty()) {

            response.getWriter().println("<h1>Veuillez entrez votre nom</h1>");

            return;
        } else if (password != null && password.isEmpty()) {

            response.getWriter().println("<h1><Veuillez entrez un mot de passe</h1>");

            return;
        } else if (email != null && email.isEmpty()) {

            response.getWriter().println("<h1>Veuillez entrer une adresse mail<h1>");

            return;
        }
        boolean usernameExists = false;
        boolean emailExists = false;
        // Chargement du Driver JDBC

        // Creation de la connexion a la base de donnees
        // Verification si le mom d'utilisateur n'est pas encore pri

        String sql1 = "SELECT * FROM usersApp WHERE username=?";
        String sql2 = "SELECT * FROM usersApp WHERE email=?";

        try {

            Connection connection = MyConnectionPool.borrowConnection();
            PreparedStatement checkUsername = connection.prepareStatement(sql1);
            PreparedStatement checkEmail = connection.prepareStatement(sql2);

            checkUsername.setString(1, username);
            ResultSet usernameRs = checkUsername.executeQuery();
            if (usernameRs.next()) {
                usernameExists = true;
                response.sendRedirect("register.html?error=usernameExists");
            }

            checkEmail.setString(1, email);
            ResultSet emailRs = checkEmail.executeQuery();
            if (emailRs.next()) {
                emailExists = true;
                response.sendRedirect("register.html?error=emailExists");
            }
            // Insertion dans la base de Donnees
            if (!usernameExists && !emailExists) {

                String sql = "INSERT INTO usersApp (username, first_name, last_name, password, email, city, postal_code) VALUES (?,?,?,?,?,?,?)";
                PreparedStatement prepareStatement = connection.prepareStatement(sql);

                prepareStatement.setString(1, username);
                prepareStatement.setString(2, firstName);
                prepareStatement.setString(3, lastName);
                prepareStatement.setString(4, password);
                prepareStatement.setString(5, email);
                prepareStatement.setString(6, city);
                prepareStatement.setString(7, postalCode);

                //check if the user has been created and  call the email verification/sender class
                int ex = prepareStatement.executeUpdate();
                if (ex > 0) {
                    String subject = "Einrichtung des Kontos für die Buchung von Terminen für die Covid-19-Impfung.";

                    String body = "Hallo Frau/Herr" + lastName + "," + "<br> Ihr KOnto wurde erfolgreich erstellt. Klicken Sie "
                            + "auf den folgenden Link um Ihnen einloggen zu können: " + " http://localhost:8084/App/signIn.html";
                    Runnable emailSenderServlet = new EmailSenderServlet(email, subject, body);
                    Thread runner = new Thread(emailSenderServlet);
                    runner.start();
                    response.getWriter().println("Ihr Konto wurde erfolgreich erstellt. Eine Bestätigungs-E-Mail wird an Ihre E-Mail-Adresse gesendet.");

                } else {
                    response.sendRedirect("register.html?error=InsertionError");
                }
            }
        } catch (SQLException e) {
            System.out.println("Insertion error");
            e.printStackTrace();
        }
    }
}


package com.hbv;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignInServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        Thread threadLoginCheck = new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    if ((username != null) && username.isEmpty() && (password != null) && password.isEmpty()) {
                        response.sendRedirect("login.html?error=missingFields");
                    } else if (IsValidUser(username, password, request)) {
                        response.sendRedirect("central.html?username=" + username);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        threadLoginCheck.start();
    }

    private boolean IsValidUser(String username, String password, HttpServletRequest request) {

        boolean isValid = false;
        try {
            Connection connection = MyConnectionPool.borrowConnection();
            ResultSet rs;
            PreparedStatement ps = connection.prepareStatement("select * from user where username=? and password=?");
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();
            if (rs.next()) {
                isValid = true;

                HttpSession session = request.getSession();
                session.setAttribute("username", username);
                session.setAttribute("firstName", rs.getString("first_name"));
                session.setAttribute("lastName", rs.getString("last_name"));
                session.setAttribute("email", rs.getString("email"));
                session.setAttribute("city", rs.getString("city"));
                session.setAttribute("postalCode", rs.getString("postal_code"));

            }
            rs.close();
            ps.close();
            MyConnectionPool.releaseConnection(connection);
        } catch (SQLException e) {
            System.out.println("User not Found");
            e.printStackTrace();
        }
        return isValid;
    }

}

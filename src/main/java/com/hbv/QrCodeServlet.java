package com.hbv;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class QrCodeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // get the Session Attributes
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        String firstName = (String) session.getAttribute("firstName");
        String lastName = (String) session.getAttribute("lastName");
        String email = (String) session.getAttribute("email");
        String city = (String) session.getAttribute("city");
        String postalCode = (String) session.getAttribute("postalCode");
        String date1 = (String) session.getAttribute("date1");
        String heure1 = (String) session.getAttribute("heure1");
        String date2 = (String) session.getAttribute("date2");
        String heure2 = (String) session.getAttribute("heure2");

        // Qr-code content init
        String data = "Username: " + username + "\n" +
                "Vorname: " + firstName + "\n" +
                "Nachname: " + lastName + "\n" +
                "Email: " + email + "\n" +
                "Stadt: " + city + "\n" +
                "Posleitzahl: " + postalCode + "\n" +
                "Datum 1. Termin: " + date1 + "\n" +
                "Uhrzeit 1. Termin: " + heure1 + "\n" +
                "Datum 2. Termin: " + date2 + "\n" +
                "Uhrzeit 2. Termin: " + heure2 + "\n";


        // set the size of the output
        //String data = "Hallo je m'appele jordan";
        //File path = new File("./img.png");
        int width = 400;
        int height = 400;


        // create and display the qrcode using Bitmatrix
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, width, height);
            response.setContentType("image/png");
            MatrixToImageWriter.writeToStream(matrix, "png", response.getOutputStream());

        } catch (WriterException | IOException e) {
            System.err.println("Fail to generate the qr image : " + e.getMessage());
        }
    }
}

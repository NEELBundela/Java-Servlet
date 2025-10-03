package com.codewithz;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.sql.*;

@WebServlet("/StudentRegisterServlet")
public class StudentRegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Get form values
        int stu_id = Integer.parseInt(request.getParameter("stu_id"));
        String name = request.getParameter("name");
        String course = request.getParameter("courses");

        try {
            // Load JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to database
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/java", "root", "neel@18");

            // Insert query
            String query = "INSERT INTO student(stu_id, name, course) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, stu_id);
            ps.setString(2, name);
            ps.setString(3, course);

            int i = ps.executeUpdate();
            if (i > 0) {
                out.println("<h3>Student registered successfully!</h3>");
            } else {
                out.println("<h3>Registration failed. Try again.</h3>");
            }

            ps.close();
            con.close();
        } catch (SQLIntegrityConstraintViolationException e) {
            out.println("<h3 style='color:red;'>Error: Student ID already exists!</h3>");
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }
}

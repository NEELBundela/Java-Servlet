package com.codewithz;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/cal-servlet")
public class calservlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public calservlet() {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");


        String num1Str = request.getParameter("txt1");
        String num2Str = request.getParameter("txt2");
        String op = request.getParameter("op");

        try {
            int a = Integer.parseInt(num1Str);
            int b = Integer.parseInt(num2Str);
            int res = 0;
            String ops ="";

            if("add".equals(op))
            {
                res = a+b;
                ops = "+";
            } else if ("sub".equals(op)) {
                res = a-b;
                ops = "-";
            }else if ("mul".equals(op)) {
                res = a*b;
                ops = "*";
            }else if("div".equals(op)) {
                if(b==0)
                {
                    response.getWriter().println("<h3> cannot divide by zero");
                    return;
                }else {
                    res = a/b;
                    ops = "/";
                }
            }

            response.getWriter().println("<h1>Calculation....");
            response.getWriter().println("<p>first number is : "+a+ "</p>");
            response.getWriter().println("<p>second number is : "+b+ "</p>");
            response.getWriter().println("<p>Opration symbol is : "+ops+ "</p>");
            response.getWriter().println("<h2>" + a + " " + ops + " " + b + " = " + res + "</h2>");

        } catch (NumberFormatException e) {
            response.getWriter().println("<h1> Invalid Input. Please Enter valid input");
        }
    }
}

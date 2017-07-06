/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.demo.demoapp;

import app.demo.demoapp.model.User;
import app.demo.demoapp.model.UserDao;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author igogo
 */
@WebServlet(name = "IdBinding", urlPatterns = {"/idBinding"})
public class IdBinding extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(IdBinding.class);

    UserDao userDao;
    User user;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws java.sql.SQLException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        HttpSession session = request.getSession();
        if (session.getAttribute("sub") == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            request.setAttribute("msg", "Please login first");
            RequestDispatcher dispatcher = request
                    .getRequestDispatcher("forbidden.jsp");
            dispatcher.forward(request, response);
        }

        String sub = session.getAttribute("sub").toString();
        String cwd = getServletContext().getRealPath("/");
        userDao = new UserDao(cwd);
        Boolean isBinding = userDao.isBindingByID(sub);
//             利用sub值進行資料庫比對,分三种情況
//             1.無帳號,新申請
//             2. 有帳號尚未綁定
//             3.有帳號且已綁定

        if (isBinding) {
            logger.info("已綁定帳號, 導向");
            User user = userDao.findByMappindID(sub);
            logger.info(user.getUsername());
            session.setAttribute("username", user.getUsername());
            response.sendRedirect("userhome");
        } else {
            logger.info("未綁定,進行綁定");
            RequestDispatcher dispatcher = request
                    .getRequestDispatcher("idBinding.jsp");
            dispatcher.forward(request, response);
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(IdBinding.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(IdBinding.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

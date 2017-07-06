/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import app.demo.demoapp.model.User;
import app.demo.demoapp.model.UserDao;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Base64;
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

@WebServlet(name = "Users", urlPatterns = {"/api/users/*"})
public class Users extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(Users.class);
    String username;
    String password = null;
    String sub = null;
    User user;
    ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String cwd = getServletContext().getRealPath("/");
        UserDao userDao = new UserDao(cwd);

        HttpSession session = request.getSession();
        sub = session.getAttribute("sub").toString();
        if (sub == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            request.setAttribute("msg", "Please login moe oidc service first");
            RequestDispatcher dispatcher = request
                    .getRequestDispatcher("forbidden.jsp");
            dispatcher.forward(request, response);
        } else {

            String pathInfo = request.getPathInfo();
            String[] path = pathInfo.split("/");
            logger.info(String.format("%d", path.length));

            //取得username,password
            if (path.length == 2) {
                username = path[1];
                StringBuffer jb = new StringBuffer();
                String line = null;
                try {
                    BufferedReader reader = request.getReader();
                    while ((line = reader.readLine()) != null) {
                        jb.append(line);
                    }
                } catch (Exception e) {
                    logger.info(e.getMessage());
                }

                JsonNode root = mapper.readTree(jb.toString());
//                logger.info("act get parameters from json:" + root.get("act").asText());

                password = root.get("password").asText();

            } else {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                request.setAttribute("msg", "request wrong path");
                RequestDispatcher dispatcher = request
                        .getRequestDispatcher("forbidden.jsp");
                dispatcher.forward(request, response);
            }

            Boolean isValid = false;
            try {
                isValid = userDao.userAuth(username, password);
            } catch (SQLException ex) {
                logger.info(ex.getMessage());
            }
            Writer writer = new StringWriter();
            try (JsonGenerator g = mapper.getFactory().createGenerator(writer)) {
                g.writeStartObject();

                if (isValid) {

                    try {
                        //更新資料庫
                        if (userDao.updateMappingID(username, sub)) {
                            g.writeStringField("result", "ok");
                            session.setAttribute("username", username);
                        }
                    } catch (SQLException ex) {
                        logger.info(ex.getMessage());
                    }

                } else {
                    String msg = "帳密不對";
                    g.writeStringField("result", URLEncoder.encode(msg, "UTF-8"));
                }
                g.writeEndObject();
            }

            response.setContentType("application/json");
            try (PrintWriter out = response.getWriter()) {
                out.println(writer.toString());
            }

        }

    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("delete request");
        HttpSession session = request.getSession();
        username = session.getAttribute("username").toString();
        String cwd = getServletContext().getRealPath("/");
        UserDao userDao = new UserDao(cwd);
        Boolean isUpdated = false;

        try {
            isUpdated = userDao.updateMappingID(username, "");
        } catch (SQLException ex) {
            logger.info(ex.getMessage());
        }

        if (isUpdated) {
            try {
                user = userDao.findByUsername(username);
            } catch (SQLException ex) {
                logger.info(ex.getMessage());
            }
            response.setContentType("application/json; charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.println(mapper.writeValueAsString(user));
            }

        }

    }

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
        logger.info("doget page");
        String cwd = getServletContext().getRealPath("/");
        UserDao userDao = new UserDao(cwd);

        HttpSession session = request.getSession();
        if (session.getAttribute("username") == null) {
            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                /* TODO output your page here. You may use following sample code. */
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Servlet Users</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Servlet Users at " + request.getContextPath() + "</h1>");
                out.println("</body>");
                out.println("</html>");
            }
        } else {
            user = userDao.findByUsername(session.getAttribute("username").toString());
//            response.setContentType("application/json");
            response.setContentType("application/json; charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.println(mapper.writeValueAsString(user));
            }
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
            logger.info(ex.getMessage());
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
            logger.info(ex.getMessage());
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DAO.PatientDAO;
import DAO.UserDAO;
import Model.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author chinh
 */
public class LoginController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet LoginController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet LoginController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
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
        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            request.getSession().invalidate();
            request.setAttribute("title", "Đăng nhập");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        } else {
            response.sendRedirect(".");
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
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        HttpSession session = request.getSession();

        UserDAO userDAO = new UserDAO();

        User user = userDAO.login(new User(username, password));
       // user.setUserId(userDAO.getIdFromUsername(username));
        if (user == null || !user.isActiveStatus()) {
            if (user == null) {
                request.setAttribute("message", "Sai thông tin đăng nhập");
            } else if (!user.isActiveStatus()) {
                session.setAttribute("message",
                        "Tài khoản của bạn đang tạm khóa."
                        + "<br>Vui lòng liên hệ quản trị viên.");
            }
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        } else {
            session.setAttribute("user", user);
            if (user.getUserType() == 3) {
                PatientDAO patientDAO = new PatientDAO();
                if (patientDAO.isFirstTimeLogin(user.getUserId())) {
                    session.setAttribute("message",
                            "Đây là lần đầu bạn đăng nhập.<br>"
                            + "Vui lòng cập nhật thông tin cá nhân");
                    response.sendRedirect("info?action=update");
                } else {
                    response.sendRedirect(".");
                }
            } else {
                response.sendRedirect(".");
            }
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

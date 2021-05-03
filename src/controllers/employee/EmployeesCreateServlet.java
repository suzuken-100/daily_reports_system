package controllers.employee;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import models.validators.EmployeeValidators;
import utils.DBUtil;
import utils.EncryptUtil;

/**
 * Servlet implementation class EmployeesCreateServlet
 */
@WebServlet("/employees/create")
public class EmployeesCreateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeesCreateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        String _token=request.getParameter("_token");
        if(_token != null && _token.equals(request.getSession().getId())){
            EntityManager em=DBUtil.createEntityManager();

            Employee e=new Employee();

            e.setCode(request.getParameter("code"));
            e.setName(request.getParameter("name"));
            e.setPassword(
                    EncryptUtil.getPasswordEncrypt("password", (String)this.getServletContext().getAttribute("pasword"))
                    );
            e.setAdmin_flag(Integer.parseInt(request.getParameter("admin_flag")));

            Timestamp currenTime = new Timestamp(System.currentTimeMillis());
            e.setCreated_at(currenTime);
            e.setUpdate_at(currenTime);
            e.setDelete_flag(0);

            List<String> errors = EmployeeValidators.validate(e, true, true);
            if(errors.size() > 0) {
                em.close();

                request.setAttribute("_token", request.getSession().getId());
                request.setAttribute("employee", e);
                request.setAttribute("errors", errors);

                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/employees/new.jsp");
                rd.forward(request, response);
            } else {
                em.getTransaction().begin();
                em.persist(e);
                em.getTransaction().commit();
                request.getSession().setAttribute("flush", "登録が完了しました。");
                em.close();

                response.sendRedirect(request.getContextPath() + "/employees/index");
        }
    }

}
    }

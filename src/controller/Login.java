package controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.google.gson.Gson;

import controller.Master;
import model.User;


@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		response.setContentType("text/json");
		User user = null;
		if(Master.getSessionFactory()==null){
			Master master = new Master();
			master.contextInitialized();
		}
		Session dbSession = Master.getSessionFactory().getCurrentSession();
		Transaction transaction = dbSession.beginTransaction();
		try {
			String hql = "FROM users u where u.username='" + username + "'";
			org.hibernate.query.Query<User> query = dbSession.createQuery(hql, User.class);
			List<User> results = query.list();
			if (results.size() > 0) {
				user = results.get(0);
				dbSession.save(user);
			}
			HttpSession session = request.getSession(true);
			session.setAttribute("user", user);
//			sendsuccess
			String son = "[username = "+user.getUsername()+", userID= "+user.getUserID()+", password = "+user.getPassword()+"]";
			Gson g = new Gson();
			response.getWriter().write( g.toJson(son));
//			request.getRequestDispatcher("displaysites?userID="+user.getUserID()).forward(request, response);
			DisplaySites ds = new DisplaySites();
			ds.doGet(request, response, dbSession, transaction,  user);
//			request.getRequestDispatcher("main.html?userID=" + user.getUserID()).forward(request, response);
			transaction.commit();
			response.sendRedirect("main.html?userID=" + user.getUserID());
		} catch (Exception ex) {
			transaction.rollback();
			throw new RuntimeException(ex);
		}
		if (user == null || !user.getPassword().equals(password)) {
			handleInvalidLogin(request, response);
			return;
		}

	}
//sendsucceess in gsont-to-json
	private void handleInvalidLogin(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setAttribute("error", "Invalid username or password");
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}

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

import model.User;

@WebServlet("/main")
public class Main extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userID = request.getParameter("userID");
		int id = Integer.parseInt(userID);
		response.setContentType("text/json");
		User user = null;
		if(Master.getSessionFactory()==null){
			Master master = new Master();
			master.contextInitialized();
		}
		Session dbSession = Master.getSessionFactory().getCurrentSession();
		Transaction transaction = dbSession.beginTransaction();
		try {
			String hql = "FROM users u where u.user_id='" + id + "'";
			org.hibernate.query.Query<User> query = dbSession.createQuery(hql, User.class);
			List<User> results = query.list();
			if (results.size() > 0) {
				user = results.get(0);
				dbSession.save(user);
			}
			HttpSession session = request.getSession(true);
			session.setAttribute("user", user);
			String son = "[username = "+user.getUsername()+", userID= "+user.getUserID()+", password = "+user.getPassword()+"]";
			Gson g = new Gson();
			response.getWriter().write( g.toJson(son));
			DisplaySites ds = new DisplaySites();
			ds.doGet(request, response, dbSession, transaction,  user);
			transaction.commit();
//			response.sendRedirect("main.html?userID=" + user.getUserID());
		} catch(Exception ex) {
			transaction.rollback();
			throw new RuntimeException(ex);	
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

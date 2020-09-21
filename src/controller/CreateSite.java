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

import model.Site;
import model.User;



@WebServlet("/CreateSiteManager")
public class CreateSite extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String siteName = request.getParameter("sitename");
		HttpSession session = request.getSession(true);
		String userID = request.getParameter("userID");
		int id = Integer.parseInt(userID);
		User user = null;
		response.setContentType("text/json");
		if (Master.getSessionFactory() == null) {
			Master master = new Master();
			master.contextInitialized();
		}
		Session dbSession = Master.getSessionFactory().getCurrentSession();
		Transaction transaction = dbSession.beginTransaction();
		try {
			String hql = "FROM users u where u.userID ='" + id + "'";
			org.hibernate.query.Query<User> query = dbSession.createQuery(hql, User.class);
			List<User> results = query.list();
			if (results.size() > 0) {
				user = results.get(0);
				dbSession.save(user);
			}
		if (user == null) {
			handleMustLogin(session, request, response, id);
			return;
		}
		Site site = new Site();
		site.setSitename(siteName);
		site.setOwner(user);
		user.getSites().add(site);
		dbSession.save(site);
		request.setAttribute("site", site);
		transaction.commit();
//		response.sendRedirect("createsite.html?sitename="+siteName+"&userID=" + user.getUserID());
		} catch (Exception ex) {
			transaction.rollback();
			throw new RuntimeException(ex);
		}
	}

	private void handleMustLogin(HttpSession session, HttpServletRequest request, HttpServletResponse response, int id)
			throws ServletException, IOException {
		session.setAttribute("error", "You are not logged in.  Please login first");
//		request.getRequestDispatcher("main.html?userID=" + id).forward(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

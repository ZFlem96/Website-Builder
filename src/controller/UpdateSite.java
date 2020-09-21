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



@WebServlet("/UpdateSiteManager")
public class UpdateSite extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String currentsiteName = request.getParameter("currentsitename"), newsitename = request.getParameter("newsitename");
		String userID = request.getParameter("userID");
		int id = Integer.parseInt(userID);
		HttpSession session = request.getSession(true);
		if (session == null) {
			handleMustLogin(session, request, response, id);
			return;
		}
		response.setContentType("text/json");
		if (Master.getSessionFactory() == null) {
			Master master = new Master();
			master.contextInitialized();
		}
		Session dbSession = Master.getSessionFactory().getCurrentSession();
		Transaction transaction = dbSession.beginTransaction();
		User user = null;
		String hql = "FROM users u where u.userID='" + id + "'";
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
		int foundID = 0;
		boolean found = false;
		for(int x = 0;x<user.getSites().size();x++){
			if(user.getSites().get(x).getSitename().equals(currentsiteName)){
				found = true;
				foundID = x;
			}
		}
		if(found){
			try {
				user.getSites().get(foundID).setSitename(newsitename);
				Site site = user.getSites().get(foundID);
				dbSession.update(site);
				dbSession.save(site);
				transaction.commit();
//				response.sendRedirect("updatesite.html?sitename="+newsitename+"&userID=" + user.getUserID());
			} catch (Exception ex) {
				transaction.rollback();
				throw new RuntimeException(ex);
			}
		}
	}

	private void handleMustLogin(HttpSession session, HttpServletRequest request, HttpServletResponse response, int id)
			throws ServletException, IOException {
		session.setAttribute("error", "You are not logged in.  Please login first");
		request.getRequestDispatcher("main.html?userID=" + id).forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

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


@WebServlet("/DeleteSiteManager")
public class DeleteSite extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String listname = request.getParameter("sitename");
		String userID = request.getParameter("userID");
		int id = Integer.parseInt(userID);
		User user = null;
		if (Master.getSessionFactory() == null) {
			Master master = new Master();
			master.contextInitialized();
		}
		Session dbSession = Master.getSessionFactory().getCurrentSession();
		Transaction transaction = dbSession.beginTransaction();
		response.setContentType("text/json");
		
		try {
			String hql = "FROM users u where u.userID='" + id + "'";
			org.hibernate.query.Query<User> query = dbSession.createQuery(hql, User.class);
			List<User> results = query.list();
			if (results.size() > 0) {
				user = results.get(0);
				dbSession.save(user);
			}
			int foundID = 0;
			boolean found = false;
			for(int x = 0;x<user.getSites().size();x++){
				if(user.getSites().get(x).getSitename().equals(listname)){
					found = true;
					foundID = x;
				}
			}
			if(found){
				dbSession.delete(user.getSites().get(foundID));
				user.getSites().remove(foundID);
				String son = "[username = "+user.getUsername()+", userID= "+user.getUserID()+", password = "+user.getPassword()+"]";
				Gson g = new Gson();
				response.getWriter().write( g.toJson(son));
				transaction.commit();
//				response.sendRedirect("main.html?userID=" + user.getUserID());
			}
		} catch(Exception ex) {
			transaction.rollback();
			throw new RuntimeException(ex);	
		}
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

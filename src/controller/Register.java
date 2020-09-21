package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.google.gson.Gson;

import controller.Master;
import model.User;

@WebServlet("/registeruser")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static int nextUserID = 1;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String retypePassword = request.getParameter("retype_password");
		response.setContentType("text/json");
		if (password == null || retypePassword == null || username == null) {
			throw new IllegalArgumentException("username, password, and retype password are all required");
		}
		if (!password.equals(retypePassword)) {
			throw new IllegalArgumentException("password and retype password do not match");
		}
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setUserID(nextUserID++); 
		if(Master.getSessionFactory()==null){
			Master master = new Master();
			master.contextInitialized();
		}
		Session dbSession = Master.getSessionFactory().getCurrentSession();
		Transaction transaction = dbSession.beginTransaction();
//		HttpSession session = request.getSession(true);
		try {
			dbSession.save(user);
			String son = "[username = "+user.getUsername()+", userID= "+user.getUserID()+", password = "+user.getPassword()+"]";
			Gson g = new Gson();
			response.getWriter().write( g.toJson(son));
//			request.getRequestDispatcher("index.html").forward(request, response);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			throw new RuntimeException(ex);
		}
}

protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
	doGet(request, response);
}
}

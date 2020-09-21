package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.google.gson.Gson;

import model.Site;
import model.User;

//@WebServlet("/displaysites")
public class DisplaySites extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response, Session dbSession,
			Transaction transaction, User user) throws ServletException, IOException {
		try {
			response.setContentType("text/json");
			if (Master.getSessionFactory() == null) {
				Master master = new Master();
				master.contextInitialized();
			}
			List<Site> usersites = user.getSites();
			if (usersites == null) {
				usersites = new ArrayList<Site>();
			}
			String li = "";
			if (usersites.size() > 0) {
				for (int x = 0; x < usersites.size(); x++) {
					li += "<li>" + usersites.get(x).getSitename() + " <li>";
				}
			} else if (usersites.size() == 0) {
				li += "<h2>You haven't made any sites yet.<h2>";
			}
			response.getWriter().write(li);
			String son = "[user: " + user.getUsername() + " show sites]";
			Gson g = new Gson();
			response.getWriter().write(g.toJson(son));
		} catch (Exception ex) {
			transaction.rollback();
			throw new RuntimeException(ex);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response, Session dbSession,
			Transaction transaction, User user) throws ServletException, IOException {
		doGet(request, response, dbSession, transaction, user);
	}

}

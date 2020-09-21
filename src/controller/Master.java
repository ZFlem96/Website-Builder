package controller;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

@WebServlet("/Master")
public class Master extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static SessionFactory sessionfactory;

	public static SessionFactory getSessionFactory() {
		return sessionfactory;
	}

	// @Override
	public void contextInitialized() {
		StandardServiceRegistry sr = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
		Metadata metadata = new MetadataSources(sr).getMetadataBuilder().build();
		sessionfactory = metadata.getSessionFactoryBuilder().build();
	}

	public static void shutdown() {
		getSessionFactory().close();
	}

//	private static SessionFactory createSessionFactory() {
//
//	    Configuration configuration = new Configuration();
//	    configuration.configure("hibernate.cfg.xml");
//	    ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
//	            configuration.getProperties()).build();
//	    sessionfactory = configuration.buildSessionFactory(serviceRegistry);
//	    return sessionfactory;
//	}
	
}

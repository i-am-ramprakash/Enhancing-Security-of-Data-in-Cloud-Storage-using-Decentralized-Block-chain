package com.security;

import com.dao.DBConnection;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppLifecycle implements ServletContextListener {
    @Override public void contextInitialized(ServletContextEvent event) {
        FileCrypto.masterKeyFromEnvironment();
        FileCrypto.auditKeyFromEnvironment();
    }
    @Override public void contextDestroyed(ServletContextEvent event) {
        DBConnection.close();
    }
}

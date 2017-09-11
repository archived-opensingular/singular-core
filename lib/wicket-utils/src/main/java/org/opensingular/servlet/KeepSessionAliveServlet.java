package org.opensingular.servlet;

import org.apache.wicket.Session;
import org.opensingular.lib.commons.util.Loggable;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(KeepSessionAliveServlet.ENDPOINT)
public class KeepSessionAliveServlet extends HttpServlet implements Loggable {

    public static final String ENDPOINT = "/KeepSessionAlive";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(Session.exists()){
            getLogger().info(String.format("Keeping session with id %s alive.", Session.get().getId()));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(Session.exists()){
            getLogger().info(String.format("Keeping session with id %s alive.", Session.get().getId()));
        }
    }
}

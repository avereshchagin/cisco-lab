package com.github.avereshchagin.ciscolab.rest;

import com.github.avereshchagin.ciscolab.util.CommPortUtils;
import com.github.avereshchagin.ciscolab.util.PortDispatcher;
import com.google.gson.Gson;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/")
public class Rack extends Thread {

    private static final Logger LOGGER = Logger.getLogger(Rack.class.getName());

    @GET
    @Path("availablePorts")
    @Produces(MediaType.APPLICATION_JSON)
    public String availablePorts() {
        List<String> ports = CommPortUtils.listAvailablePorts();
        Gson gson = new Gson();
        return gson.toJson(ports);
    }

    @GET
    @Path("closePort")
    @Produces("text/plain")
    public String closePort(@QueryParam("name") String name) {
        if (name != null) {
            PortDispatcher.INSTANCE.closePort(name);
        }
        return "";
    }

    @Override
    public void run() {
        try {
            HttpServer server = HttpServerFactory.create("http://localhost:9009/");
            server.start();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "run", e);
        }
    }
}
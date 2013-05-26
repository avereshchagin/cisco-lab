package com.github.avereshchagin.ciscolab.webapp.rest;

import com.github.avereshchagin.ciscolab.webapp.jpa.User;
import com.github.avereshchagin.ciscolab.webapp.jpa.UserType;
import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("menu")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MenuResource {

    private static final Gson GSON = new Gson();

    @GET
    public String getMenu(@Context HttpServletRequest request) {
        Object obj = request.getSession().getAttribute("user");
        List<String[]> items = new ArrayList<>();
        items.add(new String[]{"Main Page", "index.xhtml"});
        if (obj instanceof User) {
            User user = (User) obj;
            if (user.getType().equals(UserType.ADMINISTRATOR)) {
                items.add(new String[]{"Topology", "topology.xhtml"});
                items.add(new String[]{"Settings", "settings.xhtml"});
            }
        }
        return GSON.toJson(items);
    }
}

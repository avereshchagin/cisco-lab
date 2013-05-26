package com.github.avereshchagin.ciscolab.webapp.rest;

import com.github.avereshchagin.ciscolab.webapp.jpa.Administrator;
import com.github.avereshchagin.ciscolab.webapp.jpa.User;
import com.google.gson.Gson;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/user")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private static final Gson GSON = new Gson();

    @PersistenceContext(unitName = "IBTSRemoteLabPU")
    private EntityManager em;

    @GET
    @Path("me")
    public String me(@Context HttpServletRequest request) {
        Object obj = request.getSession().getAttribute("user");
        if (obj instanceof User) {
            User user = (User) obj;
            return "{\"displayName\": \"" + user.getDisplayName() + "\", \"type\": \"" +
                    user.getClass().getSimpleName() + "\"}";
        }
        return GSON.toJson(null);
    }

    public void addUser(User user) {
        em.persist(user);
    }

    @GET
    @Path("auth")
    public Response verifyUser(@QueryParam("login") String login,
                               @QueryParam("password") String password,
                               @Context HttpServletRequest request) {
        User user = null;
        try {
            TypedQuery<User> query = em.createNamedQuery("User.verify", User.class);
            query.setParameter("login", login);
            query.setParameter("password", password);
            user = query.getSingleResult();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        request.getSession().setAttribute("user", user);
        return Response.ok().entity(
                GSON.toJson(user != null ? "ok" : "fail")
        ).build();
    }

    @POST
    @Path("out")
    public void out(@Context HttpServletRequest request) {
        request.getSession().setAttribute("user", null);
    }

    @GET
    @Path("_admin")
    public Response initAdmin() {
        TypedQuery<User> query = em.createNamedQuery("User.verify", User.class);
        query.setParameter("login", "admin");
        query.setParameter("password", "12345");
        List<User> users = query.getResultList();
        if (users.isEmpty()) {
            Administrator admin = new Administrator();
            admin.setLogin("admin");
            admin.setDisplayName("Administrator");
            admin.setPassword("12345");
            em.persist(admin);
        }
        return Response.noContent().build();
    }
}

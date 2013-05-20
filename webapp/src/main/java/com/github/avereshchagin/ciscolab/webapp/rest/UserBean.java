package com.github.avereshchagin.ciscolab.webapp.rest;

import com.github.avereshchagin.ciscolab.webapp.jpa.User;
import com.google.gson.Gson;

import javax.ejb.Stateless;
import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/user")
@Stateless
public class UserBean {

    private static final Gson GSON = new Gson();

    @PersistenceContext(unitName = "IBTSRemoteLabPU")
    private EntityManager em;

    @GET
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    public String me(@Context HttpServletRequest request) {
        Object obj = request.getSession().getAttribute("user");
        if (obj instanceof User) {
            User user = (User) obj;
            return GSON.toJson(user);
        }
        return GSON.toJson(null);
    }

    public void addUser(User user) {
        em.persist(user);
    }

    @GET
    @Path("auth")
    @Produces(MediaType.APPLICATION_JSON)
    public String verifyUser(@QueryParam("login") String login,
                             @QueryParam("password") String password,
                             @Context HttpServletRequest request) {
        TypedQuery<User> query = em.createNamedQuery("verifyUser", User.class);
        query.setParameter("login", login);
        query.setParameter("password", password);
        User result = null;
        try {
            result = query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            e.printStackTrace();
        }
        request.getSession().setAttribute("user", result);
        return GSON.toJson(result != null ? "ok" : "fail");
    }

    @POST
    @Path("out")
    public void out(@Context HttpServletRequest request) {
        request.getSession().setAttribute("user", null);
    }
}

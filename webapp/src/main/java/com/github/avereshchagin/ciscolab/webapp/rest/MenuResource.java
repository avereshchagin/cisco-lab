package com.github.avereshchagin.ciscolab.webapp.rest;

import com.github.avereshchagin.ciscolab.webapp.jpa.Administrator;
import com.github.avereshchagin.ciscolab.webapp.jpa.Lab;
import com.github.avereshchagin.ciscolab.webapp.jpa.Student;
import com.github.avereshchagin.ciscolab.webapp.jpa.Trainer;
import com.google.gson.Gson;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
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
@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MenuResource {

    private static final Gson GSON = new Gson();

    @PersistenceContext(unitName = "IBTSRemoteLabPU")
    private EntityManager em;

    @GET
    public String getMenu(@Context HttpServletRequest request) {
        Object obj = request.getSession().getAttribute("user");
        List<String[]> items = new ArrayList<>();
        items.add(new String[]{"Main Page", "index.xhtml"});
        if (obj instanceof Administrator) {
            TypedQuery<Lab> query = em.createNamedQuery("Lab.findAll", Lab.class);
            List<Lab> labs = query.getResultList();
            for (Lab lab : labs) {
                items.add(new String[]{lab.getName(), ""});
                items.add(new String[]{"Topology", String.format("topology.xhtml?id=%d", lab.getId())});
                items.add(new String[]{"Edit", String.format("editLab.xhtml?id=%d", lab.getId())});
            }
            items.add(new String[]{"Create Lab", "createLab.xhtml"});
            items.add(new String[]{"Settings", "settings.xhtml"});
        } else if (obj instanceof Trainer) {
            Trainer trainer = (Trainer) obj;
            Lab lab = trainer.getLab();
            items.add(new String[]{lab.getName(), ""});
            items.add(new String[]{"Topology", String.format("topology.xhtml?id=%d", lab.getId())});
            items.add(new String[]{"Device Management", "#"});
        } else if (obj instanceof Student) {
            Student student = (Student) obj;
            Lab lab = student.getLab();
            items.add(new String[]{lab.getName(), ""});
            items.add(new String[]{"Topology", String.format("topology.xhtml?id=%d", lab.getId())});
        }
        return GSON.toJson(items);
    }
}

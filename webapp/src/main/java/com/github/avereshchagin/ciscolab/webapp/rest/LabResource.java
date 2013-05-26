package com.github.avereshchagin.ciscolab.webapp.rest;

import com.github.avereshchagin.ciscolab.webapp.jpa.Lab;
import com.github.avereshchagin.ciscolab.webapp.jpa.Student;
import com.github.avereshchagin.ciscolab.webapp.jpa.Trainer;
import com.github.avereshchagin.ciscolab.webapp.jpa.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.api.Responses;
import org.apache.commons.lang3.RandomStringUtils;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@Path("/lab")
@Stateless
public class LabResource {

    private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();

    @PersistenceContext(unitName = "IBTSRemoteLabPU")
    private EntityManager em;

    private static <T extends User> T generateUser(T user) {
        String login = RandomStringUtils.randomAlphabetic(6);
        user.setLogin(login);
        user.setDisplayName(login);
        user.setPassword(RandomStringUtils.randomAlphanumeric(8));
        return user;
    }

    @POST
    public Response createLab(@FormParam("name") String name,
                              @FormParam("capacity") Integer capacity,
                              @FormParam("layout") String layout) {
        try {
            Lab lab = new Lab();
            lab.setName(name);
            lab.setTopology(layout);

            Trainer trainer = generateUser(new Trainer());
            trainer.setLab(lab);
            lab.setTrainer(trainer);

            List<Student> students = new ArrayList<>();
            for (int i = 0; i < capacity; i++) {
                Student student = generateUser(new Student());
                student.setLab(lab);
                students.add(student);
            }
            lab.setStudents(students);

            GregorianCalendar calendar = new GregorianCalendar();
            lab.setStartTime(calendar.getTime());
            calendar.add(Calendar.DATE, 7);
            lab.setEndTime(calendar.getTime());

            em.persist(lab);
            em.persist(trainer);
            for (Student student : students) {
                em.persist(student);
            }

            return Response.ok().build();
        } catch (RuntimeException e) {
            return Response.serverError().entity(e.toString()).build();
        }
    }

    @GET
    @Path("{labId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLab(@PathParam("labId") Long labId) {
        try {
            Lab lab = em.find(Lab.class, labId);
            if (lab != null) {
                return Response.ok().entity(
                        GSON.toJson(lab)
                ).build();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return Responses.notFound().build();
    }

    @GET
    @Path("students/{labId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStudents(@PathParam("labId") Long labId) {
        try {
            Lab lab = em.find(Lab.class, labId);
            if (lab != null) {
                return Response.ok().entity(
                        GSON.toJson(lab.getStudents())
                ).build();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return Responses.notFound().build();
    }

    @GET
    @Path("layout/{labId}")
    @Produces(MediaType.APPLICATION_XML)
    public String getLayout(@PathParam("labId") Long labId) {
        Lab lab = em.find(Lab.class, labId);
        if (lab != null) {
            return lab.getTopology();
        }
        return "";
    }

    @GET
    @Path("topology/{labId}")
    @Produces("image/svg+xml")
    public Response getTopology(@PathParam("labId") Long labId) {
        try {
            Lab lab = em.find(Lab.class, labId);
            if (lab != null) {
                Source xslt = new StreamSource(getClass().getResourceAsStream("transform.xsl"));
                Source xml = new StreamSource(new StringReader(lab.getTopology()));
                Transformer transformer = TRANSFORMER_FACTORY.newTransformer(xslt);
                StringWriter svg = new StringWriter();
                transformer.transform(xml, new StreamResult(svg));
                return Response.ok().entity(svg.toString()).build();
            }
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return Responses.noContent().build();
    }
}

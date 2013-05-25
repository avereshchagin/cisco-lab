package com.github.avereshchagin.ciscolab.webapp.rest;

import com.github.avereshchagin.ciscolab.webapp.jpa.Device;
import com.github.avereshchagin.ciscolab.webapp.jpa.Rack;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

@Path("/rack")
@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RackResource {

    private static final Gson GSON = new Gson();

    private static final Client CLIENT = Client.create();

    @PersistenceContext(unitName = "IBTSRemoteLabPU")
    private EntityManager em;

    @GET
    public String listRacks(@Context HttpServletRequest request) {
        TypedQuery<Rack> query = em.createNamedQuery("Rack.findAll", Rack.class);
        List<Rack> racks = query.getResultList();
        return GSON.toJson(racks);
    }

    private void addRackDevices(Rack rack) {
        try {
            WebResource resource = CLIENT.resource(String.format("http://%s:%d/availablePorts",
                    rack.getLocalIP(), rack.getLocalControlPort()));
            String response = resource.get(String.class);
            Type collectionType = new TypeToken<Collection<String>>(){}.getType();
            Collection<String> paths = GSON.fromJson(response, collectionType);
            for (String path : paths) {
                Device device = new Device();
                device.setRack(rack);
                device.setPath(path);
                device.setName(path.substring(path.lastIndexOf('/') + 1));
                em.persist(device);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @POST
    public void add(String json,
                    @Context HttpServletRequest request) {
        try {
            Rack rack = GSON.fromJson(json, Rack.class);
            em.persist(rack);
            addRackDevices(rack);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @DELETE
    @Path("{rackId}")
    public void deleteRack(@PathParam("rackId") Long rackId) {
        try {
            Rack rack = em.find(Rack.class, rackId);
            Query query = em.createNamedQuery("Rack.deleteDevices");
            query.setParameter("rack", rack);
            query.executeUpdate();
            em.remove(rack);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @GET
    @Path("ping/{rackId}")
    public Response ping(@PathParam("rackId") Long rackId) {
        String status = "offline";
        try {
            Rack rack = em.find(Rack.class, rackId);
            WebResource resource = CLIENT.resource(String.format("http://%s:%d/ping",
                    rack.getLocalIP(), rack.getLocalControlPort()));
            resource.get(String.class);
            status = "online";
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return Response.ok().entity(GSON.toJson(status)).build();
    }
}

package com.github.avereshchagin.ciscolab.webapp.rest;

import com.github.avereshchagin.ciscolab.webapp.jpa.Device;
import com.google.gson.Gson;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/device")
@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DeviceResource {

    private static final int MAX_DEVICES = 100;

    private static final Gson GSON = new Gson();

    @PersistenceContext(unitName = "IBTSRemoteLabPU")
    private EntityManager em;

    @GET
    public String getAllDevices() {
        TypedQuery<Device> query = em.createNamedQuery("allDevices", Device.class);
        List<Device> devices = query.setMaxResults(MAX_DEVICES).getResultList();
        return GSON.toJson(devices);
    }

    @GET
    @Path("{deviceId}")
    public String getDevice(@PathParam("deviceId") Long deviceId) {
        try {
            Device device = em.find(Device.class, deviceId);
            return GSON.toJson(device);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return GSON.toJson(null);
    }

    @POST
    public void addDevice(String json) {
        try {
            Device device = GSON.fromJson(json, Device.class);
            em.persist(device);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @DELETE
    @Path("{deviceId}")
    public void deleteDevice(@PathParam("deviceId") Long deviceId) {
        try {
            Device device = em.find(Device.class, deviceId);
            em.remove(device);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
}

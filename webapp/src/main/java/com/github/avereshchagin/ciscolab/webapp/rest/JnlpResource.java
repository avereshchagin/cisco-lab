package com.github.avereshchagin.ciscolab.webapp.rest;

import com.github.avereshchagin.ciscolab.webapp.jpa.Device;
import com.github.avereshchagin.ciscolab.webapp.jpa.Rack;
import com.sun.jersey.api.Responses;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Path("jnlp")
@Stateless
@Produces("application/x-java-jnlp-file")
public class JnlpResource {

    @PersistenceContext(unitName = "IBTSRemoteLabPU")
    private EntityManager em;

    private boolean isSiteLocalRequest(HttpServletRequest request) {
        String address = request.getRemoteAddr();
        if (address != null && !address.isEmpty()) {
            try {
                InetAddress inetAddress = InetAddress.getByName(address);
                if (inetAddress.isSiteLocalAddress()) {
                    return true;
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static final String JNLP_TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<jnlp spec=\"1.0+\" codebase=\"http://%s:%d/console\">\n" +
            "    <information>\n" +
            "        <title>IBTS Cisco Lab</title>\n" +
            "        <vendor>Aleksandr Vereshchagin</vendor>\n" +
            "    </information>\n" +
            "    <security>\n" +
            "        <all-permissions/>\n" +
            "    </security>\n" +
            "    <resources>\n" +
            "        <j2se version=\"1.6+\"/>\n" +
            "        <jar href=\"applet-client.jar\" main=\"true\"/>\n" +
            "    </resources>\n" +
            "    <application-desc\n" +
            "            name=\"Applet Client\"\n" +
            "            main-class=\"com.github.avereshchagin.ciscolab.Main\"\n" +
            "            width=\"600\"\n" +
            "            height=\"400\">\n" +
            "        <argument>%s</argument>\n" +
            "        <argument>%d</argument>\n" +
            "        <argument>%s</argument>\n" +
            "        <argument>%d</argument>\n" +
            "    </application-desc>\n" +
            "</jnlp>";

    @GET
    @Path("console{deviceId}.jnlp")
    public Response getJnlp(@Context HttpServletRequest request,
                            @PathParam("deviceId") Long deviceId,
                            @QueryParam("accessToken") String accessToken) {
        Device device = em.find(Device.class, deviceId);
        if (device != null) {
            Rack rack = device.getRack();
            if (rack != null) {
                String host;
                int port;
                if (isSiteLocalRequest(request)) {
                    host = rack.getLocalIP();
                    port = rack.getLocalTerminalPort();
                } else {
                    host = rack.getExternalIP();
                    port = rack.getExternalTerminalPort();
                }
                Response.ResponseBuilder response = Response.ok();
                response.entity(String.format(JNLP_TEMPLATE, request.getServerName(), request.getServerPort(),
                        host, port, accessToken, deviceId));
                return response.build();
            }
        }
        return Responses.notFound().build();
    }
}

package com.github.avereshchagin.ciscolab.webapp.rest;

import com.google.gson.Gson;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/")
public class RestApi {

    @GET
    @Path("getPortName")
    @Produces(MediaType.APPLICATION_JSON)
    public String getPortName(@QueryParam("accessToken") String accessToken, @QueryParam("deviceId") int deviceId) {
        Gson gson = new Gson();
        if (deviceId != 1) {
            return gson.toJson("");
        }
        return gson.toJson("/dev/ttyS0");
    }

}

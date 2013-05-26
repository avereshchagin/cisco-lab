package com.github.avereshchagin.ciscolab.webapp.rest;

import com.google.gson.Gson;
import com.sun.jersey.api.Responses;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;

@Path("/lab")
@Stateless
public class LabResource {

    private static final Gson GSON = new Gson();

    @GET
    @Path("topology")
    @Produces("image/svg+xml")
    public Response getTopology() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Source xslt = new StreamSource(getClass().getResourceAsStream("transform.xsl"));
            Source xml = new StreamSource(getClass().getResourceAsStream("topology.xml"));
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer(xslt);
            transformer.transform(xml, new StreamResult(out));
            String svg = out.toString();
            return Response.ok().entity(svg).build();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return Responses.noContent().build();
    }
}

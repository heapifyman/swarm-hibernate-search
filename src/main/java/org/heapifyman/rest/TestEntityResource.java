package org.heapifyman.rest;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.heapifyman.model.TestEntity;
import org.heapifyman.service.TestEntityService;

@Path("/testentities")
public class TestEntityResource {

    @Inject
    Logger logger;

    @Inject
    TestEntityService testEntityService;

    @GET
    @Path("/")
    @Produces({ MediaType.APPLICATION_XML })
    public List<TestEntity> getAll() {
        return testEntityService.getAll();
    }

    @GET
    @Path("/search")
    @Produces({ MediaType.APPLICATION_XML })
    public List<TestEntity> searchByName(@QueryParam("q") String queryString) {
        return testEntityService.searchByName(queryString);
    }
}

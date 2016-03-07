package org.heapifyman.init;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.heapifyman.model.TestEntity;
import org.heapifyman.service.TestEntityService;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;

@ApplicationScoped
@Singleton
@Startup
public class Initializer {

    @Inject
    Logger logger;

    @Inject
    EntityManager em;

    @Inject
    TestEntityService testEntityService;

    @PostConstruct
    public void init() {
        try {
            // initialize hibernate full text search
            FullTextEntityManager ftem = Search.getFullTextEntityManager(em);
            ftem.createIndexer().startAndWait();

            // create test entities
            for (int i = 1; i < 12; i++) {
                createTestEntity(i);
            }

            List<TestEntity> all = testEntityService.getAll();
            for (TestEntity testEntity : all) {
                logger.info("" + testEntity);
            }

        } catch (InterruptedException e) {
            logger.severe(e.getMessage());
        }
    }

    private TestEntity createTestEntity(int i) {
        TestEntity test = new TestEntity();
        test.setName("TestEntity_" + i);
        test = testEntityService.create(test);
        logger.info("Created: " + test);
        return test;
    }
}

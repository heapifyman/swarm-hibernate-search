package org.heapifyman.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.lucene.search.Query;
import org.heapifyman.model.TestEntity;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.TermMatchingContext;
import org.hibernate.search.query.dsl.WildcardContext;

@Stateless
public class TestEntityService {

    @Inject
    EntityManager em;

    public TestEntity create(TestEntity testEntity) {
        em.persist(testEntity);
        return testEntity;
    }

    public List<TestEntity> getAll() {
        return em.createQuery("select te from TestEntity te", TestEntity.class).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<TestEntity> searchByName(String queryString) {
        FullTextEntityManager ftem = Search.getFullTextEntityManager(em);
        QueryBuilder qb = ftem.getSearchFactory().buildQueryBuilder().forEntity(TestEntity.class).get();
        Query luceneQuery;
        try {
            WildcardContext wc = qb.keyword().wildcard();
            TermMatchingContext tmc = wc.onField("name");
            luceneQuery = tmc.ignoreAnalyzer().ignoreFieldBridge().matching(queryString.toLowerCase()).createQuery();
        } catch (Exception e) {
            return new ArrayList<>(0);
        }
        FullTextQuery query = ftem.createFullTextQuery(luceneQuery, TestEntity.class);
        return query.getResultList();
    }

}

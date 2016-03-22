package org.heapifyman;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.wildfly.swarm.container.Container;
import org.wildfly.swarm.datasources.DatasourcesFraction;
import org.wildfly.swarm.hibernate.search.HibernateSearchFraction;
import org.wildfly.swarm.jaxrs.JAXRSArchive;
import org.wildfly.swarm.jpa.JPAFraction;

public class Main {

    private static final String PROJECT_NAME = "swarm-hibernate-search";
    private static final String DS_NAME = PROJECT_NAME + "-DS";

    public static void main(String[] args) throws Exception {
        Container container = new Container();


        container.fraction(new DatasourcesFraction()
                .jdbcDriver("h2", (driver) -> {
                    driver.driverClassName("org.h2.Driver");
                    driver.xaDatasourceClass("org.h2.jdbcx.JdbcDataSource");
                    driver.driverModuleName("com.h2database.h2");
                }).dataSource(DS_NAME, (dataSource) -> {
                    dataSource.driverName("h2");
                    dataSource.connectionUrl("jdbc:h2:mem:" + PROJECT_NAME + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
                    dataSource.userName("sa");
                    dataSource.password("sa");
                }));

        // Prevent JPA Fraction from installing it's default datasource fraction and use ours instead
        container.fraction(new JPAFraction()
                .inhibitDefaultDatasource()
                .defaultDatasource("jboss/datasources/" + DS_NAME)
        );

        // add hibernate search fraction
        container.fraction(HibernateSearchFraction.createDefaultFraction());

        container.start();

        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class);
        deployment.addPackages(true, Main.class.getPackage());
        deployment.addAsWebInfResource(new ClassLoaderAsset("META-INF/beans.xml", Main.class.getClassLoader()), "beans.xml");
        deployment.addAsWebInfResource(new ClassLoaderAsset("META-INF/persistence.xml", Main.class.getClassLoader()), "classes/META-INF/persistence.xml");
        deployment.addAllDependencies();

        container.deploy(deployment);
    }

}

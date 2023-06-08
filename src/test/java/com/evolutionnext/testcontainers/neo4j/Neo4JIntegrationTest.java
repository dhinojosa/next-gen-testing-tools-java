package com.evolutionnext.testcontainers.neo4j;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.GraphDatabase;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class Neo4JIntegrationTest {

    @Container
    private static Neo4jContainer<?> neo4JContainer =
        new Neo4jContainer<>(DockerImageName.parse("neo4j:4.4"))
            .withReuse(true)
            .withLabel("com.xyzcorp", "opt")
            .withoutAuthentication();

    @Test
    void testContainerReturningAllNodes() {
        System.out.println(neo4JContainer.getBoltUrl());
        try (var driver = GraphDatabase.driver(neo4JContainer.getBoltUrl()); var session =
            driver.session()) {
            var result = session.run("CREATE (n)");
            var summary = result.consume();
            System.out.println(summary.counters().nodesCreated());
        }
    }

    @Test
    void testContainerCreatingTwoNodesWithCyber() {
        try (var driver = GraphDatabase.driver(neo4JContainer.getBoltUrl());
             var session = driver.session()) {
            String pattern = "CREATE (keanu:Person:Actor {name: 'Keanu Reeves'})-[role:ACTED_IN {roles: ['Neo']}]->(matrix:Movie {title: 'The Matrix'})";
            session.run(pattern);
            var result = session.run("CREATE (:Person)-[:ACTED_IN]->(:Movie)");
            var summary = result.consume();
            System.out.println(summary.counters().nodesCreated());
        }
    }

    @AfterAll
    static void afterAll() {
        neo4JContainer.close();
    }
}

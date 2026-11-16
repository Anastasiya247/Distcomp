package com.example.tweetapp;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TweetApiTests {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/v1.0";
    }

    // ==========================================
    // БАЗОВЫЕ ТЕСТЫ REST (TASK 310)
    // ==========================================

    @Test
    void testAuthorCrud() {
        String uniqueLogin = "author_" + System.currentTimeMillis();

        Integer authorId = given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "login": "%s",
                    "password": "password123",
                    "firstname": "TestName",
                    "lastname": "TestLast"
                }
                """, uniqueLogin))
                .when()
                .post("/authors")
                .then()
                .statusCode(201)
                .body("login", equalTo(uniqueLogin))
                .body("firstname", equalTo("TestName"))
                .extract().path("id");

        given()
                .pathParam("id", authorId)
                .when()
                .get("/authors/{id}")
                .then()
                .statusCode(200)
                .body("login", equalTo(uniqueLogin));

        given()
                .contentType(ContentType.JSON)
                .pathParam("id", authorId)
                .body(String.format("""
                {
                    "login": "%s",
                    "password": "password123",
                    "firstname": "UpdatedName",
                    "lastname": "TestLast"
                }
                """, uniqueLogin))
                .when()
                .put("/authors/{id}")
                .then()
                .statusCode(200)
                .body("firstname", equalTo("UpdatedName"));

        given()
                .pathParam("id", authorId)
                .when()
                .delete("/authors/{id}")
                .then()
                .statusCode(204);
    }

    @Test
    void testDuplicateAuthorLogin() {
        String uniqueLogin = "dup_login_" + System.currentTimeMillis();

        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {"login": "%s", "password": "password123", "firstname": "NameOne", "lastname": "LastOne"}
                """, uniqueLogin))
                .when().post("/authors")
                .then().statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {"login": "%s", "password": "password123", "firstname": "NameTwo", "lastname": "LastTwo"}
                """, uniqueLogin))
                .when()
                .post("/authors")
                .then()
                .statusCode(403)
                .body("errorCode", equalTo(40301));
    }

    @Test
    void testLabelCrud() {
        String uniqueLabel = "label_" + System.currentTimeMillis();

        Integer labelId = given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {"name": "%s"}
                """, uniqueLabel))
                .when()
                .post("/labels")
                .then()
                .statusCode(201)
                .body("name", equalTo(uniqueLabel))
                .extract().path("id");

        given()
                .contentType(ContentType.JSON)
                .pathParam("id", labelId)
                .body("""
                {"name": "updated_label_long"}
                """)
                .when()
                .put("/labels/{id}")
                .then()
                .statusCode(200)
                .body("name", equalTo("updated_label_long"));

        given()
                .pathParam("id", labelId)
                .when()
                .delete("/labels/{id}")
                .then()
                .statusCode(204);
    }

    @Test
    void testDuplicateLabelName() {
        String uniqueLabel = "dup_label_" + System.currentTimeMillis();

        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {"name": "%s"}
                """, uniqueLabel))
                .when().post("/labels")
                .then().statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {"name": "%s"}
                """, uniqueLabel))
                .when()
                .post("/labels")
                .then()
                .statusCode(403)
                .body("errorCode", equalTo(40302));
    }

    @Test
    void testTweetAndReactionCrud() {
        long timestamp = System.currentTimeMillis();
        String uniqueLogin = "tweet_author_" + timestamp;
        String uniqueTitle = "Test Tweet Title " + timestamp;
        String uniqueLabel = "tweet_label_" + timestamp;

        Integer authorId = given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {"login": "%s", "password": "password123", "firstname": "TweetName", "lastname": "AuthorLast"}
                """, uniqueLogin))
                .when().post("/authors")
                .then().statusCode(201).extract().path("id");

        Integer labelId = given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {"name": "%s"}
                """, uniqueLabel))
                .when().post("/labels")
                .then().statusCode(201).extract().path("id");

        Integer tweetId = given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "authorId": %d,
                    "title": "%s",
                    "content": "This is a test tweet content that is long enough",
                    "labelIds": ["%d"]
                }
                """, authorId, uniqueTitle, labelId))
                .when()
                .post("/tweets")
                .then()
                .statusCode(201)
                .body("title", equalTo(uniqueTitle))
                .body("authorId", equalTo(authorId))
                .extract().path("id");

        Integer reactionId = given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {"tweetId": %d, "content": "Nice tweet, thanks!"}
                """, tweetId))
                .when()
                .post("/reactions")
                .then()
                .statusCode(201)
                .body("content", equalTo("Nice tweet, thanks!"))
                .extract().path("id");

        given()
                .pathParam("id", reactionId)
                .when()
                .delete("/reactions/{id}")
                .then()
                .statusCode(204);

        given()
                .pathParam("id", tweetId)
                .when()
                .delete("/tweets/{id}")
                .then()
                .statusCode(204);

        given().pathParam("id", labelId).delete("/labels/{id}").then().statusCode(204);
        given().pathParam("id", authorId).delete("/authors/{id}").then().statusCode(204);
    }

    @Test
    void testTweetWithInvalidAuthor() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                {"authorId": 99999, "title": "Valid Title Here", "content": "Valid Content Here"}
                """)
                .when()
                .post("/tweets")
                .then()
                .statusCode(403)
                .body("errorCode", equalTo(40302));
    }

    @Test
    void testTweetWithInvalidLabel() {
        long timestamp = System.currentTimeMillis();
        String uniqueLogin = "invalid_label_author_" + timestamp;

        Integer authorId = given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {"login": "%s", "password": "password123", "firstname": "NameValid", "lastname": "LastValid"}
                """, uniqueLogin))
                .when().post("/authors")
                .then().statusCode(201).extract().path("id");

        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {"authorId": %d, "title": "Valid Title Here", "content": "Valid Content Here", "labelIds": ["99999"]}
                """, authorId))
                .when()
                .post("/tweets")
                .then()
                .statusCode(403)
                .body("errorCode", equalTo(40303));

        given().pathParam("id", authorId).delete("/authors/{id}");
    }

    @Test
    void testReactionWithInvalidTweet() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                {"tweetId": 99999, "content": "Test reaction content"}
                """)
                .when()
                .post("/reactions")
                .then()
                .statusCode(403)
                .body("errorCode", equalTo(40304));
    }

    @Test
    void testValidationErrors() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                {"login": "a", "password": "password123", "firstname": "TestName", "lastname": "TestLast"}
                """)
                .when()
                .post("/authors")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo(40001));
    }

    @Test
    void testNotFoundErrors() {
        given()
                .pathParam("id", 99999)
                .when()
                .get("/authors/{id}")
                .then()
                .statusCode(404)
                .body("errorCode", equalTo(40401));

        given()
                .pathParam("id", 99999)
                .when()
                .get("/tweets/{id}")
                .then()
                .statusCode(404)
                .body("errorCode", equalTo(40403));
    }

    // ==========================================
    // НОВЫЕ ТЕСТЫ ДЛЯ JPA (TASK 320)
    // ==========================================

    @Test
    void testJpaManyToManyRelation() {
        long timestamp = System.currentTimeMillis();
        String uniqueLogin = "jpa_author_" + timestamp;
        String uniqueTitle = "JPA ManyToMany Test " + timestamp;
        String label1 = "jpa_label_1_" + timestamp;
        String label2 = "jpa_label_2_" + timestamp;

        Integer authorId = given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {"login": "%s", "password": "password123", "firstname": "JpaName", "lastname": "JpaLast"}
                """, uniqueLogin))
                .when().post("/authors")
                .then().statusCode(201).extract().path("id");

        Integer labelId1 = given()
                .contentType(ContentType.JSON).body(String.format("{\"name\": \"%s\"}", label1))
                .when().post("/labels").then().statusCode(201).extract().path("id");
        Integer labelId2 = given()
                .contentType(ContentType.JSON).body(String.format("{\"name\": \"%s\"}", label2))
                .when().post("/labels").then().statusCode(201).extract().path("id");

        Integer tweetId = given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "authorId": %d,
                    "title": "%s",
                    "content": "Testing relation between tweet and labels",
                    "labelIds": ["%d", "%d"]
                }
                """, authorId, uniqueTitle, labelId1, labelId2))
                .when().post("/tweets")
                .then().statusCode(201).extract().path("id");

        given()
                .pathParam("id", tweetId)
                .when().get("/tweets/{id}")
                .then()
                .statusCode(200)
                .body("labelIds", hasSize(2));

        given().pathParam("id", tweetId).delete("/tweets/{id}");
        given().pathParam("id", labelId1).delete("/labels/{id}");
        given().pathParam("id", labelId2).delete("/labels/{id}");
        given().pathParam("id", authorId).delete("/authors/{id}");
    }

    @Test
    void testJpaCascadeReactionUpdate() {
        long timestamp = System.currentTimeMillis();
        String uniqueLogin = "cascade_author_" + timestamp;
        String uniqueTitle = "Cascade Test Title " + timestamp;

        Integer authorId = given().contentType(ContentType.JSON)
                .body(String.format("{\"login\": \"%s\", \"password\": \"password123\", \"firstname\": \"AuthorName\", \"lastname\": \"AuthorLast\"}", uniqueLogin))
                .when().post("/authors").then().statusCode(201).extract().path("id");

        Integer tweetId = given().contentType(ContentType.JSON)
                .body(String.format("{\"authorId\": %d, \"title\": \"%s\", \"content\": \"This is content here\"}", authorId, uniqueTitle))
                .when().post("/tweets").then().statusCode(201).extract().path("id");

        Integer reactionId = given().contentType(ContentType.JSON)
                .body(String.format("{\"tweetId\": %d, \"content\": \"Original reaction text\"}", tweetId))
                .when().post("/reactions").then().statusCode(201).extract().path("id");

        given().contentType(ContentType.JSON)
                .pathParam("id", reactionId)
                .body(String.format("{\"tweetId\": %d, \"content\": \"Updated reaction text\"}", tweetId))
                .when().put("/reactions/{id}")
                .then().statusCode(200);

        given().pathParam("id", reactionId)
                .when().get("/reactions/{id}")
                .then()
                .statusCode(200)
                .body("content", equalTo("Updated reaction text"));

        given().pathParam("id", reactionId).delete("/reactions/{id}").then().statusCode(204);
        given().pathParam("id", tweetId).delete("/tweets/{id}").then().statusCode(204);
        given().pathParam("id", authorId).delete("/authors/{id}").then().statusCode(204);
    }
}
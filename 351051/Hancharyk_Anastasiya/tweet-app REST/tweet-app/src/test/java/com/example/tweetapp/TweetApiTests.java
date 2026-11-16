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

    @Test
    void testAuthorCrud() {
        Integer authorId = given()
                .contentType(ContentType.JSON)
                .body("""
                {
                    "login": "author_test_long",
                    "password": "password123",
                    "firstname": "TestName",
                    "lastname": "TestLast"
                }
                """)
                .when()
                .post("/authors")
                .then()
                .statusCode(201)
                .body("login", equalTo("author_test_long"))
                .body("firstname", equalTo("TestName"))
                .extract().path("id");

        given()
                .pathParam("id", authorId)
                .when()
                .get("/authors/{id}")
                .then()
                .statusCode(200)
                .body("login", equalTo("author_test_long"));

        given()
                .contentType(ContentType.JSON)
                .pathParam("id", authorId)
                .body("""
                {
                    "login": "author_test_long",
                    "password": "password123",
                    "firstname": "UpdatedName",
                    "lastname": "TestLast"
                }
                """)
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
        given()
                .contentType(ContentType.JSON)
                .body("""
                {"login": "duplicate_login", "password": "password123", "firstname": "NameOne", "lastname": "LastOne"}
                """)
                .when().post("/authors")
                .then().statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .body("""
                {"login": "duplicate_login", "password": "password123", "firstname": "NameTwo", "lastname": "LastTwo"}
                """)
                .when()
                .post("/authors")
                .then()
                .statusCode(409)
                .body("errorCode", equalTo(40901));
    }

    @Test
    void testLabelCrud() {
        Integer labelId = given()
                .contentType(ContentType.JSON)
                .body("""
                {"name": "test_label_long"}
                """)
                .when()
                .post("/labels")
                .then()
                .statusCode(201)
                .body("name", equalTo("test_label_long"))
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
        given()
                .contentType(ContentType.JSON)
                .body("""
                {"name": "duplicate_label"}
                """)
                .when().post("/labels")
                .then().statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .body("""
                {"name": "duplicate_label"}
                """)
                .when()
                .post("/labels")
                .then()
                .statusCode(409)
                .body("errorCode", equalTo(40902));
    }

    @Test
    void testTweetAndReactionCrud() {
        Integer authorId = given()
                .contentType(ContentType.JSON)
                .body("""
                {"login": "tweet_author", "password": "password123", "firstname": "TweetName", "lastname": "AuthorLast"}
                """)
                .when().post("/authors")
                .then().statusCode(201).extract().path("id");

        Integer labelId = given()
                .contentType(ContentType.JSON)
                .body("""
                {"name": "tweet_label"}
                """)
                .when().post("/labels")
                .then().statusCode(201).extract().path("id");

        Integer tweetId = given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "authorId": %d,
                    "title": "Test Tweet Title",
                    "content": "This is a test tweet content that is long enough",
                    "labelIds": [%d]
                }
                """, authorId, labelId))
                .when()
                .post("/tweets")
                .then()
                .statusCode(201)
                .body("title", equalTo("Test Tweet Title"))
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
                .statusCode(400)
                .body("errorCode", equalTo(40002));
    }

    @Test
    void testTweetWithInvalidLabel() {
        Integer authorId = given()
                .contentType(ContentType.JSON)
                .body("""
                {"login": "invalid_label_author", "password": "password123", "firstname": "NameValid", "lastname": "LastValid"}
                """)
                .when().post("/authors")
                .then().statusCode(201).extract().path("id");

        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {"authorId": %d, "title": "Valid Title Here", "content": "Valid Content Here", "labelIds": [99999]}
                """, authorId))
                .when()
                .post("/tweets")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo(40003));

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
                .statusCode(400)
                .body("errorCode", equalTo(40004));
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
}
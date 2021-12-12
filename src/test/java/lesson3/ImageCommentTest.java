package lesson3;

import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ImageCommentTest extends BaseTest {
    static String commentId;
    static String commentToCommentId;
    static String imageToCommentId = "OEMeeFI";

    @BeforeAll
    static void beforeAllTest() {
        given()
                .headers("Authorization", token)
                .when()
                .get("https://api.imgur.com/3/image/{imageHash}", imageToCommentId)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    @Order(1)
     void addCommentToImageTest() {
        commentId = given()
                .headers("Authorization", token)
                .multiPart("image_id", imageToCommentId)
                .multiPart("comment", "What's a beautiful view!")
                .when()
                .post("https://api.imgur.com/3/comment")
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("data.id");
    }

    @Test
    @Order(2)
    void addCommentToCommentTest() {
        commentToCommentId = given()
                .headers("Authorization", token)
                .multiPart("image_id", imageToCommentId)
                .multiPart("parent_id", commentId)
                .multiPart("comment", "Realy nice!")
                .when()
                .post("https://api.imgur.com/3/comment")
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("data.id");
    }

    @Test
    @Order(3)
    void getAllCommentsHaveBeenCreatedTest() {
        given()
                .headers("Authorization", token)
                .when()
                .get("https://api.imgur.com/3/account/{username}/comments", username)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    @Order(4)
    void getLastCommentHasBeenCreatedTest() {
        given()
                .headers("Authorization", token)
                .when()
                .get("https://api.imgur.com/3/account/{username}/comment/{commentId}", username, commentId)
                .prettyPeek()
                .then()
                .statusCode(200);
    }


    @Test
    @Order(5)
    void deleteCommentToCommentTest() {
        given()
                .headers("Authorization", token)
                .when()
                .delete("https://api.imgur.com/3/comment/{commentId}", commentToCommentId)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    @Order(6)
    void deleteCommentToImageTest() {
        given()
                .headers("Authorization", token)
                .when()
                .delete("https://api.imgur.com/3/comment/{commentId}", commentId)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

}

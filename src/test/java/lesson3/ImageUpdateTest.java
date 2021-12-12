package lesson3;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.CoreMatchers.is;

public class ImageUpdateTest extends BaseTest {
    private final String PATH_TO_IMAGE = "https://avatarko.ru/img/kartinka/33/multfilm_lyagushka_32117.jpg";
    String uploadedImageId;

    @BeforeEach
    void beforeAllTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", PATH_TO_IMAGE)
                .multiPart("type", "url")
                .multiPart("title", "Stitch and frog")
                .multiPart("description", "Stitch and frog from Lilo & Stitch illustration")
                .when()
                .post("https://api.imgur.com/3/upload")
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.id");
    }

    @Test
    void getImageTest() {
        given()
                .headers("Authorization", token)
                .when()
                .get("https://api.imgur.com/3/image/{imageHash}", uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("data.title", is("Stitch and frog"))
                .body("data.description", containsStringIgnoringCase("stitch"));
    }

    @Test
    void updateImageTitleTest() {
        given()
                .headers("Authorization", token)
                .multiPart("title", "no name")
                .when()
                .post("https://api.imgur.com/3/image/{imageHash}", uploadedImageId)
                .prettyPeek()
                .then()
                .body("success", is(true))
                .statusCode(200);
    }

    @Test
    void updateImageDescriptionTest() {
        given()
                .headers("Authorization", token)
                .multiPart("description", "no descrioption")
                .when()
                .post("https://api.imgur.com/3/image/{imageHash}", uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    void updateImageTitleAndDescriptionTest() {
        given()
                .headers("Authorization", token)
                .multiPart("title", "changed name")
                .multiPart("description", "changed descrioption")
                .when()
                .post("https://api.imgur.com/3/image/{imageHash}", uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    void favoriteImageTest() {
        given()
                .headers("Authorization", token)
                .when()
                .post("https://api.imgur.com/3/image/{imageHash}/favorite", uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    void addCommentToImageTest() {
        given()
                .headers("Authorization", token)
                .multiPart("image_id", "y7ezcNL")
                .multiPart("comment", "What's a beautiful view!")
                .when()
                .post("https://api.imgur.com/3/comment")
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @AfterEach
    void tearDown() {
        given()
                .headers("Authorization", token)
                .when()
                .delete("https://api.imgur.com/3/account/{username}/image/{deleteHash}", username, uploadedImageId)
                .then()
                .statusCode(200);
    }

}

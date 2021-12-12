package lesson3;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.apache.commons.io.FileUtils.readFileToByteArray;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class ImageUploadTests extends BaseTest {
    private final String PATH_TO_IMAGE = "src/test/resources/black_image.jpg";
    static String encodedFile;
    String uploadedImageId;

    @Test
    void uploadFileEncodedBase64Test() {
        byte[] byteArray = getFileContent();
        encodedFile = Base64.getEncoder().encodeToString(byteArray);
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", encodedFile)
                .multiPart("type", "base64")
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    void uploadFileImageTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", new File (PATH_TO_IMAGE))
                .multiPart("type", "file")
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    void uploadUrlImageTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", "https://avatarko.ru/img/kartinka/33/multfilm_lyagushka_32117.jpg")
                .multiPart("type", "url")
                .multiPart("title", "my favorite")
                .expect()
                .statusCode(200)
                .body("data.title", is("my favorite"))
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
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

    private byte[] getFileContent() {
        byte[] byteArray = new byte[0];
        try {
            byteArray = readFileToByteArray(new File(PATH_TO_IMAGE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }


}

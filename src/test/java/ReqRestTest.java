import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class ReqRestTest extends BaseTest  {

    @Test
    public void loginTest(){
                given()
                .body("{\n" +
                        "    \"email\": \"eve.holt@reqres.in\",\n" +
                        "    \"password\": \"cityslicka\"\n" +
                        "}")
                .post("login")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("token", notNullValue());
    }

    @Test
    public void getSingleUserTest(){
                given()
                .get("users/2")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("data.id", equalTo(2));
    }

    @Test
    public void deleteUserTest(){
        given()
                .delete("users/2")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void patchUserTest(){
        String nameUpdate = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"zion resident\"\n" +
                        "}")
                .patch("users/2")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .jsonPath().getString("name");

        assertThat(nameUpdate, equalTo("morpheus"));
    }

    @Test
    public void putuserTest(){
        String jobUpdate = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"zion resident\"\n" +
                        "}")
                .put("users/2")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .jsonPath().getString("job");

        assertThat(jobUpdate, equalTo("zion resident"));
    }

    @Test
    public void getAllUsersTest(){
        Response response = given()
                .get("users?page=2");

        Headers headers = response.getHeaders();
        int statusCode = response.getStatusCode();
        String body = response.getBody().asString();
        String contentType = response.getContentType();

        assertThat(statusCode, equalTo(HttpStatus.SC_OK));
        System.out.println("body: " + body);
        System.out.println("content type: " + contentType);
        System.out.println("headers: " + headers);
        System.out.println("****************");
        System.out.println("****************");
        System.out.println(headers.get("Content-Type"));
        System.out.println(headers.get("Transfer-Encoding"));
    }

    @Test
    public void getAllUsersTest2(){
        String response = given()
                .when()
                .get("users?page=2")
                .then()
                .extract()
                .body()
                .asString();

        int page = from(response).get("page");
        int totalPages = from(response).get("total_pages");

        int idFirstUser = from(response).get("data[0].id");

        System.out.println("page = " + page);
        System.out.println("totalPages = " + totalPages);
        System.out.println("idFirstUser = " + idFirstUser);

        List<Map> userWithIdGreaterThan10 = from(response).get("data.findAll { user -> user.id > 10 }");
        String email = userWithIdGreaterThan10.get(0).get("email").toString();

        List<Map> user = from(response).get("data.findAll { user -> user.id > 10 && user.last_name == 'Howell' }");
        int id = Integer.valueOf(user.get(0).get("id").toString());
    }

    @Test
    public void createUserTest(){
        String response = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"leader\"\n" +
                        "}")
                .post("users")
                .then()
                .extract()
                .body()
                .asString();

        User user = from(response).getObject("", User.class);

        System.out.println(user.getId());
        System.out.println(user.getJob());
    }

    @Test
    public void registerUserTest(){
        CreateUserRequest user = new CreateUserRequest();
        user.setEmail("eve.holt@reqres.in");
        user.setPassword("pistol");

        CreateUserResponse userResponse = given()
                .when()
                .body(user)
                .post("register")
                .then()
                .spec(defaultResponseSpecification())
                .contentType(equalTo("application/json; charset=utf-8"))
                .extract()
                .body()
                .as(CreateUserResponse.class);

        assertThat(userResponse.getId(), equalTo(4));
        assertThat(userResponse.getToken(), equalTo("QpwL5tke4Pnpja7X4"));
    }
}

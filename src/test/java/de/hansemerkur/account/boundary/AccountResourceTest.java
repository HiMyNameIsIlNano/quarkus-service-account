package de.hansemerkur.account.boundary;

import de.hansemerkur.account.entity.Account;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountResourceTest {

    @AfterEach
    void afterEach() {
        given().when()
                .contentType(ContentType.JSON)
                .post("/accounts/restore")
                .then()
                .statusCode(Status.OK.getStatusCode());
    }

    private Account saveAccount(Account account) {
        return given().when()
                .contentType(ContentType.JSON)
                .body(account)
                .post("/accounts")
                .then()
                .statusCode(Status.CREATED.getStatusCode())
                .extract()
                .as(Account.class);
    }

    @Test
    @Order(1)
    void allAccounts() {
        List<Account> allAccounts = given().when()
                .get("/accounts")
                .then()
                .statusCode(Status.OK.getStatusCode())
                .body(
                        containsString("Cookie Monster"),
                        containsString("Count von Count"),
                        containsString("Elmo")
                )
                .extract()
                .jsonPath()
                .getList(".", Account.class);

        assertThat(allAccounts, not(empty()));
        assertThat(allAccounts.size(), is(3));
    }

    @Test
    @Order(2)
    void getAccount() {
        Account cookieMonster = given().when().get("/accounts/{accountId}", 1)
                .then()
                .statusCode(Status.OK.getStatusCode())
                .extract()
                .as(Account.class);

        assertThat(cookieMonster.getCustomerName(), equalTo("Cookie Monster"));
        assertThat(cookieMonster.getAccountNumber(), equalTo(1L));
        assertThat(cookieMonster.getCustomerNumber(), equalTo(1L));
        assertThat(cookieMonster.getBalance(), equalTo(BigDecimal.valueOf(10)));
    }

    @Test
    @Order(3)
    void saveAccount_shouldReturn201_whenNewAccount() {
        Account bert = new Account(4L, 4L, "Bert", BigDecimal.valueOf(40));

        Account account = saveAccount(bert);

        assertThat(account, notNullValue());
        assertThat(account, is(equalTo(bert)));

        List<Account> allAccounts = given().when()
                .get("/accounts")
                .then()
                .statusCode(Status.OK.getStatusCode())
                .body(
                        containsString("Cookie Monster"),
                        containsString("Count von Count"),
                        containsString("Elmo"),
                        containsString("Bert")
                )
                .extract()
                .jsonPath()
                .getList(".", Account.class);

        assertThat(allAccounts, not(empty()));
        assertThat(allAccounts.size(), is(4));
    }

    @Test
    @Order(4)
    void saveAccount_shouldReturn304_whenDuplicate() {
        Account cookieMonster = given().when().get("/accounts/{accountId}", 1)
                .then()
                .statusCode(200)
                .extract()
                .as(Account.class);

        given().when()
                .contentType(ContentType.JSON)
                .body(cookieMonster)
                .post("/accounts")
                .then()
                .statusCode(Status.NOT_MODIFIED.getStatusCode());
    }

    @Test
    @Order(5)
    void deleteAccount_shouldReturn200_whenDeletingAccount() {
        Account cookieMonster = given().when().get("/accounts/{accountId}", 1)
                .then()
                .statusCode(200)
                .extract()
                .as(Account.class);

        Account deletedAccount = given().when()
                .contentType(ContentType.JSON)
                .delete("/accounts/{accountId}", cookieMonster.getAccountNumber())
                .then()
                .statusCode(Status.OK.getStatusCode())
                .extract()
                .as(Account.class);

        assertThat(deletedAccount, equalTo(cookieMonster));
    }

    @Test
    @Order(6)
    void deleteAccount_shouldReturn404_whenAccountDoesNotExist() {
        given().when()
                .contentType(ContentType.JSON)
                .delete("/accounts/{accountId}", -1L)
                .then()
                .statusCode(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @Order(7)
    void depositFund_shouldReturn204_whenAccountUpdated() {
        given().when()
                .contentType(ContentType.JSON)
                .put("/accounts/deposit/{accountId}/{amount}", 1L, BigDecimal.valueOf(10))
                .then()
                .statusCode(Status.NO_CONTENT.getStatusCode());

        Account accountAfterDeposit = given().when().contentType(ContentType.JSON)
                .get("/accounts/{accountId}", 1L)
                .then()
                .statusCode(Status.OK.getStatusCode())
                .extract()
                .as(Account.class);

        assertThat(accountAfterDeposit.getBalance(), is(BigDecimal.valueOf(20)));
    }

    @Test
    @Order(8)
    void depositFund_shouldReturn404_whenAccountNotExist() {
        given().when()
                .contentType(ContentType.JSON)
                .put("/accounts/deposit/{accountId}/{amount}", -1L, BigDecimal.valueOf(10))
                .then()
                .statusCode(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @Order(9)
    void withdrawFund_shouldReturn204_whenAccountUpdated() {
        given().when()
                .contentType(ContentType.JSON)
                .put("/accounts/withdraw/{accountId}/{amount}", 1L, BigDecimal.valueOf(10))
                .then()
                .statusCode(Status.NO_CONTENT.getStatusCode());

        Account accountAfterDeposit = given().when().contentType(ContentType.JSON)
                .get("/accounts/{accountId}", 1L)
                .then()
                .statusCode(Status.OK.getStatusCode())
                .extract()
                .as(Account.class);

        assertThat(accountAfterDeposit.getBalance(), is(BigDecimal.valueOf(0)));
    }

    @Test
    @Order(10)
    void withdrawFund_shouldReturn404_whenAccountNotExist() {
        given().when()
                .contentType(ContentType.JSON)
                .put("/accounts/withdraw/{accountId}/{amount}", -1L, BigDecimal.valueOf(10))
                .then()
                .statusCode(Status.NOT_FOUND.getStatusCode());
    }
}
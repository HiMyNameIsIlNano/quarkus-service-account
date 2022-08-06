package com.himynameisilnano.account.boundary;

import com.himynameisilnano.account.entity.Account;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static javax.ws.rs.core.Response.Status;
import static javax.ws.rs.core.Response.status;

@Path("/accounts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    private Set<Account> accounts;

    @PostConstruct
    public void initialize() {
        this.accounts = new HashSet<>(
                Arrays.asList(
                        new Account(1L, 1L, "Cookie Monster", BigDecimal.valueOf(10)),
                        new Account(2L, 2L, "Count von Count", BigDecimal.valueOf(20)),
                        new Account(3L, 3L, "Elmo", BigDecimal.valueOf(30))
                )
        );
    }

    @GET
    public Set<Account> allAccounts() {
        return accounts;
    }

    @POST
    @Path("/restore")
    public Response restore() {
        initialize();

        return status(Status.OK).build();
    }

    @GET
    @Path("/{accountId}")
    public Account getAccount(@PathParam("accountId") Long accountId) {
        return accounts.stream()
                .filter(account -> account.getAccountNumber().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new WebApplicationException("Account with id " + accountId + " does not exist."));
    }

    @POST
    public Response saveAccount(Account account) {
        boolean add = accounts.add(account);
        if (!add) {
            return status(Status.NOT_MODIFIED).build();
        }

        Optional<Account> result = accounts.stream()
                .filter(a -> Objects.equals(a, account))
                .findFirst();

        return status(Status.CREATED)
                .entity(result)
                .build();
    }

    @PUT
    @Path("/deposit/{accountId}/{amount}")
    public Response depositFund(@PathParam("accountId") Long accountId, @PathParam("amount") BigDecimal amount) {
        Optional<Account> accountToUpdateOptional = accounts.stream()
                .filter(account -> Objects.equals(account.getAccountNumber(), accountId))
                .findFirst();

        if (accountToUpdateOptional.isEmpty()) {
            return status(Status.NOT_FOUND).build();
        }

        accountToUpdateOptional.get()
                .addFunds(amount);

        return status(Status.NO_CONTENT)
                .build();
    }

    @PUT
    @Path("/withdraw/{accountId}/{amount}")
    public Response withdrawFund(@PathParam("accountId") Long accountId, @PathParam("amount") BigDecimal amount) {
        Optional<Account> accountToUpdateOptional = accounts.stream()
                .filter(account -> Objects.equals(account.getAccountNumber(), accountId))
                .findFirst();

        if (accountToUpdateOptional.isEmpty()) {
            return status(Status.NOT_FOUND).build();
        }

        accountToUpdateOptional.get()
                .addFunds(amount.negate());

        return status(Status.NO_CONTENT)
                .build();
    }

    @DELETE
    @Path("/{accountId}")
    public Response deleteAccount(@PathParam("accountId") Long accountId) {
        Optional<Account> accountToRemoveOptional = accounts.stream()
                .filter(account -> Objects.equals(account.getAccountNumber(), accountId))
                .findFirst();

        if (accountToRemoveOptional.isEmpty()) {
            return status(Status.NOT_FOUND).build();
        }

        Account accountToRemove = accountToRemoveOptional.get();
        accounts.remove(accountToRemove);
        return status(Status.OK)
                .entity(accountToRemove)
                .build();
    }

}
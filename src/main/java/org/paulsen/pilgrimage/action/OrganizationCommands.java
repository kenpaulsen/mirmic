package org.paulsen.pilgrimage.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.paulsen.pilgrimage.dynamo.DAO;
import org.paulsen.pilgrimage.model.Organization;

@Slf4j
@Named("org")
@ApplicationScoped
public class OrganizationCommands {
    private static final long TIMEOUT = 5_000;

    public Organization create() {
        return Organization.builder().build();
    }

    public Organization get(final String id) {
        return optionalWaitOrElse(DAO.getInstance().getOrgDao().get(id), () -> null);
    }

    public Boolean save(final Organization org) {
        try {
            return waitOrElse(DAO.getInstance().getOrgDao().save(org), () -> false);
        } catch (final IOException ex) {
            // FIXME
            ex.printStackTrace();
            return false;
        }
    }

    public List<Organization> list() {
        return waitOrElse(DAO.getInstance().getOrgDao().list(), List::of);
    }

    private <R> R waitOrElse(final CompletableFuture<R> future, final Supplier<R> orElse) {
        try {
            final R result = future.get(TIMEOUT, TimeUnit.MILLISECONDS);
            return result == null ? orElse.get() : result;
        } catch (final ExecutionException | InterruptedException | TimeoutException ex) {
            // FIXME:
            ex.printStackTrace();
            return orElse.get();
        }
    }

    private <R> R optionalWaitOrElse(final CompletableFuture<Optional<R>> future, final Supplier<R> orElse) {
        try {
            return future.get(TIMEOUT, TimeUnit.MILLISECONDS).orElse(orElse.get());
        } catch (final ExecutionException | InterruptedException | TimeoutException ex) {
            // FIXME:
            ex.printStackTrace();
            return orElse.get();
        }
    }
}

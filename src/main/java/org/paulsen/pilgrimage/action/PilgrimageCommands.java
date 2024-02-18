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
import org.paulsen.pilgrimage.model.Pilgrimage;

@Slf4j
@Named("org")
@ApplicationScoped
public class PilgrimageCommands {
    private static final long TIMEOUT = 5_000;

    public Pilgrimage create() {
        return Pilgrimage.builder().build();
    }

    public Pilgrimage get(final String id) {
        return optionalWaitOrElse(DAO.getInstance().getPilgrimageDao().get(id), () -> null);
    }

    public Boolean save(final Pilgrimage org) {
        try {
            return waitOrElse(DAO.getInstance().getPilgrimageDao().save(org), () -> false);
        } catch (final IOException ex) {
            // FIXME
            ex.printStackTrace();
            return false;
        }
    }

    public List<Pilgrimage> list() {
        return waitOrElse(DAO.getInstance().getPilgrimageDao().list(), List::of);
    }

    // FIXME: extra common code w/ OrganizationCommands
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

    // FIXME: extra common code w/ OrganizationCommands
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

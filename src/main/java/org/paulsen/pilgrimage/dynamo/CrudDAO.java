package org.paulsen.pilgrimage.dynamo;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.paulsen.pilgrimage.model.BasicModel;
import org.paulsen.pilgrimage.model.Organization;

// FIXME: What should BasicModel<T> be? Is T correct?
public interface CrudDAO<T extends BasicModel<T>> {
    void clearCache();
    CompletableFuture<Boolean> save(final T org) throws IOException;
    CompletableFuture<Optional<T>> get(final String id);
    CompletableFuture<List<T>> list();
}

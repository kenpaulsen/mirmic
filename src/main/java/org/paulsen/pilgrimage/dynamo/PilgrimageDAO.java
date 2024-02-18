package org.paulsen.pilgrimage.dynamo;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.paulsen.pilgrimage.model.Pilgrimage;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Slf4j
// FIXME: should there be a DAO interface or base class? or is "persistence" good enough? I think there is common
// patterns for using CRUD that we could push to a base class maybe? That doesn't really belong in Persistence.
public class PilgrimageDAO implements CrudDAO<Pilgrimage> {
    private static final String ID = "id";
    private static final String CONTENT = "content";
    private static final String TABLE = "pilgrimages";

    private final Map<String, Pilgrimage> cache = new ConcurrentHashMap<>();
    private final ObjectMapper mapper;
    private final Persistence persistence;

    protected PilgrimageDAO(final ObjectMapper mapper, final Persistence persistence) {
        this.mapper = mapper;
        this.persistence = persistence;
    }

    public void clearCache() {
        cache.clear();
    }

    // FIXME: extra common code with OrganizationDao
    @Override
    public CompletableFuture<Boolean> save(final Pilgrimage pilgrimage) throws IOException {
        final Map<String, AttributeValue> map = new HashMap<>();
        map.put(ID, persistence.toStrAttr(pilgrimage.getId()));
        map.put(CONTENT, persistence.toStrAttr(mapper.writeValueAsString(pilgrimage)));
        return persistence.putItem(b -> b.tableName(TABLE).item(map))
            .thenApply(resp -> resp.sdkHttpResponse().isSuccessful())
            .thenApply(r -> r ? persistence.cacheOne(cache, pilgrimage, pilgrimage.getId(), true) :
                persistence.clearCache(cache, false))
            .exceptionally(ex -> {
                log.error("Failed to save pilgrimage!", ex);
                return false;
            });
    }

    // FIXME: extra common code with OrganizationDao
    @Override
    public CompletableFuture<Optional<Pilgrimage>> get(final String id) {
        final Pilgrimage pilgrimage = cache.get(id);
        if (pilgrimage != null) {
            return CompletableFuture.completedFuture(Optional.of(pilgrimage));
        }
        return list().thenApply(na -> Optional.ofNullable(cache.get(id))); // Load all the pilgrimage
    }

    // FIXME: extra common code with OrganizationDao
    @Override
    public CompletableFuture<List<Pilgrimage>> list() {
        if (!cache.isEmpty()) {
            // FIXME: Can we push this to a base class? "persistence" serves this purpose.. is it good enough?
            // FIXME: Can we do better at caching and not sort every time we get a list?
            return CompletableFuture.completedFuture(cache.values().stream()
                .sorted(Comparator.comparing(Pilgrimage::getStartDate)).toList());
        }
        // FIXME: Can we support more than 1000?
        return persistence.scan(b -> b.consistentRead(false).limit(1000).tableName(TABLE).build())
            .thenApply(resp -> resp.items().stream()
                .map(it -> toPilgrimage(it.get(CONTENT)))
                .sorted(Comparator.comparing(Pilgrimage::getStartDate))
                .toList())
            .thenApply(list -> persistence.cacheAll(cache, list, Pilgrimage::getId));
    }

    private Pilgrimage toPilgrimage(final AttributeValue content) {
        if (content == null) {
            return null;
        }
        try {
            return mapper.readValue(content.s(), Pilgrimage.class);
        } catch (final IOException ex) {
            log.error("Unable to parse pilgrimage record: " + content, ex);
            return null;
        }
    }
}

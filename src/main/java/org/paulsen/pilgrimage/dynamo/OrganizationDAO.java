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
import org.paulsen.pilgrimage.model.Organization;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Slf4j
// FIXME: should there be a DAO interface or base class? or is "persistence" good enough? I think there is common
// patterns for using CRUD that we could push to a base class maybe? That doesn't really belong in Persistence.
// FIXME: Can we change this to something like: public class OrganizationDAO<T> where T is the object
//        to get? T implements an interface for sorting, id, etc.
public class OrganizationDAO implements CrudDAO<Organization> {
    private static final String ID = "id";
    private static final String CONTENT = "content";
    private static final String TABLE = "organizations";

    private final Map<String, Organization> cache = new ConcurrentHashMap<>();
    private final ObjectMapper mapper;
    private final Persistence persistence;

    protected OrganizationDAO(final ObjectMapper mapper, final Persistence persistence) {
        this.mapper = mapper;
        this.persistence = persistence;
    }

    @Override
    public void clearCache() {
        cache.clear();
    }

    // FIXME: was protected... do we want this public? Prob not... make CrudDAO an abstract class instead of interface
    @Override
    public CompletableFuture<Boolean> save(final Organization org) throws IOException {
        final Map<String, AttributeValue> map = new HashMap<>();
        map.put(ID, persistence.toStrAttr(org.getId()));
        map.put(CONTENT, persistence.toStrAttr(mapper.writeValueAsString(org)));
        return persistence.putItem(b -> b.tableName(TABLE).item(map))
            .thenApply(resp -> resp.sdkHttpResponse().isSuccessful())
            .thenApply(r -> r ? persistence.cacheOne(cache, org, org.getId(), true) :
                persistence.clearCache(cache, false))
            .exceptionally(ex -> {
                log.error("Failed to save org!", ex);
                return false;
            });
    }

    @Override
    public CompletableFuture<Optional<Organization>> get(final String id) {
        final Organization org = cache.get(id);
        if (org != null) {
            return CompletableFuture.completedFuture(Optional.of(org));
        }
        return list().thenApply(na -> Optional.ofNullable(cache.get(id))); // Load all the organization
    }

    @Override
    public CompletableFuture<List<Organization>> list() {
        if (!cache.isEmpty()) {
            // FIXME: Can we push this to a base class? "persistence" serves this purpose.. is it good enough?
            // FIXME: Can we do better at caching and not sort every time we get a list?
            return CompletableFuture.completedFuture(cache.values().stream()
                .sorted(Comparator.comparing(Organization::getName)).toList());
        }
        // FIXME: Can we support more than 1000?
        return persistence.scan(b -> b.consistentRead(false).limit(1000).tableName(TABLE).build())
            .thenApply(resp -> resp.items().stream()
                .map(it -> toOrganization(it.get(CONTENT)))
                .sorted(Comparator.comparing(Organization::getName))
                .toList())
            .thenApply(list -> persistence.cacheAll(cache, list, Organization::getId));
    }

     Organization toOrganization(final AttributeValue content) {
        if (content == null) {
            return null;
        }
        try {
            return mapper.readValue(content.s(), Organization.class);
        } catch (final IOException ex) {
            log.error("Unable to parse organization record: " + content, ex);
            return null;
        }
    }
}

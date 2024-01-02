package org.paulsen.trip.dynamo;

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
import org.paulsen.trip.model.Pilgrimage;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Slf4j
public class PilgrimageDAO {
    private final Map<String, Pilgrimage> tripCache = new ConcurrentHashMap<>();
    private final ObjectMapper mapper;
    private final Persistence persistence;

    protected PilgrimageDAO(final ObjectMapper mapper, final Persistence persistence) {
        this.mapper = mapper;
        this.persistence = persistence;
    }

    public void clearCache() {
        tripCache.clear();
    }

/*
    private Trip toTrip(final AttributeValue content) {
        if (content == null) {
            return null;
        }
        try {
            return mapper.readValue(content.s(), Trip.class);
        } catch (final IOException ex) {
            log.error("Unable to parse trip record: " + content, ex);
            return null;
        }
    }
    */
}

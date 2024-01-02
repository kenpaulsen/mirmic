package org.paulsen.trip.dynamo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DAO {
    @Getter
    private final ObjectMapper mapper;
    private final PilgrimageDAO pilgrimageDAO;

    // This flag is set in the web.xml
    private static final DAO INSTANCE = new DAO();

    private DAO() {
        final Persistence persistence = createTripPersistence();
        this.mapper = createObjectMapper();
        this.pilgrimageDAO = new PilgrimageDAO(mapper, persistence);
        //FakeData.addFakeData(personDao, tripDao);
    }

    public static DAO getInstance() {
        return INSTANCE;
    }

    /* People
    public CompletableFuture<Boolean> savePerson(final Person person) throws IOException {
        return personDao.savePerson(person);
    }
    public CompletableFuture<List<Person>> getPeople() {
        return personDao.getPeople();
    }
    public CompletableFuture<Optional<Person>> getPerson(final Person.Id id) {
        return personDao.getPerson(id);
    }
    public CompletableFuture<Person> getPersonByEmail(final String email) {
        return personDao.getPersonByEmail(email);
    }

    // Trips
    public CompletableFuture<Boolean> saveTrip(final Trip trip) throws IOException {
        return tripDao.saveTrip(trip);
    }
    public CompletableFuture<Optional<Trip>> getTrip(final String id) {
        return tripDao.getTrip(id);
    }
    public CompletableFuture<List<Trip>> getTrips() {
        return tripDao.getTrips();
    }
    */

    /* Package-private for testing */
    public void clearAllCaches() {
        pilgrimageDAO.clearCache();
    }

    private ObjectMapper createObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    private Persistence createTripPersistence() {
        final Persistence result;
        if (FakeData.isLocal()) {
            // Local development only -- don't talk to dynamo
            result = FakeData.createFakePersistence();
        } else {
            // The real deal
            result = new DynamoPersistence();
        }
        return result;
    }
}

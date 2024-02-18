package org.paulsen.pilgrimage.dynamo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.paulsen.pilgrimage.model.Organization;
import org.paulsen.pilgrimage.model.Pilgrimage;

@Slf4j
public class DAO {
    @Getter
    private final ObjectMapper mapper;
    private final OrganizationDAO orgDao;
    private final PilgrimageDAO pilgrimageDao;

    // This flag is set in the web.xml
    private static final DAO INSTANCE = new DAO();

    private DAO() {
        final Persistence persistence = createPersistence();
        this.mapper = createObjectMapper();
        this.pilgrimageDao = new PilgrimageDAO(mapper, persistence);
        this.orgDao = new OrganizationDAO(mapper, persistence);
        // FIXME: TBD? Or remove/
        //FakeData.addFakeData(personDao, tripDao);
    }

    public static DAO getInstance() {
        return INSTANCE;
    }

    public CrudDAO<Organization> getOrgDao() {
        return orgDao;
    }
    public CrudDAO<Pilgrimage> getPilgrimageDao() {
        return pilgrimageDao;
    }

    /*
    // Pilgrimages
    public CompletableFuture<Boolean> savePilgrimage(final Pilgrimage pilgrimage) throws IOException {
        return pilgrimageDao.save(pilgrimage);
    }
    public CompletableFuture<Optional<Pilgrimage>> getTrip(final String id) {
        return pilgrimageDao.get(id);
    }
    public CompletableFuture<List<Pilgrimage>> getTrips() {
        return pilgrimageDao.list();
    }
     */

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

    */

    /* Package-private for testing */
    public void clearAllCaches() {
        pilgrimageDao.clearCache();
    }

    private ObjectMapper createObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    private Persistence createPersistence() {
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

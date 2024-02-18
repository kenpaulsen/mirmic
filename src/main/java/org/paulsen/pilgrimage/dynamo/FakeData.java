package org.paulsen.pilgrimage.dynamo;

import jakarta.faces.context.FacesContext;
import jakarta.servlet.ServletContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import lombok.Getter;
import org.paulsen.pilgrimage.model.Pilgrimage;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

public class FakeData {
    private static final String LOCAL = "local";
    private static final String FACES_SERVLET = "Faces Servlet";

    /*
    @Getter
    private static final List<Person> fakePeople = initFakePeople();
    */
    @Getter
    private static final List<Pilgrimage> fakeTrips = initFakePilgrimages();

    public static boolean isLocal() {
        // fc will be null in a test environment that doesn't full start the server w/ JSF installed.
        final FacesContext fc = FacesContext.getCurrentInstance();
        return (fc == null) || "true".equals(((ServletContext) fc.getExternalContext().getContext())
                .getServletRegistration(FACES_SERVLET).getInitParameter(LOCAL));
    }

    public static Persistence createFakePersistence() {
        return new Persistence() { };
    }

    public static Persistence createFakePersistenceWithQueryMonitor(
            final Consumer<Consumer<QueryRequest.Builder>> whenQueryCalled) {
        return new Persistence() {
            @Override
            public CompletableFuture<QueryResponse> query(Consumer<QueryRequest.Builder> queryRequest) {
                whenQueryCalled.accept(queryRequest);
                return CompletableFuture.completedFuture(QueryResponse.builder().items(new ArrayList<>()).build());
            }
        };
    }

/*
    public static void addFakeData(final PersonDAO personDao, final TripDAO tripDao) {
        if (isLocal()) {
            // Setup some sample data
            FakeData.getFakePeople().forEach(p -> {
                try {
                    personDao.savePerson(p);
                } catch (IOException ex) {
                    throw new IllegalStateException("Should have worked...");
                }
            });
            FakeData.getFakeTrips().forEach(t -> {
                try {
                    tripDao.saveTrip(t);
                } catch (IOException ex) {
                    throw new IllegalStateException("Should have worked...");
                }
            });
        }
    }

    static List<Person> initFakePeople() {
        final List<Person> people = new ArrayList<>();
        people.add(new Person(null, "Joe", "Joseph", "Bob", "Smith", Sex.Male,
                LocalDate.of(1947, 2, 11), null, "user1", null, null, null, null, null, null, null));
        people.add(Person.builder()
                .id(Person.Id.from("admin"))
                .first("admin")
                .last("user")
                .email("admin")
                .build());
        people.add(new Person(null, "Ken", "Kenneth", "", "Paulsen", Sex.Male,
                LocalDate.of(1977, 12, 11), null, "user2", null, null, null, null, null, null, null));
        people.add(new Person(null, null, "Kevin", "David", "Paulsen", Sex.Male,
                LocalDate.of(1987, 9, 27), null,"user3", null, null, null, null, null, null, null));
        people.add(new Person(null, "Trinity", "Trinity", "Anne", "Paulsen", Sex.Female,
                LocalDate.of(1979, 12, 11), null, "user4", null, null, null, null, null, null, null));
        people.add(new Person(null, "Dave", "David", "A", "Robinson", Sex.Male,
                LocalDate.of(1999, 1, 30), null, "user5", null, null, null, null, null, null, null));
        people.add(new Person(null, "Matt", "Matthew", null, "Smith", Sex.Male,
                LocalDate.of(2010, 6, 1), null, "user6", null, null, null, null, null, null, null));
        return people;
    }
    */

    static List<Pilgrimage> initFakePilgrimages() {
        // Pilgrimage 1
        final List<Pilgrimage> trips = new ArrayList<>();
        trips.add(Pilgrimage.builder()
            .id("faketrip")
            .title("Spring Demo Trip")
            .description("desc")
            .startDate(LocalDateTime.now().plusDays(48))
            .endDate(LocalDateTime.now().plusDays(60))
            .build());

        // Pilgrimage 2
        trips.add(Pilgrimage.builder()
            .id("Fake2")
            .title("Summer Demo Trip")
            .description("Trip Description")
            .startDate(LocalDateTime.now().minusDays(4))
            .endDate(LocalDateTime.now().plusDays(7))
            .build());
        return trips;
    }

    /*
    static List<RegistrationOption> getDefaultOptions() {
        final List<RegistrationOption> result = new ArrayList<>();
        result.add(new RegistrationOption(1, "Room Preference:",
                "Private room ($15 more per night) or shared?", true));
        result.add(new RegistrationOption(2, "Roommate request?",
                "If you are sharing a room, do you have someone in mind?", true));
        result.add(new RegistrationOption(3, "Preferred Departure Airport?",
                "What airport would you like to leave from?", true));
        result.add(new RegistrationOption(4, "Trip Insurance?",
                "Price is will be paid directly to insurance company, typically $100+.", true));
        result.add(new RegistrationOption(6, "Portugal excursion?",
                "Those interested will visit Fatima before the main pilgrimage.", true));
        result.add(new RegistrationOption(5, "Check luggage?",
                "Will you need to check luggage?", true));
        result.add(new RegistrationOption(7, "Special Requests?",
                "Do you have any special requests for this pilgrimage?", true));
        result.add(new RegistrationOption(8, "Agree to Terms?",
                "Type your full name to agree.", true));
        return result;
    }

    static Map<String, AttributeValue> getTestUserCreds(final GetItemRequest giReq) {
        final AttributeValue email = giReq.key().get(CredentialsDAO.EMAIL);
        final AttributeValue lowEmail = email.toBuilder().s(email.s().toLowerCase(Locale.getDefault())).build();
        final AttributeValue priv;
        if (lowEmail.s().startsWith("admin")) {
            priv = AttributeValue.builder().s("admin").build();
        } else if (lowEmail.s().startsWith("user")) {
            priv = AttributeValue.builder().s("user").build();
        } else {
            // Not authorized
            return null;
        }
        final Map<String, AttributeValue> attrs = new HashMap<>();
        attrs.put(CredentialsDAO.EMAIL, lowEmail);
        final AttributeValue userId = DAO.getInstance().getPeople().join().stream()
                .filter(person -> lowEmail.s().equalsIgnoreCase(person.getEmail())).findAny()
                .map(Person::getId).map(id -> AttributeValue.builder().s(id.getValue()).build())
                .orElse(lowEmail);
        attrs.put(CredentialsDAO.USER_ID, userId);
        attrs.put(CredentialsDAO.PRIV, priv);
        attrs.put(CredentialsDAO.PW, priv);
        attrs.put(CredentialsDAO.LAST_LOGIN,
                AttributeValue.builder().n("" + (System.currentTimeMillis() - 86_400_000L)).build());
        return attrs;
    }
    */
}

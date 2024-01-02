package org.paulsen.trip.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public final class Pilgrimage implements Serializable {
    // FIXME: move organization to it's own entity and refer to it by id
    private String organizationName;        // Organization Name
    private String organizationLink;        // Link to Organization
    private String organizationPicture;     // Organization Picture (url)
    private String tripId;                  // Trip ID
    private String tripPhone;               // Phone number for more information
    private String tripEmail;               // Email for more information
    private String tripTitle;               // Title of trip
    private String tripPicture;             // Picture for this trip (url)
    private String description;             // Short trip description
    private String fullCost;                // Cost with air + ground
    private String groundCost;              // Cost for ground-only (no air)
    private String location;                // City where trip originates
    private boolean otherLocationOk;        // True if other locations can join
    private LocalDateTime startDate;        // Start of trip
    private LocalDateTime endDate;          // End of trip

/*
    @lombok.Builder(builderClassName = "Builder")
    public Pilgrimage(
            @JsonProperty("id") String id,
            @JsonProperty("title") String title,
            @JsonProperty("openToPublic") Boolean openToPublic,
            @JsonProperty("description") String description,
            @JsonProperty("startDate") LocalDateTime startDate,
            @JsonProperty("endDate") LocalDateTime endDate,
            @JsonProperty("people") List<Person.Id> people,
            @JsonProperty("tripEvents") List<TripEvent> tripEvents,
            @JsonProperty("regOptions") List<RegistrationOption> regOptions) {
        this.id = id;
        this.title = title;
        this.openToPublic = openToPublic == null || openToPublic;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Pilgrimage() {
        this(UUID.randomUUID().toString(), null, null, null, LocalDateTime.now().plusDays(60),
                LocalDateTime.now().plusDays(70), null, null, null);
    }
    */

    /**
     * Returns {@code true} if the the given person can join this Trip. This requires the Person to not already be on
     * the trip, and for the trip to not yet be started.
    public boolean canJoin(final Person.Id userId) {
        return !people.contains(userId) && startDate.isAfter(LocalDateTime.now());
    }

    public String addTripEvent(final String title, final String notes, final LocalDateTime date) {
        // Very simple validation check...
        tripEvents.stream().filter(te -> matchingTE(te, title, date)).findAny().ifPresent(te -> {
            throw new IllegalStateException(
                    "Trip Event with title (" + title + ") and date (" + date + ") already exists!");
        });
        // Add it
        final String id = UUID.randomUUID().toString();
        tripEvents.add(new TripEvent(id, title, notes, date, null, null));
        tripEvents.sort(Comparator.comparing(TripEvent::getStart));
        return id;
    }

    @JsonIgnore
    public TripEvent getTripEvent(final String teId) {
        return tripEvents.stream().filter(e -> e.getId().equals(teId)).findAny().orElse(null);
    }

    @JsonIgnore
    public List<TripEvent> getTripEventsForUser(final Person.Id userId) {
        return tripEvents.stream().filter(te -> te.getParticipants().contains(userId)).toList();
    }

    @JsonIgnore
    public void deleteTripEvent(final TripEvent te) {
        tripEvents.remove(te);
    }

    public void editTripEvent(final TripEvent newTE) {
        // Ensure we have the TripEvent to edit
        final TripEvent oldTE = tripEvents.stream().filter(e -> e.getId().equals(newTE.getId())).findAny()
                .orElseThrow(() -> new IllegalArgumentException("TripEvent id (" + newTE.getId() + ") not found!"));
        tripEvents.remove(oldTE);
        tripEvents.add(newTE);
        tripEvents.sort(Comparator.comparing(TripEvent::getStart));
    }

    public void addTripOption() {
        regOptions.add(new RegistrationOption(regOptions.size(), "New Option", "New Option Description", false));
    }

    private boolean matchingTE(final TripEvent te, final String title, final LocalDateTime date) {
        return title.equals(te.getTitle()) && date.equals(te.getStart());
    }
     */
}

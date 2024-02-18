package org.paulsen.pilgrimage.model;

import java.util.Comparator;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class Organization implements BasicModel<Organization> {
    @Builder.Default
    private final String id = UUID.randomUUID().toString();     // ID
    private String name;            // Organization Name
    private String description;     // Short pilgrimage description
    private String link;            // Link to Organization
    private String picture;         // Organization Picture (url)
    private String phone;           // Phone number for more information
    private String email;           // Email for more information

    @Override
    public Class<Organization> type() {
        return Organization.class;
    }

    /*
    @Override
    public Comparator<Organization> defaultSorter() {
    }
     */
}

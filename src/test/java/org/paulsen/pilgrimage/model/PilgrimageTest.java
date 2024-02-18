package org.paulsen.pilgrimage.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDateTime;
import org.paulsen.pilgrimage.dynamo.DAO;
import org.paulsen.pilgrimage.util.RandomData;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PilgrimageTest {
    private final ObjectMapper mapper = DAO.getInstance().getMapper();

    @Test
    public void testGetTripId() {
        final Pilgrimage defId = Pilgrimage.builder().build();
        Assert.assertNotNull(defId.getId());
        Assert.assertTrue(defId.getId().length() > 5);
        final String id = RandomData.genAlpha(15);
        final Pilgrimage specificId = Pilgrimage.builder().id(id).build();
        Assert.assertNotNull(specificId.getId());
        Assert.assertEquals(specificId.getId().length(), 15);
        Assert.assertEquals(specificId.getId(), id);
    }

    @Test
    public void testJsonSerialization() throws IOException {
        final String id = RandomData.genAlpha(10);
        final String desc = RandomData.genAlpha(100);
        final String fullCost = RandomData.genAlpha(20);
        final String groundCost = RandomData.genAlpha(12);
        final String loc = RandomData.genAlpha(8);
        final String orgId = RandomData.genAlpha(9);
        final String link = RandomData.genAlpha(15);
        final String email = RandomData.genAlpha(14);
        final String phone = RandomData.genAlpha(15);
        final String pic = RandomData.genAlpha(16);
        final String title = RandomData.genAlpha(17);
        final LocalDateTime start = LocalDateTime.now();
        final LocalDateTime end = LocalDateTime.now().plusDays(11);
        final Pilgrimage before = Pilgrimage.builder()
            .id(id)
            .description(desc)
            .startDate(start)
            .endDate(end)
            .fullCost(fullCost)
            .groundCost(groundCost)
            .departureLocation(loc)
            .organizationId(orgId)
            .otherDepartureLocationOk(true)
            .link(link)
            .email(email)
            .phone(phone)
            .picture(pic)
            .title(title)
            .build();
        final String str = mapper.writeValueAsString(before);
        Assert.assertTrue(str.contains(id));
        final Pilgrimage after = mapper.readValue(str, Pilgrimage.class);
        Assert.assertEquals(after, before);
    }
}
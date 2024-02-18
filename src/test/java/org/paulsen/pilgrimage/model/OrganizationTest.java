package org.paulsen.pilgrimage.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.paulsen.pilgrimage.dynamo.DAO;
import org.paulsen.pilgrimage.util.RandomData;
import org.testng.Assert;
import org.testng.annotations.Test;

public class OrganizationTest {
    private final ObjectMapper mapper = DAO.getInstance().getMapper();

    @Test
    public void testGetId() {
        final Organization defId = Organization.builder().build();
        Assert.assertNotNull(defId.getId());
        Assert.assertTrue(defId.getId().length() > 5);
        final String id = RandomData.genAlpha(15);
        final Organization specificId = Organization.builder().id(id).build();
        Assert.assertNotNull(specificId.getId());
        Assert.assertEquals(specificId.getId().length(), 15);
        Assert.assertEquals(specificId.getId(), id);
    }

    @Test
    public void testJsonSerialization() throws IOException {
        final String id = RandomData.genAlpha(10);
        final String name = RandomData.genAlpha(17);
        final String desc = RandomData.genAlpha(100);
        final String email = RandomData.genAlpha(14);
        final String phone = RandomData.genAlpha(15);
        final String pic = RandomData.genAlpha(16);
        final String link = RandomData.genAlpha(17);
        final Organization before = Organization.builder()
            .id(id)
            .name(name)
            .description(desc)
            .email(email)
            .phone(phone)
            .picture(pic)
            .link(link)
            .build();
        final String str = mapper.writeValueAsString(before);
        Assert.assertTrue(str.contains(id));
        final Organization after = mapper.readValue(str, Organization.class);
        Assert.assertEquals(after, before);
    }
}
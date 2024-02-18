package org.paulsen.pilgrimage.action;

import org.paulsen.pilgrimage.model.Organization;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

public class OrganizationCommandsTest {
    @Test
    void canCreateOrg() {
        final Organization org = new OrganizationCommands().create();
        assertNotNull(org);
        assertNotNull(org.getId());
    }

    // FIXME: write tests for save / get / list
    // FIXME: after you do that, create the same for pilgrimages
    // FIXME: after you do that, start creating some pages that create and show this stuff
}
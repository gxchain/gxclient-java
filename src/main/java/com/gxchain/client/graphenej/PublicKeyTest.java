package com.gxchain.client.graphenej;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by nelson on 12/16/16.
 */
public class PublicKeyTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(PublicKeyTest.class);
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void equals() throws Exception {
        Address address1 = new Address("BTS8RiFgs8HkcVPVobHLKEv6yL3iXcC9SWjbPVS15dDAXLG9GYhnY");
        Address address2 = new Address("BTS8RiFgs8HkcVPVobHLKEv6yL3iXcC9SWjbPVS15dDAXLG9GYhnY");
        Address address3 = new Address("BTS8RiFgs8HkcVPVobHLKEv6yL3iXcC9SWjbPVS15dDAXLG9GYp00");
        PublicKey pk1 = address1.getPublicKey();
        PublicKey pk2 = address2.getPublicKey();
        PublicKey pk3 = address3.getPublicKey();
        assertEquals("Public keys must be equal", pk1, pk2);
        assertNotEquals("Public keys must not be equal", pk1, pk3);
    }

    @Test
    public void testGxc() throws Exception {
        Address address1 = new Address("GXC5Yu6M75wt1HP87wpqqPrrNDANFMkgvA9djiT8N73D6Rq7zNraQ");
        PublicKey pk1 = address1.getPublicKey();
        LOGGER.info(pk1.toString());
    }

    @After
    public void tearDown() throws Exception {

    }

}

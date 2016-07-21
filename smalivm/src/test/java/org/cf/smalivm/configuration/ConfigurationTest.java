package org.cf.smalivm.configuration;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigurationTest {

    private Configuration configuration;

    @Before
    public void setUp() {
        configuration = Configuration.instance();
    }

    @Test
    public void testStringIsImmutable() {
        assertTrue(configuration.isImmutable("Ljava/lang/String;"));
    }

    @Test
    public void testStringArrayIsImmutable() {
        assertFalse(configuration.isImmutable("[Ljava/lang/String;"));
    }

    @Test
    public void testStringBuilderIsImmutable() {
        assertFalse(configuration.isImmutable("Ljava/lang/StringBuilder;"));
    }

}

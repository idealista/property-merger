package com.idealista.configuration.combiner.properties;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.PropertiesConfigurationLayout;
import org.junit.Before;
import org.junit.Test;

import com.idealista.configuration.combiner.ConfigurationMerger;
import com.idealista.configuration.combiner.MergeOptions;

import static org.junit.Assert.*;

public class ConfigurationMergerTest {

    private ConfigurationMerger configurationMerger;
    
    @Before
    public void setUp() {
        this.configurationMerger = new ConfigurationMerger();
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testWhenMergeWithoutBaseConfigurationNeitherOtherConfigurationNeitherMergeOptions_ShouldThrowException() {
        configurationMerger.merge(null, null, null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testWhenMergeWithoutOtherConfigurationNeitherMergeOptions_ShouldThrowException() {
        configurationMerger.merge(new PropertiesConfiguration(), null, null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testWhenMergeWithoutBaseConfigurationNeitherMergeOptions_ShouldThrowException() {
        configurationMerger.merge(null, new PropertiesConfiguration(), null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testWhenMergeWithoutMergeOptions_ShouldThrowException() {
        configurationMerger.merge(new PropertiesConfiguration(), new PropertiesConfiguration(), null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testWhenMergeWithoutBaseConfigurationNeitherOtherConfigurationButMergeOptions_ShouldThrowException() {
        configurationMerger.merge(null, null, defaultMergeOptions());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testWhenMergeWithoutOtherConfigurationButMergeOptions_ShouldThrowException() {
        configurationMerger.merge(new PropertiesConfiguration(), null, defaultMergeOptions());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testWhenMergeWithoutBaseConfigurationButMergeOptions_ShouldThrowException() {
        configurationMerger.merge(null, new PropertiesConfiguration(), defaultMergeOptions());
    }
    
    @Test
    public void testWhenMergeEmtpyPropertiesConfiguration_ShouldReturnEmptyConfiguration() {
        PropertiesConfiguration mergeResult = configurationMerger.merge(new PropertiesConfiguration(), new PropertiesConfiguration(), defaultMergeOptions());
        assertTrue(mergeResult.isEmpty());
    }
        
    @Test
    public void testWhenMergeConfigurationWithDefaultMergeOptions_ShouldReturnSameAsBaseConfiguration() {
        PropertiesConfiguration baseConfiguration = baseConfiguration();        
        PropertiesConfiguration otherConfiguration = otherConfiguration();

        Set<String> baseKeys = new HashSet<>(baseConfiguration.getLayout().getKeys());
        
        PropertiesConfiguration mergeResult = configurationMerger.merge(baseConfiguration, otherConfiguration, defaultMergeOptions());
        
        assertEquals(baseConfiguration.getLayout(), mergeResult.getLayout());
        assertEquals(baseConfiguration.size(), mergeResult.size());
        
        // Has all the properties present in baseConfiguration
        for(String baseKey : baseKeys) {
            assertEquals(baseConfiguration.getString(baseKey), mergeResult.getString(baseKey));
        }
    }
    
    @Test
    public void testWhenMergeConfigurationWithAddition_ShouldReturnBaseConfigurationAddingOtherConfigurationPropertiesWithDifferentKeys() {
        PropertiesConfiguration baseConfiguration = baseConfiguration();        
        PropertiesConfiguration otherConfiguration = otherConfiguration();

        Set<String> baseKeys = new HashSet<>(baseConfiguration.getLayout().getKeys());
        
        Set<String> differentKeys = new HashSet<>(otherConfiguration.getLayout().getKeys());
        differentKeys.removeAll(baseKeys);
        
        PropertiesConfiguration mergeResult = configurationMerger.merge(baseConfiguration, otherConfiguration, withAdditionMergeOptions());
        
        assertEquals(baseConfiguration.getLayout(), mergeResult.getLayout());
        assertEquals(baseKeys.size() + differentKeys.size(), mergeResult.size());
        
        // Has all the properties present in baseConfiguration 
        for(String baseKey : baseKeys) {
            assertEquals(baseConfiguration.getString(baseKey), mergeResult.getString(baseKey));
        }
        
        // Has new properties coming from otherConfiguration (with different key)
        for(String differentKey : differentKeys) {
            assertEquals(otherConfiguration.getString(differentKey), mergeResult.getString(differentKey));
        }
    }

    @Test
    public void testWhenMergeConfigurationWithModification_ShouldReturnBaseConfigurationWithOtherConfigValuesWhenKeysAreEquals() {
        PropertiesConfiguration baseConfiguration = baseConfiguration();
        PropertiesConfiguration otherConfiguration = otherConfiguration();

        Set<String> baseKeys = new HashSet<>(baseConfiguration.getLayout().getKeys());
        Set<String> otherKeys = new HashSet<>(otherConfiguration.getLayout().getKeys());

        Set<String> differentKeys = new HashSet<>(baseKeys);
        differentKeys.removeAll(otherKeys);

        Set<String> maintainedKeys = new HashSet<>(baseKeys);
        maintainedKeys.retainAll(otherKeys);

        PropertiesConfiguration mergeResult = configurationMerger.merge(baseConfiguration, otherConfiguration, withModificationMergeOptions());

        assertEquals(baseConfiguration.getLayout(), mergeResult.getLayout());
        assertEquals(baseKeys.size(), mergeResult.size());

        // Has all the properties present in baseConfiguration not present in otherConfiguration
        for(String differentKey : differentKeys) {
            assertEquals(baseConfiguration.getString(differentKey), mergeResult.getString(differentKey));
        }

        // Has values coming from otherConfiguration when key are equals
        for(String maintainedKey : maintainedKeys) {
            assertEquals(otherConfiguration.getString(maintainedKey), mergeResult.getString(maintainedKey));
        }
    }

    @Test
    public void testWhenMergeConfigurationWithDeletion_ShouldReturnBaseConfigurationDeletingPropertiesNotPresentInOtherConfigurationProperties() {
        PropertiesConfiguration baseConfiguration = baseConfiguration();
        PropertiesConfiguration otherConfiguration = otherConfiguration();

        Set<String> otherKeys = new HashSet<>(otherConfiguration.getLayout().getKeys());

        Set<String> maintainedKeys = new HashSet<>(baseConfiguration.getLayout().getKeys());
        maintainedKeys.retainAll(otherKeys);

        PropertiesConfiguration mergeResult = configurationMerger.merge(baseConfiguration, otherConfiguration, withDeletionMergeOptions());

        assertEquals(baseConfiguration.getLayout(), mergeResult.getLayout());
        assertEquals(maintainedKeys.size(), mergeResult.size());

        // Has all the properties present in baseConfiguration
        for(String maintainedKey : maintainedKeys) {
            assertEquals(baseConfiguration.getString(maintainedKey), mergeResult.getString(maintainedKey));
        }
    }

    @Test
    public void testWhenMergeConfigurationWithRemoveBaseEscapeAndModification_ShouldRemoveEscapesInTheMerge() {
        PropertiesConfiguration baseConfiguration = baseConfiguration();
        PropertiesConfiguration otherConfiguration = otherConfigurationWithEscapeChars();

        PropertiesConfiguration mergeResult = configurationMerger.merge(baseConfiguration, otherConfiguration, withModificationAndRemoveEscapesMergeOptions());

        assertNotEquals(otherConfiguration.getString("same.config.key"), mergeResult.getString("same.config.key"));
        assertEquals(mergeResult.getString("same.config.key"), otherConfiguration.getString("same.config.key").replace("\\",""));
    }

    private PropertiesConfiguration baseConfiguration() {
        PropertiesConfiguration baseConfiguration = new PropertiesConfiguration();
        baseConfiguration.setLayout(new PropertiesConfigurationLayout());

        baseConfiguration.setProperty("base.config.key", "baseFakeValue");
        baseConfiguration.setProperty("base.config.key.1", "baseFakeValue1");
        baseConfiguration.setProperty("base.config.key.2", "baseFakeValue2");
        baseConfiguration.setProperty("same.config.key", "sameValue");
        baseConfiguration.setProperty("same.config.key.1", "sameValue1");
        baseConfiguration.setProperty("same.config.key.2", "baseDifferentValue2");
        baseConfiguration.setProperty("same.config.key.3", "baseDifferentValue3");

        return baseConfiguration;
    }

    private PropertiesConfiguration otherConfiguration() {
        PropertiesConfiguration otherConfiguration = new PropertiesConfiguration();
        otherConfiguration.setLayout(new PropertiesConfigurationLayout());

        otherConfiguration.setProperty("other.config.key", "otherFakeValue");
        otherConfiguration.setProperty("other.config.key.1", "otherFakeValue1");
        otherConfiguration.setProperty("other.config.key.2", "otherFakeValue2");
        otherConfiguration.setProperty("same.config.key", "sameValue");
        otherConfiguration.setProperty("same.config.key.1", "sameValue1");
        otherConfiguration.setProperty("same.config.key.2", "otherDifferentValue2");
        otherConfiguration.setProperty("same.config.key.4", "otherDifferentValue3");

        return otherConfiguration;
    }

    private PropertiesConfiguration otherConfigurationWithEscapeChars() {
        PropertiesConfiguration otherConfiguration = new PropertiesConfiguration();
        otherConfiguration.setLayout(new PropertiesConfigurationLayout());

        otherConfiguration.setProperty("other.config.key", "otherFakeValue");
        otherConfiguration.setProperty("other.config.key.1", "otherFakeValue1");
        otherConfiguration.setProperty("other.config.key.2", "otherFakeValue2");
        otherConfiguration.setProperty("same.config.key", "sameValue\\");
        otherConfiguration.setProperty("same.config.key.1", "sameValue1");
        otherConfiguration.setProperty("same.config.key.2", "otherDifferentValue2");
        otherConfiguration.setProperty("same.config.key.4", "otherDifferentValue3");

        return otherConfiguration;
    }

    private MergeOptions withAdditionMergeOptions() {
        return new MergeOptions.Builder().withAdditionAllowed().build();
    }

    private MergeOptions withModificationMergeOptions() {
        return new MergeOptions.Builder().withModificationAllowed().build();
    }

    private MergeOptions withModificationAndRemoveEscapesMergeOptions() {
        return new MergeOptions.Builder().withModificationAllowed().withRemoveBaseEscapes().build();
    }

    private MergeOptions withDeletionMergeOptions() {
        return new MergeOptions.Builder().withDeletionAllowed().build();
    } 
    
    private MergeOptions defaultMergeOptions() {
        return new MergeOptions.Builder().build();
    }    
}
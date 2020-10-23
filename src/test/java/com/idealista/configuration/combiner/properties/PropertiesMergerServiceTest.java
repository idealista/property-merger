package com.idealista.configuration.combiner.properties;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.idealista.configuration.combiner.ConfigurationMerger;
import com.idealista.configuration.combiner.MergeOptions;
import com.idealista.configuration.combiner.PropertiesConfigurationReader;
import com.idealista.configuration.combiner.PropertiesConfigurationWriter;
import com.idealista.configuration.combiner.PropertiesMergerService;

@RunWith(MockitoJUnitRunner.class)
public class PropertiesMergerServiceTest {

    private static final MergeOptions MERGE_OPTIONS = new MergeOptions.Builder().build();

    private static final String FAKE_MERGE_RESULT_FILE_PATH = "fakeMergeResultFilePath";

    private static final String FAKE_OTHER_FILE_PATH = "fakeOtherFilePath";

    private static final String FAKE_BASE_FILE_PATH = "fakeBaseFilePath";

    @InjectMocks
    private PropertiesMergerService propertiesMergerService;
    
    @Mock
    private ConfigurationMerger configurationMergerMock;
    
    @Mock
    private PropertiesConfigurationWriter propertiesConfigurationWriterMock;
    
    @Mock
    private PropertiesConfigurationReader propertiesConfigurationReaderMock;
    
    @Test(expected=IllegalArgumentException.class)
    public void testWhenMergeWithNullBaseFilePath_ShouldThrowIllegalArgumentException() {
        propertiesMergerService.merge(null, null, null, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testWhenMergeWithEmtpyBaseFilePath_ShouldThrowIllegalArgumentException() {
        propertiesMergerService.merge("", null, null, null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testWhenMergeWithBlankBaseFilePath_ShouldThrowIllegalArgumentException() {
        propertiesMergerService.merge(" ", null, null, null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testWhenMergeWithNullOtherFilePath_ShouldThrowIllegalArgumentException() {
        propertiesMergerService.merge(FAKE_BASE_FILE_PATH, null, null, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testWhenMergeWithEmtpyOtherFilePath_ShouldThrowIllegalArgumentException() {
        propertiesMergerService.merge(FAKE_BASE_FILE_PATH, "", null, null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testWhenMergeWithBlankOtherFilePath_ShouldThrowIllegalArgumentException() {
        propertiesMergerService.merge(FAKE_BASE_FILE_PATH, " ", null, null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testWhenMergeWithMergeResultFilePath_ShouldThrowIllegalArgumentException() {
        propertiesMergerService.merge(FAKE_BASE_FILE_PATH, FAKE_OTHER_FILE_PATH, null, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testWhenMergeWithEmtpyMergeResultFilePath_ShouldThrowIllegalArgumentException() {
        propertiesMergerService.merge(FAKE_BASE_FILE_PATH, FAKE_OTHER_FILE_PATH, "", null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testWhenMergeWithBlankMergeResultFilePath_ShouldThrowIllegalArgumentException() {
        propertiesMergerService.merge(FAKE_BASE_FILE_PATH, FAKE_OTHER_FILE_PATH, " ", null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testWhenMergeWithNullMergeOptions_ShouldThrowIllegalArgumentException() {
        propertiesMergerService.merge(FAKE_BASE_FILE_PATH, FAKE_OTHER_FILE_PATH, FAKE_MERGE_RESULT_FILE_PATH, null);
    }
    
    @Test
    public void testMerge() {
        PropertiesConfiguration baseConfiguration = new PropertiesConfiguration();
        PropertiesConfiguration otherConfiguration = new PropertiesConfiguration();
        
        PropertiesConfiguration mergeResultConfiguration = new PropertiesConfiguration();
        
        Mockito.when(propertiesConfigurationReaderMock.read(FAKE_BASE_FILE_PATH)).thenReturn(baseConfiguration);
        Mockito.when(propertiesConfigurationReaderMock.read(FAKE_OTHER_FILE_PATH)).thenReturn(otherConfiguration);
        Mockito.when(configurationMergerMock.merge(baseConfiguration, otherConfiguration, MERGE_OPTIONS)).thenReturn(mergeResultConfiguration);
        
        propertiesMergerService.merge(FAKE_BASE_FILE_PATH, FAKE_OTHER_FILE_PATH, FAKE_MERGE_RESULT_FILE_PATH, MERGE_OPTIONS);
        Mockito.verify(propertiesConfigurationWriterMock).write(FAKE_MERGE_RESULT_FILE_PATH, mergeResultConfiguration);
    }
    
}
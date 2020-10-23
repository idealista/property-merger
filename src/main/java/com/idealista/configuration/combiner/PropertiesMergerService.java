package com.idealista.configuration.combiner;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;

public class PropertiesMergerService {
    
    private ConfigurationMerger configurationMerger;
    
    private PropertiesConfigurationWriter propertiesConfigurationWriter;
    
    private PropertiesConfigurationReader propertiesConfigurationReader;
    
    public PropertiesMergerService(ConfigurationMerger configurationMerger,
            PropertiesConfigurationWriter propertiesConfigurationWriter, 
            PropertiesConfigurationReader propertiesConfigurationReader) {
        this.configurationMerger = configurationMerger;
        this.propertiesConfigurationReader = propertiesConfigurationReader;
        this.propertiesConfigurationWriter = propertiesConfigurationWriter;
    }
    
    public void merge(String baseFilePath, String otherFilePath, String mergeResultPath, MergeOptions mergeOptions) {
        if(StringUtils.isBlank(baseFilePath)) throw new IllegalArgumentException("base File Path cannot be null or empty"); 
        if(StringUtils.isBlank(otherFilePath)) throw new IllegalArgumentException("other File Path cannot be null or empty"); 
        if(StringUtils.isBlank(mergeResultPath)) throw new IllegalArgumentException("merge result File Path cannot be null or empty");
        if(mergeOptions == null) throw new IllegalArgumentException("merge options cannot be null");
        
        PropertiesConfiguration baseConfiguration = read(baseFilePath);
        PropertiesConfiguration otherConfiguration = read(otherFilePath);
        
        PropertiesConfiguration mergeResult = configurationMerger.merge(baseConfiguration, otherConfiguration, mergeOptions);
        write(mergeResultPath, mergeResult);
    }    
    
    private void write(String fileName, PropertiesConfiguration configuration) {
        propertiesConfigurationWriter.write(fileName, configuration);
    }
    
    private PropertiesConfiguration read(String fileName) {
        return propertiesConfigurationReader.read(fileName);
    }    
   
}
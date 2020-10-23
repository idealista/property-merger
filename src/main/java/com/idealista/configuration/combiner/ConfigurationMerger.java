package com.idealista.configuration.combiner;

import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;

public class ConfigurationMerger {

    public PropertiesConfiguration merge(PropertiesConfiguration baseConfiguration, PropertiesConfiguration otherConfiguration, MergeOptions options) {
        if(baseConfiguration == null) throw new IllegalArgumentException("base configuration cannot be null");
        if(otherConfiguration == null) throw new IllegalArgumentException("other configuration cannot be null");
        if(options == null) throw new IllegalArgumentException("merge options cannot be null");        
        
        MergeCombiner combiner = new MergeCombiner(options);
        
        CombinedConfiguration props = new CombinedConfiguration();
        props.setNodeCombiner(combiner);
        props.addConfiguration(baseConfiguration);
        props.addConfiguration(otherConfiguration);
        
        PropertiesConfiguration mergedConfiguration = new PropertiesConfiguration();
        mergedConfiguration.setLayout(baseConfiguration.getLayout());
        mergedConfiguration.append(props);
        
        return mergedConfiguration;
    }
    
}
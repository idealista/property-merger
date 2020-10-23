package com.idealista.configuration.combiner.utils;

import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;

public class VersionRetriever {

    private static final String VERSION_KEY = "version";
    
    private static final String POM_PROPERTIES_PATH = "/META-INF/maven/com.idealista/property-merger/pom.properties";

    public static String getVersion() {
        String version = readVersionFromMavenProperties();
        
        if(StringUtils.isBlank(version)) {
            version = readVersionUsingJavaAPI();
        }

        return version;
    }

    private static String readVersionFromMavenProperties() {
        try {
            java.util.Properties p = new java.util.Properties();
            InputStream is = VersionRetriever.class.getResourceAsStream(POM_PROPERTIES_PATH);
            p.load(is);
            
            return p.getProperty(VERSION_KEY, StringUtils.EMPTY);
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }        
    }
    
    private static String readVersionUsingJavaAPI() {
        String version = StringUtils.EMPTY;
        Package aPackage = VersionRetriever.class.getPackage();
       
        if (aPackage != null) {
            version = aPackage.getImplementationVersion();
            if (StringUtils.isBlank(version)) {
                version = aPackage.getSpecificationVersion();
            }
        }
        
        return version;
    }
    
}
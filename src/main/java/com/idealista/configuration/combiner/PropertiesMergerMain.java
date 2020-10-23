package com.idealista.configuration.combiner;

import org.apache.commons.cli.ParseException;

import com.idealista.configuration.combiner.cli.CommandLineParser;
import com.idealista.configuration.combiner.cli.MergerCommandLine;
import com.idealista.configuration.combiner.utils.VersionRetriever;

class PropertiesMergerMain {

    public static void main(String[] args) {
        
        CommandLineParser parser = new CommandLineParser();

        PropertiesMergerService propertiesMergerService = new PropertiesMergerService(new ConfigurationMerger(),
                                                                                        new PropertiesConfigurationWriter(), 
                                                                                        new PropertiesConfigurationReader());

        try {
            MergerCommandLine cmd = parser.parse(args);

            if (cmd.hasHelpOption()) {
                parser.printHelp();
                return;
            }

            if (cmd.hasVersionOption()) {
                printVersion();
                return;
            }

            propertiesMergerService.merge(cmd.getBaseFilePath(), 
                                            cmd.getOtherFilePath(), 
                                            cmd.getMergeResultFilePath(),
                                            cmd.getMergeOptions());
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            parser.printHelp();
        }
    }

    private static void printVersion() {
        System.out.println("Property-merger version: " + VersionRetriever.getVersion());
    }
    
}
package com.idealista.configuration.combiner.cli;

import org.apache.commons.cli.CommandLine;

import com.idealista.configuration.combiner.MergeOptions;

public class MergerCommandLine {

    private CommandLine cmd; 
    
    public MergerCommandLine(CommandLine cmd) {
        this.cmd = cmd;
    }
    
    public boolean hasHelpOption() {
        return cmd.hasOption("help");
    }
    
    public boolean hasVersionOption() {
        return cmd.hasOption("version");
    }
    
    public String getBaseFilePath() {
        return cmd.getOptionValue("baseFilePath");
    }
    
    public String getOtherFilePath() {
        return cmd.getOptionValue("otherFilePath");
    }
    
    public String getMergeResultFilePath() {
        return cmd.getOptionValue("resultFilePath");
    }
    
    public MergeOptions getMergeOptions() {        
        MergeOptions.Builder mergeOptionsBuider = new MergeOptions.Builder();
        
        if(hasAdditionConfigured()) {
            mergeOptionsBuider.withAdditionAllowed();
        }
        
        if(hasModificationConfigured()) {
            mergeOptionsBuider.withModificationAllowed();
        }
        
        if(hasDeletionConfigured()) {
            mergeOptionsBuider.withDeletionAllowed();
        }

        if(hasRemoveBaseEscapesConfigured()) {
            mergeOptionsBuider.withRemoveBaseEscapes();
        }
        
        return mergeOptionsBuider.build();
    }
    
    private boolean hasDeletionConfigured() {
        return cmd.hasOption("with-deletion");
    }

    private boolean hasModificationConfigured() {
        return cmd.hasOption("with-modification");
    }

    private boolean hasAdditionConfigured() {
        return cmd.hasOption("with-addition");
    }

    private boolean hasRemoveBaseEscapesConfigured() {
        return cmd.hasOption("with-remove-base-escapes");
    }
}
package com.idealista.configuration.combiner.cli;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CommandLineParser {
   
    private static final int HELP_COLUMN_WIDTH = 180;
    
    private static final String CMD_LINE_SYNTAX = "java -jar <property_merger_jar_path> --help |"
            + " java -jar <property_merger_jar_path> --version |"
            + " java -jar <property_merger_jar_path> "
            + "--baseFilePath <base_file_path> "
            + "--otherFilePath <other_file_path> "
            + "--resultFilePath <merge_result_file_path> "
            + "[--with-adittion] [--with-deletion] [--with-modification] [--with-remove-base-escapes]";
    
    private static final String HEADER = "GitHub Page: https://www.github.com/idealista/property-merger";
    
    public MergerCommandLine parse(String[] args) throws ParseException {
        MergerCommandLine cmd = new MergerCommandLine(new DefaultParser().parse(buildMiscOptions(), args, true));

        if(!cmd.hasHelpOption() && !cmd.hasVersionOption()) {
            cmd = new MergerCommandLine(new DefaultParser().parse(buildCmdOptions(), args));
        }
        
        return cmd;
    }

    public void printHelp() {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setWidth(HELP_COLUMN_WIDTH);
        helpFormatter.printHelp(CMD_LINE_SYNTAX, HEADER, buildCmdOptions(), null);
    }
    
    private Options buildMiscOptions() {
        Options options = new Options();

        options.addOption(Option.builder("h").longOpt("help").desc("print this message").required(false).build());
        options.addOption(Option.builder("v").longOpt("version").desc("print version").required(false).build());
        
        return options;
    }
    
    private Options buildCmdOptions() {
        Options options = new Options();

        options.addOption(Option.builder("h").longOpt("help").desc("print this message").required(false).build());
        options.addOption(Option.builder("v").longOpt("version").desc("print version").required(false).build());
        
        options.addOption(Option.builder("b").longOpt("baseFilePath").desc("base file path").required(true).hasArg()
                                .argName("FILE PATH").type(String.class).build());
        options.addOption(Option.builder("t").longOpt("otherFilePath").desc("other file path").required(true).hasArg()
                                .argName("FILE PATH").type(String.class).build());
        options.addOption(Option.builder("r").longOpt("resultFilePath").desc("merge result file path").required(true).hasArg()
                                .argName("FILE PATH").type(String.class).build());
        
        options.addOption(Option.builder().longOpt("with-addition")
                                .desc("addition configured for merge (default to false)").required(false).build());
        options.addOption(Option.builder().longOpt("with-modification")
                                .desc("modification configured for merge (default to false)").required(false).build());
        options.addOption(Option.builder().longOpt("with-deletion")
                                .desc("deletion configured for merge (default to false)").required(false).build());
        options.addOption(Option.builder().longOpt("with-remove-base-escapes")
                .desc("remove escape character from base file").required(false).build());
                
        return options;
    }
}

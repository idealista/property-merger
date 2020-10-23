package com.idealista.configuration.combiner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration.DefaultIOFactory;
import org.apache.commons.configuration2.PropertiesConfiguration.PropertiesReader;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class PropertiesConfigurationReader {

    public PropertiesConfiguration read(String fileName) {
        if (StringUtils.isBlank(fileName))
            throw new IllegalArgumentException("fileName cannot be null or empty");

        try {
            return read(buildReader(fileName));
        } catch (Exception e) {
            throw new CannotReadPropertiesException("Cannot read properties configuration from fileName: " + fileName,
                    e);
        }
    }

    private PropertiesConfiguration read(Reader in) throws ConfigurationException, IOException {
        PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
        propertiesConfiguration.setIOFactory(new NonEscapingUnicodesIOFactory());
        propertiesConfiguration.read(in);

        return propertiesConfiguration;
    }

    private Reader buildReader(String fileName) throws FileNotFoundException {
        return new BufferedReader(new FileReader(fileName));
    }
    
    class NonEscapingUnicodesIOFactory extends DefaultIOFactory {

        @Override
        public PropertiesReader createPropertiesReader(Reader in) {
            return new PropertiesMergerReader(in);
        }
        
    }

    public static class PropertiesMergerReader extends PropertiesReader {

        public PropertiesMergerReader(Reader reader) {
            super(reader);
            commentLines = new ArrayList<>();
        }

        private static final char[] SEPARATORS = new char[] { '=', ':' };

        /** The regular expression to parse the key and the value of a property. */
        private static final Pattern PROPERTY_PATTERN = Pattern.compile("(([\\S&&[^\\\\" + new String(SEPARATORS)
                + "]]|\\\\.)*)(\\s*(\\s+|[" + new String(SEPARATORS) + "])\\s*)?(.*)");

        /** The list of possible key/value separators */

        /** Constant for the index of the group for the key. */
        private static final int IDX_KEY = 1;

        /** Constant for the index of the group for the value. */
        private static final int IDX_VALUE = 5;

        /** Constant for the index of the group for the separator. */
        private static final int IDX_SEPARATOR = 3;

        /** Stores the comment lines for the currently processed property. */
        private final List<String> commentLines;

        /** Stores the name of the last read property. */
        private String propertyName;

        /** Stores the value of the last read property. */
        private String propertyValue;

        /** Constant for the default properties separator. */
        static final String DEFAULT_SEPARATOR = " = ";

        /** Stores the property separator of the last read property. */
        private String propertySeparator = DEFAULT_SEPARATOR;

        /** Constant for the supported comment characters. */
        static final String COMMENT_CHARS = "#!";

        /**
         * Reads a property line. Returns null if Stream is at EOF. Concatenates lines
         * ending with "\". Skips lines beginning with "#" or "!" and empty lines. The
         * return value is a property definition (<code>&lt;name&gt;</code> =
         * <code>&lt;value&gt;</code>)
         *
         * @return A string containing a property value or null
         *
         * @throws IOException
         *             in case of an I/O error
         */
        public String readProperty() throws IOException {
            commentLines.clear();
            StringBuilder buffer = new StringBuilder();

            while (true) {
                String line = readLine();
                if (line == null) {
                    // EOF
                    return null;
                }

                if (isCommentLine(line)) {
                    commentLines.add(line);
                    continue;
                }

                line = line.trim();

                if (checkCombineLines(line)) {
                    line = line.substring(0, line.length() - 1);
                    buffer.append(line);
                } else {
                    buffer.append(line);
                    break;
                }
            }
            return buffer.toString();
        }

        /**
         * Parses the next property from the input stream and stores the found name and
         * value in internal fields. These fields can be obtained using the provided
         * getter methods. The return value indicates whether EOF was reached
         * (<b>false</b>) or whether further properties are available (<b>true</b>).
         *
         * @return a flag if further properties are available
         * @throws IOException
         *             if an error occurs
         * @since 1.3
         */
        public boolean nextProperty() throws IOException {
            String line = readProperty();

            if (line == null) {
                return false; // EOF
            }

            // parse the line
            parseProperty(line);
            return true;
        }

        /**
         * Returns the comment lines that have been read for the last property.
         *
         * @return the comment lines for the last property returned by
         *         {@code readProperty()}
         * @since 1.3
         */
        public List<String> getCommentLines() {
            return commentLines;
        }

        /**
         * Returns the name of the last read property. This method can be called after
         * {@link #nextProperty()} was invoked and its return value was <b>true</b>.
         *
         * @return the name of the last read property
         * @since 1.3
         */
        public String getPropertyName() {
            return propertyName;
        }

        /**
         * Returns the value of the last read property. This method can be called after
         * {@link #nextProperty()} was invoked and its return value was <b>true</b>.
         *
         * @return the value of the last read property
         * @since 1.3
         */
        public String getPropertyValue() {
            return propertyValue;
        }

        /**
         * Returns the separator that was used for the last read property. The separator
         * can be stored so that it can later be restored when saving the configuration.
         *
         * @return the separator for the last read property
         * @since 1.7
         */
        public String getPropertySeparator() {
            return propertySeparator;
        }

        /**
         * Parses a line read from the properties file. This method is called for each
         * non-comment line read from the source file. Its task is to split the passed
         * in line into the property key and its value. The results of the parse
         * operation can be stored by calling the {@code initPropertyXXX()} methods.
         *
         * @param line
         *            the line read from the properties file
         * @since 1.7
         */
        protected void parseProperty(String line) {
            String[] property = doParseProperty(line);
            initPropertyName(property[0]);
            initPropertyValue(property[1]);
            initPropertySeparator(property[2]);
        }

        /**
         * Sets the name of the current property. This method can be called by
         * {@code parseProperty()} for storing the results of the parse operation. It
         * also ensures that the property key is correctly escaped.
         *
         * @param name
         *            the name of the current property
         * @since 1.7
         */
        protected void initPropertyName(String name) {
            propertyName = StringEscapeUtils.unescapeJava(name);
        }

        /**
         * Sets the value of the current property. This method can be called by
         * {@code parseProperty()} for storing the results of the parse operation. It
         * also ensures that the property value is correctly escaped.
         *
         * @param value
         *            the value of the current property
         * @since 1.7
         */
        protected void initPropertyValue(String value) {
            propertyValue = value;
        }

        /**
         * Sets the separator of the current property. This method can be called by
         * {@code parseProperty()}. It allows the associated layout object to keep track
         * of the property separators. When saving the configuration the separators can
         * be restored.
         *
         * @param value
         *            the separator used for the current property
         * @since 1.7
         */
        protected void initPropertySeparator(String value) {
            propertySeparator = value;
        }

        /**
         * Checks if the passed in line should be combined with the following. This is
         * true, if the line ends with an odd number of backslashes.
         *
         * @param line
         *            the line
         * @return a flag if the lines should be combined
         */
        private static boolean checkCombineLines(String line) {
            return countTrailingBS(line) % 2 != 0;
        }

        /**
         * Parse a property line and return the key, the value, and the separator in an
         * array.
         *
         * @param line
         *            the line to parse
         * @return an array with the property's key, value, and separator
         */
        private static String[] doParseProperty(String line) {
            Matcher matcher = PROPERTY_PATTERN.matcher(line);

            String[] result = { "", "", "" };

            if (matcher.matches()) {
                result[0] = matcher.group(IDX_KEY).trim();
                result[1] = matcher.group(IDX_VALUE).trim();
                result[2] = matcher.group(IDX_SEPARATOR);
            }

            return result;
        }

        /**
         * Returns the number of trailing backslashes. This is sometimes needed for the
         * correct handling of escape characters.
         *
         * @param line
         *            the string to investigate
         * @return the number of trailing backslashes
         */
        private static int countTrailingBS(String line) {
            int bsCount = 0;
            for (int idx = line.length() - 1; idx >= 0 && line.charAt(idx) == '\\'; idx--) {
                bsCount++;
            }

            return bsCount;
        }

        static boolean isCommentLine(String line) {
            String s = line.trim();
            // blanc lines are also treated as comment lines
            return s.length() < 1 || COMMENT_CHARS.indexOf(s.charAt(0)) >= 0;
        }
    }
}
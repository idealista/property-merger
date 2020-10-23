package com.idealista.configuration.combiner;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration.DefaultIOFactory;
import org.apache.commons.configuration2.PropertiesConfiguration.PropertiesWriter;
import org.apache.commons.configuration2.PropertiesConfigurationLayout;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.convert.ValueTransformer;
import org.apache.commons.lang3.StringUtils;

public class PropertiesConfigurationWriter {

    public void write(String fileName, PropertiesConfiguration configuration) {
        if(StringUtils.isBlank(fileName)) throw new IllegalArgumentException("file name cannot be null or empty");        
        if(configuration == null) throw new IllegalArgumentException("configuration cannot be null");   
        
        try {
            configuration.setIOFactory(new NonEscapingUnicodesIOFactory());
            PropertiesConfigurationLayout layout = new PropertiesConfigurationLayout(configuration.getLayout());
            layout.save(configuration, buildWriter(fileName));
        } catch(Exception e) {
            throw new CannotWritePropertiesException("Cannot write properties configuration in fileName: " + fileName, e);
        }
    }

    private Writer buildWriter(String fileName) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8));
    }
    
}

class NonEscapingUnicodesIOFactory extends DefaultIOFactory {

    @Override
    public PropertiesWriter createPropertiesWriter(Writer out, ListDelimiterHandler handler) {
        return new NonEscapingUnicodePropertiesWriter(out, handler);
    }
    
}

class NonEscapingUnicodePropertiesWriter extends PropertiesWriter {

    public NonEscapingUnicodePropertiesWriter(Writer writer, ListDelimiterHandler delHandler) {
        super(writer, delHandler);
    }
    
    @Override
    public void writeProperty(String key, Object value, boolean forceSingleLine) throws IOException {
        String v;

        if (value instanceof List) {
            v = null;
            List<?> values = (List<?>) value;
            if (forceSingleLine) {
                try {
                    v = String.valueOf(getDelimiterHandler().escapeList(values, new ValueTransformer() {
                        
                        @Override
                        public Object transformValue(Object value) {
                            return value;
                        }
                    }));
                } catch (UnsupportedOperationException uoex) {
                    // the handler may not support escaping lists,
                    // then the list is written in multiple lines
                }
            }
            if (v == null) {
                writeProperty(key, values);
                return;
            }
        } else {
            v = String.valueOf(getDelimiterHandler().escape(value, new ValueTransformer() {
                
                @Override
                public Object transformValue(Object value) {
                    return value;
                }
            }));
        }

        write(escapeKey(key));
        write(fetchSeparator(key, value));
        write(v);

        writeln(null);
    }
}
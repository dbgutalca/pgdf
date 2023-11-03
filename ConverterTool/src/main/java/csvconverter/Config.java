package csvconverter;

import java.util.List;

public abstract class Config {
    protected boolean header;
    protected String delimiter;
    private List<String> labels;
    private List<String> properties;

    public Config(List<String> labels, List<String> properties, String delimiter, boolean header) {
        this.labels = labels;
        this.properties = properties;
        this.delimiter = delimiter;
        this.header = header;
    }

    public List<String> getLabels() {
        return labels;
    }

    public List<String> getProperties() {
        return properties;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public boolean hasHeader() {
        return header;
    }
}

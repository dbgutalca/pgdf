package csvconverter;

import java.util.List;

public class NodeConfig extends Config{

    private final int id;

    public NodeConfig(int id, String delimiter, boolean header, List<String> labels, List<String> properties) {
        super(labels, properties, delimiter, header);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

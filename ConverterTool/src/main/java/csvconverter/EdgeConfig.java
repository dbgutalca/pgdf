package csvconverter;

import java.util.List;

public class EdgeConfig extends Config{

    private final int source;
    private final int target;
    private final boolean dir;

    public EdgeConfig(int source, int target, String delimiter, boolean header, boolean dir, List<String> labels, List<String> properties) {
        super(labels, properties, delimiter, header);
        this.source = source;
        this.target = target;
        this.dir = dir;
    }

    public int getSource() {
        return source;
    }

    public int getTarget() {
        return target;
    }

    public boolean isDir() {
        return dir;
    }
}

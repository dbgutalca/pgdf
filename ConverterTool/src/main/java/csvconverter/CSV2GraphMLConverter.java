package csvconverter;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CSV2GraphMLConverter extends CSVConverter{

    private final String propertyPattern = "\t<key id=\"%s\" for=\"%s\" attr.name=\"%s\" \"attr.type=\"string\"/>\n";
    private final String nodePattern = """
                    <node id="%s">
            %s
                    </node>
            """;
    private final String edgePattern = """
                    <edge id="%s" source="%s" target="%s">
            %s
                    </edge>
            """;
    private final String valuePattern = "\t\t\t<data key=\"%s\">%s</data>";
    public CSV2GraphMLConverter(String jsonPath, String outputPath) throws FileNotFoundException {
        super(jsonPath, outputPath);
    }

    @Override
    protected void makefile(FileWriter fileWriter) throws FileNotFoundException {
        try {
            fileWriter.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            fileWriter.write("<GraphML>\n");
            fileWriter.write("\t<key id=\"labelV\" for=\"node\" attr.name=\"labelV\" attr.type=\"string\"/>\n");
            fileWriter.write("\t<key id=\"labelE\" for=\"edge\" attr.name=\"labelE\" \"attr.type=\"string\"/>\n");
            Set<String> propNames = new HashSet<>();
            for (NodeConfig nc : this.nodeConfigs.values()) {
                for (String p : nc.getProperties()) {
                    if (!propNames.contains(p)) {
                        fileWriter.write(String.format(propertyPattern, p, "node", p));
                        propNames.add(p);
                    }
                }
            }
            propNames = new HashSet<>();
            for(EdgeConfig ec : this.edgeConfigs.values()) {
                for (String p : ec.getProperties()) {
                    if (!propNames.contains(p)) {
                        fileWriter.write(String.format(propertyPattern, p, "edge", p));
                        propNames.add(p);
                    }
                }
            }
            fileWriter.write("\t<graph id=\"G\" edgedefault=\"directed\">\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (String nodeFilename : this.nodeConfigs.keySet()) {
            NodeConfig nc = this.nodeConfigs.get(nodeFilename);
            String line;
            try (BufferedReader br = new BufferedReader(new FileReader(nodeFilename))) {
                if (nc.hasHeader())
                    br.readLine();
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(nc.getDelimiter());
                    int idx = nc.getProperties().indexOf("@id");
                    String id = parts[idx];
                    List<String> properties = new LinkedList<>();
                    properties.add(String.format(valuePattern, "labelV", String.join(",", nc.getLabels())));
                    for (int i = 0; i < parts.length; i++) {
                        if (i != idx){
                            properties.add(String.format(valuePattern, nc.getProperties().get(i), parts[i]));
                        }
                    }
                    fileWriter.write(String.format(nodePattern, id, String.join("\n", properties)));
                }
            } catch (IOException e) {
                System.err.println("An error occurred while reading node file "+nodeFilename);
            }
        }
        for (String edgeFilename : this.edgeConfigs.keySet()) {
            EdgeConfig ec = this.edgeConfigs.get(edgeFilename);
            try (BufferedReader br = new BufferedReader(new FileReader(edgeFilename))) {
                String line;
                if (ec.hasHeader())
                    br.readLine();
                int count = 1;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(ec.getDelimiter());
                    int idx = ec.getProperties().indexOf("@id");
                    boolean generateId = idx==-1;
                    String id = !generateId? parts[idx] : String.valueOf(count++);
                    int sdx = ec.getProperties().indexOf("@out");
                    int tdx = ec.getProperties().indexOf("@in");
                    String src = parts[sdx];
                    String tgt = parts[tdx];
                    List<String> properties = new LinkedList<>();
                    properties.add(String.format(valuePattern, "labelE", ec.getLabels().get(0)));
                    for (int i = 0; i < parts.length; i++) {
                        if (i != idx && i != sdx && i != tdx){
                            properties.add(String.format(valuePattern, ec.getProperties().get(i), parts[i]));
                        }
                    }
                    fileWriter.write(String.format(edgePattern, id, src, tgt,
                            String.join("\n", properties)));
                }
            } catch (IOException e) {
                System.err.println("An error occurred while reading node file "+edgeFilename);
            }
        }
        try {
            fileWriter.write("\t</graph>\n" + "</GraphML>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

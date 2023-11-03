package csvconverter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.*;

public abstract class CSVConverter {

    protected Map<String, NodeConfig> nodeConfigs;
    protected Map<String, EdgeConfig> edgeConfigs;
    protected String outputPath;

    protected CSVConverter(String jsonPath, String outputPath) throws FileNotFoundException {
        this.outputPath = outputPath;
        this.nodeConfigs = new HashMap<>();
        this.edgeConfigs = new HashMap<>();

        JsonObject root = JsonParser.parseReader(new FileReader(jsonPath)).getAsJsonObject();
        JsonArray nodes = root.getAsJsonArray("nodes");
        JsonArray edges = root.getAsJsonArray("edges");

        for (JsonElement node : nodes) {
            //System.out.println(node);
            JsonObject o = node.getAsJsonObject();
            String filename = o.get("file").getAsString();
            int id = o.get("id").getAsInt();
            JsonArray labelsArray = o.get("labels").getAsJsonArray();
            List<String> labels = new LinkedList<>();
            for (JsonElement label : labelsArray) {
                labels.add(label.getAsString());
            }
            String delim = escapeDelim(o.get("delimiter").getAsString());
            boolean header = o.get("header").getAsBoolean();
            JsonArray propertiesArray = o.get("properties").getAsJsonArray();
            List<String> properties = new LinkedList<>();
            for (JsonElement property : propertiesArray) {
                properties.add(property.getAsString());
            }
            nodeConfigs.put(filename, new NodeConfig(id, delim, header, labels, properties));
        }

        for (JsonElement edge : edges) {
            JsonObject o = edge.getAsJsonObject();
            String filename = o.get("file").getAsString();
            int source = o.get("source").getAsInt();
            int target = o.get("target").getAsInt();
            String label = o.get("label").getAsString();
            String delim = escapeDelim(o.get("delimiter").getAsString());
            boolean header = o.get("header").getAsBoolean();
            boolean dir = o.get("dir").getAsBoolean();
            JsonArray propertiesArray = o.get("properties").getAsJsonArray();
            List<String> properties = new LinkedList<>();
            for (JsonElement property : propertiesArray) {
                properties.add(property.getAsString());
            }
            edgeConfigs.put(filename, new EdgeConfig(source, target, delim, header, dir, List.of(label), properties));
        }
    }

    private String escapeDelim(String delimiter) {
        return delimiter.replace("|", "\\|").replace(".", "\\.")
                .replace("?", "\\?").replace("*", "\\*")
                .replace("+", "\\+");
    }

    public static void main(String[] args) throws FileNotFoundException {
        String jsonPath = args[1];
        String outputPath = args[2];
        long start = System.nanoTime();
        switch (args[0]) {
            case "yarspg" -> new CSV2YarsPGConverter(jsonPath, outputPath).convert("graph.yarspg");
            case "pgdf" -> new CSV2PGDFConverter(jsonPath, outputPath).convert("graph.pgdf");
            case "graphml" -> new CSV2GraphMLConverter(jsonPath, outputPath).convert("graph.gml");
            case "json" -> new CSV2JSONConverter(jsonPath, outputPath).convert("graph.json");
            case "graphson" -> new CSV2GraphSONConverter(jsonPath, outputPath).convert("graph.graphson");
            default -> throw new UnsupportedOperationException("CSV conversion to " + args[0] + " not supported");
        }
        long end = System.nanoTime();
        System.out.println((end-start)/10e9);
    }

    public void convert(String filename){
        File file = new File(this.outputPath, filename);

        try {
            FileWriter fileWriter = new FileWriter(file);
            makefile(fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("An error occurred while writing to the file.");
        }
    }

    protected abstract void makefile(FileWriter fileWriter) throws FileNotFoundException;

}

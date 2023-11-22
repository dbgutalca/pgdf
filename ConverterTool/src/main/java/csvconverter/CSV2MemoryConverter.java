package csvconverter;

import Objects.Edge;
import Objects.GraphMemory;
import Objects.Node;
import Objects.Property;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class CSV2MemoryConverter extends CSVConverter {

    private String format;
    private boolean edgeIds = false;

    public CSV2MemoryConverter(String jsonPath, String outputPath, String format) throws FileNotFoundException {
        super(jsonPath, outputPath);
        this.format = format;
    }

    @Override
    protected void makefile(FileWriter fileWriter) {
        GraphMemory g = readGraph();
        long start = 0, end = 0;
        switch (format){
            case "pgdf"-> {
                try {
                    start = System.nanoTime();
                    makepgdf(g, fileWriter);
                    end = System.nanoTime();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case "pgdf2"-> {
                try {
                    start = System.nanoTime();
                    makepgdf2(g, fileWriter);
                    end = System.nanoTime();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case "yarspg" -> {
                try {
                    start = System.nanoTime();
                    makeyarspg(g, fileWriter);
                    end = System.nanoTime();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case "graphml" -> {
                try {
                    start = System.nanoTime();
                    makegraphml(g, fileWriter);
                    end = System.nanoTime();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case "json" -> {
                try {
                    start = System.nanoTime();
                    makejson(g, fileWriter);
                    end = System.nanoTime();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        try {
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(format);
        System.out.println("Elapsed Time: "+(end-start)/10e9+" sec.");
        try {
            System.out.println("File Size: " + Files.size(
                    FileSystems.getDefault().getPath(outputPath+"/memgraph."+format))/(1024.0*1024) + " MB");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Files.delete(FileSystems.getDefault().getPath(outputPath+"/memgraph."+format));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("-------");
    }

    private GraphMemory readGraph() {
        GraphMemory g = new GraphMemory();
        for (String nodeFilename : this.nodeConfigs.keySet()) {
            try (BufferedReader br = new BufferedReader(new FileReader(nodeFilename))) {
                NodeConfig nc = this.nodeConfigs.get(nodeFilename);
                int idx = nc.getProperties().indexOf("@id");
                //nc.getProperties().remove(idx);
                List<String> labels = nc.getLabels();
                String line;
                if (nc.hasHeader())
                    br.readLine();
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(nc.getDelimiter());
                    String id = parts[idx];
                    Node n = new Node(nc.getId()+"-"+id);
                    n.addLabels(labels);
                    for (int i = 0; i < parts.length; i++) {
                        if (i != idx){
                            n.addProperty(new Property(nc.getProperties().get(i), parts[i]));
                        }
                    }
                    g.addNode(n);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        int count = 0;
        for (String edgeFilename : this.edgeConfigs.keySet()) {
            try (BufferedReader br = new BufferedReader(new FileReader(edgeFilename))) {
                EdgeConfig ec = this.edgeConfigs.get(edgeFilename);
                String line;
                if (ec.hasHeader())
                    br.readLine();
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(ec.getDelimiter());
                    int idx = ec.getProperties().indexOf("@id");
                    int sdx = ec.getProperties().indexOf("@out");
                    int tdx = ec.getProperties().indexOf("@in");
                    if (idx!=-1) edgeIds = true;
                    String src = ec.getSource()+"-"+parts[sdx];
                    String tgt = ec.getTarget()+"-"+parts[tdx];
                    Edge e = new Edge(""+count, ec.getLabels().get(0), src, tgt);
                    for (int i = 0; i < parts.length; i++) {
                        if (i != idx && i != sdx && i != tdx) {
                            e.addProperty(new Property(ec.getProperties().get(i), parts[i]));
                        }
                    }
                    g.addEdge(e);
                    count++;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return g;
    }

    private void makejson(GraphMemory g, FileWriter fileWriter) throws IOException {
        String nodePattern = """
            {
                "type": "node",
                "id": "%s",
                "labels": [%s],
                "properties": {
                %s
                }
            }""";
        fileWriter.write("[");
        boolean first = true;
        for (Node n : g.nodeIdToNode.values()) {
            if (!first) {
                fileWriter.write(",");
            }
            fileWriter.write(String.format(nodePattern, n.getId(), "\""+n.getLabels().get(0)+"\"",
                    "\t"+n.getProperties().stream().map(x->"\""+x.getKey()+"\": \""+x.getValue()+"\"").collect(Collectors.joining(",\n\t\t"))));
            first = false;
        }
        String edgePattern = """
            {
                "type": "relationship",
                "id": "%s",
                "label": "%s",
                "properties": {
                %s
                },
                "start": {
                    "id": "%s"
                },
                "end": {
                    "id": "%s"
                }
            }""";
        for (Edge e : g.edgeIdToEdge.values()) {
            fileWriter.write(",");
            fileWriter.write(String.format(edgePattern, e.getId(), e.getLabel(),
                    "\t"+e.getProperties().stream().map(x->"\""+x.getKey()+"\": \""+x.getValue()).collect(Collectors.joining(",\n\t\t")),
                    e.getSource(), e.getTarget()));
        }
        fileWriter.write("]");
    }

    private void makegraphml(GraphMemory g, FileWriter fileWriter) throws IOException {
        fileWriter.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        fileWriter.write("<GraphML>\n");
        fileWriter.write("\t<key id=\"labelV\" for=\"node\" attr.name=\"labelV\" attr.type=\"string\"/>\n");
        fileWriter.write("\t<key id=\"labelE\" for=\"edge\" attr.name=\"labelE\" \"attr.type=\"string\"/>\n");
        String propertyPattern = "\t<key id=\"%s\" for=\"%s\" attr.name=\"%s\" \"attr.type=\"string\"/>\n";
        Set<String> nProps = nodeConfigs.values().stream().flatMap(x -> x.getProperties().stream()).filter(t -> !t.startsWith("@")).collect(Collectors.toSet());
        Set<String> eProps = edgeConfigs.values().stream().flatMap(x -> x.getProperties().stream()).filter(t -> !t.startsWith("@")).collect(Collectors.toSet());
        int pcount = 0;
        for (String p : nProps) {
            fileWriter.write(String.format(propertyPattern, pcount++, "node", p));
        }
        for (String p : eProps) {
            fileWriter.write(String.format(propertyPattern, pcount++, "edge", p));
        }
        fileWriter.write("\t<graph id=\"G\" edgedefault=\"directed\">\n");
        String nodePattern = """
                        <node id="%s">
                %s
                %s
                        </node>
                """;
        String valuePattern = "\t\t\t<data key=\"%s\">%s</data>";
        for (Node n : g.nodeIdToNode.values()) {
            fileWriter.write(String.format(nodePattern, n.getId(),
                    String.format(valuePattern, "labelV", n.getLabels().get(0)),
                    n.getProperties().stream().map(x->String.format(valuePattern, x.getKey(), x.getValue())).collect(Collectors.joining("\n"))));
        }
        String edgePattern = """
                    <edge id="%s" source="%s" target="%s">
            %s
            %s
                    </edge>
            """;
        for (Edge e : g.edgeIdToEdge.values()) {
            fileWriter.write(String.format(edgePattern, e.getId(), e.getSource(), e.getTarget(),
                    String.format(valuePattern, "labelE", e.getLabel()),
                    e.getProperties().stream().map(x->String.format(valuePattern, x.getKey(), x.getValue())).collect(Collectors.joining("\n"))));
        }
        fileWriter.write("\t</graph>\n" + "</GraphML>");
    }

    private void makeyarspg(GraphMemory g, FileWriter fileWriter) throws IOException {
        String nodePattern = "%s[%s]:{%s}\n";
        String edgePattern = "(%s)-[%s: {%s}]->(%s)\n";
        for (Node n : g.nodeIdToNode.values()) {
            fileWriter.write(String.format(nodePattern, n.getId(), n.getLabels().get(0),
                    n.getProperties().stream().map(x->x.getKey()+": \""+x.getValue()+"\"").collect(Collectors.joining(", "))));
        }
        for (Edge e : g.edgeIdToEdge.values()) {
            fileWriter.write(String.format(edgePattern, e.getSource(), e.getLabel(),
                    e.getProperties().stream().map(x->x.getKey()+": \""+x.getValue()+"\"").collect(Collectors.joining(",")),
                    e.getTarget()));
        }
    }

    private void makepgdf(GraphMemory g, FileWriter fileWriter) throws IOException {
        Set<String> labels = nodeConfigs.values().stream().flatMap(x->x.getLabels().stream()).collect(Collectors.toSet());
        boolean first;
        for (String label : labels){
            first = true;
            for (Node n : g.nodeIdToNode.values().stream().filter(x->x.getLabels().contains(label)).collect(Collectors.toList())) {
                if (first) {
                    fileWriter.write("@id|@label|"+n.getProperties().stream().map(Property::getKey).collect(Collectors.joining("|"))+"\n");
                    first = false;
                }
                fileWriter.write(n.getId()+"|"+label+"|"+n.getProperties().stream().map(Property::getValue).collect(Collectors.joining("|"))+"\n");
            }
        }
        labels = edgeConfigs.values().stream().flatMap(x->x.getLabels().stream()).collect(Collectors.toSet());
        for (String label : labels){
            first = true;
            for (Edge e : g.edgeIdToEdge.values().stream().filter(x->x.getLabel().equals(label)).collect(Collectors.toList())) {
                if (first) {
                    if (edgeIds)
                        fileWriter.write("@id|");
                    fileWriter.write("@label|@dir|@out|@in|"+e.getProperties().stream().map(Property::getKey).collect(Collectors.joining("|"))+"\n");
                    first = false;
                }
                if (edgeIds) {
                    fileWriter.write(e.getId());
                    fileWriter.write("|");
                }
                fileWriter.write(label+"|T|"+e.getSource()+"|"+e.getTarget()+"|"+e.getProperties().stream().map(Property::getValue).collect(Collectors.joining("|"))+"\n");
            }
        }
    }

    public void makepgdf2(GraphMemory g, FileWriter fileWriter) throws IOException {
        List<String> currentSchema = null;
        for (Node n : g.nodeIdToNode.values()) {
            List<String> nodeSchema = n.getProperties().stream().map(x->x.getKey()).collect(Collectors.toList());
            if (!nodeSchema.equals(currentSchema)) {
                currentSchema = nodeSchema;
                fileWriter.write("@id|@label|");
                fileWriter.write(String.join("|", currentSchema));
                fileWriter.write("\n");
            }
            fileWriter.write(n.getId()+"|"+n.getLabels().get(0)+"|");
            fileWriter.write(n.getProperties().stream().map(Property::getValue).collect(Collectors.joining("|")));
            fileWriter.write("\n");
        }
        for (Edge e : g.edgeIdToEdge.values()) {
            List<String> edgeSchema = e.getProperties().stream().map(x->x.getKey()).collect(Collectors.toList());
            if (!edgeSchema.equals(currentSchema)) {
                currentSchema = edgeSchema;
                fileWriter.write("@label|@dir|@out|@in|");
                fileWriter.write(String.join("|", currentSchema));
                fileWriter.write("\n");
            }
            fileWriter.write("\n"+e.getLabel()+"|T|"+e.getSource()+"|"+e.getTarget()+"|");
            fileWriter.write(e.getProperties().stream().map(Property::getValue).collect(Collectors.joining("|")));
            fileWriter.write("\n");
        }
    }
}

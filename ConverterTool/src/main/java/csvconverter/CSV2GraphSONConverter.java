package csvconverter;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CSV2GraphSONConverter extends CSVConverter{

    private final String nodeTemplate = """
            {
                "id": {"@type":"g:Int64","@value":%s},
                "label": %s,
                "inE": {
                    %s},
                "outE": {
                    %s},
                "properties": {
                    %s}
            }""";

    private final String edgeTemplate = "{\"%s\": [{\"id\": {\"@type\":\"g:Int64\",\"@value\":%s}, \"%sV\": {\"@type\":\"g:Int64\",\"@value\":%s}, \"properties\": {%s}}]}";

    private final String valueTemplate = """
            "%s": [{"id": {"@type":"g:Int64","@value":%s},"value": "%s"}]""";

    public CSV2GraphSONConverter(String jsonPath, String outputPath) throws FileNotFoundException {
        super(jsonPath, outputPath);
    }

    @Override
    protected void makefile(FileWriter fileWriter) throws FileNotFoundException {
        try {
            fileWriter.write("[");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Map<String, Integer> propIds = new HashMap<>();
        int currentPropId = Integer.MAX_VALUE;
        for (String nodeFilename : this.nodeConfigs.keySet()) {
            System.out.println("NODE");
            System.out.println(nodeFilename);
            NodeConfig nc = this.nodeConfigs.get(nodeFilename);
            String line;
            try (BufferedReader br = new BufferedReader(new FileReader(nodeFilename))) {
                if (nc.hasHeader())
                    br.readLine();
                int idx = nc.getProperties().indexOf("@id");
                for (int i = 0; i < nc.getProperties().size(); i++) {
                    if (i != idx){
                        if (!propIds.containsKey(nc.getProperties().get(i))) {
                            propIds.put(nc.getProperties().get(i), currentPropId--);
                        }
                    }
                }
                boolean first = true;
                while ((line = br.readLine()) != null) {
                    if (!first) fileWriter.write(",");
                    String[] parts = line.split(nc.getDelimiter());
                    String id = parts[idx];
                    List<String> properties = new LinkedList<>();
                    List<String> inE = new LinkedList<>();
                    List<String> outE = new LinkedList<>();
                    for (int i = 0; i < parts.length; i++) {
                        if (i==idx) continue;
                        properties.add(String.format(valueTemplate, nc.getProperties().get(i),
                                propIds.get(nc.getProperties().get(i)), parts[i]));
                    }
                    for (String edgeFilename : this.edgeConfigs.keySet()) {
                        EdgeConfig ec = this.edgeConfigs.get(edgeFilename);
                        if (ec.getSource()==nc.getId() || ec.getTarget()==nc.getId()) {
                            String edgeLine;
                            BufferedReader edgebr = new BufferedReader(new FileReader(edgeFilename));
                            if (ec.hasHeader())
                                edgebr.readLine();
                            while ((edgeLine=edgebr.readLine())!=null) {
                                String[] edgeParts = edgeLine.split(ec.getDelimiter());
                                int sdx = ec.getProperties().indexOf("@out");
                                int tdx = ec.getProperties().indexOf("@in");
                                if (edgeParts[sdx].equals(id)) {
                                    addEdge(ec, String.valueOf(edgeLine.hashCode()), edgeParts, sdx, tdx, inE, "out");
                                } else if (edgeParts[tdx].equals(id)) {
                                    addEdge(ec, String.valueOf(edgeLine.hashCode()), edgeParts, sdx, tdx, outE, "in");
                                }
                            }
                        }
                    }

                    fileWriter.write(String.format(nodeTemplate, id, String.join(":", nc.getLabels()),
                            String.join(",\n\t\t", inE),
                            String.join(",\n\t\t", outE),
                            String.join(",\n\t\t", properties))+"\n");
                    first = false;
                }
            } catch (IOException e) {
                System.err.println("An error occurred while reading node file "+nodeFilename);
            }
        }
        try {
            fileWriter.write("]");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addEdge(EdgeConfig ec, String edgeLine, String[] parts, int sdx, int tdx,
                         List<String> l, String out) {
        int edgeIdx = ec.getProperties().indexOf("@id");
        boolean genEdgeId = edgeIdx == -1;
        String edgeId = genEdgeId? edgeLine : parts[edgeIdx];
        List<String> edgeProperties = new LinkedList<>();
        for (int i = 0; i < parts.length; i++) {
            if (i== edgeIdx || i== sdx || i== tdx) continue;
            edgeProperties.add("\"" + ec.getProperties().get(i) + "\":\"" + parts[i] + "\"");
        }
        l.add(String.format(edgeTemplate, ec.getLabels().get(0), edgeId, out,
                out.equals("in")? parts[sdx] : parts[tdx], String.join(", ", edgeProperties)));
    }
}

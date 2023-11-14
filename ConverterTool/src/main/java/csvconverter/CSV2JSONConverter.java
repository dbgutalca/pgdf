package csvconverter;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CSV2JSONConverter extends CSVConverter{

    private final String nodePattern = """
            {
                "type": "node",
                "id": "%s",
                "labels": [%s],
                "properties": {
                %s
                }
            }
            """;

    private final String edgePattern = """
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
            }
            """;

    public CSV2JSONConverter(String jsonPath, String outputPath) throws FileNotFoundException {
        super(jsonPath, outputPath);
    }

    @Override
    protected void makefile(FileWriter fileWriter) throws FileNotFoundException {
        try {
            fileWriter.write("[");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String line = null;
        for (String nodeFilename : this.nodeConfigs.keySet()) {
            try (BufferedReader br = new BufferedReader(new FileReader(nodeFilename))) {
                NodeConfig nc = this.nodeConfigs.get(nodeFilename);
                int idx = nc.getProperties().indexOf("@id");
                String labels = "\""+String.join("\", \"", nc.getLabels())+"\"";
                if (nc.hasHeader())
                    br.readLine();

                boolean first = true;
                while ((line = br.readLine()) != null) {
                    if (!first) fileWriter.write(",");
                    String[] parts = line.split(nc.getDelimiter());
                    String id = parts[idx];
                    List<String> properties = new LinkedList<>();
                    for (int i=0; i<nc.getProperties().size(); i++) {
                        if (i==idx) continue;
                        properties.add("\t\""+nc.getProperties().get(i)+"\": \""+parts[i]+"\"");
                    }
                    fileWriter.write(String.format(nodePattern, id, labels, String.join(",\n\t", properties)));
                    if (first) first = false;
                }
            } catch (IOException e) {
                System.err.println("An error occurred while reading node file "+nodeFilename);
            } catch (ArrayIndexOutOfBoundsException ie) {
                System.out.println(line);
                System.out.println(this.nodeConfigs.get(nodeFilename).getProperties());
            }
        }
        for (String edgeFilename : this.edgeConfigs.keySet()) {
            EdgeConfig ec = this.edgeConfigs.get(edgeFilename);
            int idx = ec.getProperties().indexOf("@id");
            boolean generateId = idx==-1;
            if (!generateId)
                ec.getProperties().remove(idx);
            int sdx = ec.getProperties().indexOf("@out");
            int tdx = ec.getProperties().indexOf("@in");
            try (BufferedReader br = new BufferedReader(new FileReader(edgeFilename))) {
                if (ec.hasHeader())
                    br.readLine();
                //String line;
                int count = 1;
                boolean first = true;
                while ((line = br.readLine()) != null) {
                    if (!first) fileWriter.write(",");
                    String[] parts = line.split(ec.getDelimiter());
                    String id = generateId? String.valueOf(count) : parts[idx];
                    String src = parts[generateId? sdx : sdx+1];
                    String tgt = parts[generateId? tdx : tdx+1];
                    List<String> properties = new LinkedList<>();
                    for (int i=0; i<ec.getProperties().size(); i++) {
                        if (i==idx || i==sdx || i==tdx) continue;
                        properties.add("\t\""+ec.getProperties().get(i)+"\": \""+parts[i]+"\"");
                    }
                    fileWriter.write(String.format(edgePattern, id, ec.getLabels().get(0),
                            String.join(",\n\t", properties), src, tgt));
                    count++;
                    if (first) first=false;
                }
            } catch (IOException e) {
                System.err.println("An error occurred while reading edge file "+edgeFilename);
            }
        }
        try {
            fileWriter.write("]");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

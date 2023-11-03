package csvconverter;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class CSV2YarsPGConverter extends CSVConverter{

    public CSV2YarsPGConverter(String jsonPath, String outputPath) throws FileNotFoundException {
        super(jsonPath, outputPath);
    }


    protected void makefile(FileWriter fileWriter) throws FileNotFoundException {
        String nodeTemplate = "%s[%s]:{%s}\n";
        String edgeTemplate = "(%s)-[%s {%s}]->(%s)\n";
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
                    for (int i = 0; i < parts.length; i++) {
                        if (i != idx){
                            properties.add(String.format("%s:\"%s\"", nc.getProperties().get(i), parts[i]));
                        }
                    }
                    fileWriter.write(String.format(nodeTemplate, id, String.join(":", nc.getLabels()),
                            String.join(", ", properties)));
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
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(ec.getDelimiter());
                    int idx = ec.getProperties().indexOf("@id");
                    int sdx = ec.getProperties().indexOf("@out");
                    int tdx = ec.getProperties().indexOf("@in");
                    String src = parts[sdx];
                    String tgt = parts[tdx];
                    List<String> properties = new LinkedList<>();
                    for (int i = 0; i < parts.length; i++) {
                        if (i != idx && i != sdx && i != tdx){
                            properties.add(String.format("%s:\"%s\"", ec.getProperties().get(i), parts[i]));
                        }
                    }
                    fileWriter.write(String.format(edgeTemplate, src, ec.getLabels().get(0),
                            String.join(", ", properties), tgt));
                }
            } catch (IOException e) {
                System.err.println("An error occurred while reading node file "+edgeFilename);
            }
        }
    }
}

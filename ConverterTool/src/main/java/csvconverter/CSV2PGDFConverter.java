package csvconverter;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class CSV2PGDFConverter extends CSVConverter{

    protected CSV2PGDFConverter(String jsonPath, String outputPath) throws FileNotFoundException {
        super(jsonPath, outputPath);
    }
    protected void makefile(FileWriter fileWriter) {
        for (String nodeFilename : this.nodeConfigs.keySet()) {
            try (BufferedReader br = new BufferedReader(new FileReader(nodeFilename))) {
                NodeConfig nc = this.nodeConfigs.get(nodeFilename);
                int idx = nc.getProperties().indexOf("@id");
                nc.getProperties().remove(idx);
                fileWriter.write("@id|@label|" + String.join("|", nc.getProperties())+"\n");
                String labels = String.join(",", nc.getLabels());
                String line;
                if (nc.hasHeader())
                    br.readLine();
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(nc.getDelimiter());
                    String id = parts[idx];
                    if (nc.getProperties().size()==0)
                        fileWriter.write(id+"|"+labels+"\n");
                    else
                        fileWriter.write(id+"|"+labels+"|"
                            +Arrays.stream(parts).filter(x-> !Objects.equals(x, id)).collect(Collectors.joining("|"))+"\n");
                }
            } catch (IOException e) {
                System.err.println("An error occurred while reading node file "+nodeFilename);
            }
        }
        for (String edgeFilename : this.edgeConfigs.keySet()) {
            EdgeConfig ec = this.edgeConfigs.get(edgeFilename);
            int idx = ec.getProperties().indexOf("@id");
            boolean generateId = idx==-1;
            if (!generateId)
                ec.getProperties().remove(idx);
            int sdx = ec.getProperties().indexOf("@out");
            ec.getProperties().remove(sdx);
            int tdx = ec.getProperties().indexOf("@in");
            ec.getProperties().remove(tdx);
            try (BufferedReader br = new BufferedReader(new FileReader(edgeFilename))) {
                if (!generateId)
                    fileWriter.write("@id|");
                fileWriter.write("@label|@dir|@out|@in|"+String.join("|", ec.getProperties())+"\n");
                String line;
                if (ec.hasHeader())
                    br.readLine();
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(ec.getDelimiter());
                    String id = generateId? "" : parts[idx];
                    String src = parts[generateId? sdx : sdx+1];
                    String tgt = parts[generateId? tdx+1 : tdx+2];
                    String dir = ec.isDir()? "T": "F";
                    if (ec.getProperties().size()==0) {
                        if (!generateId)
                            fileWriter.write(id + "|");
                        fileWriter.write(ec.getLabels().get(0) + "|" + dir + "|" + src + "|" + tgt + "\n");
                    }
                    else {
                        if (!generateId)
                            fileWriter.write(id+"|");
                        fileWriter.write(ec.getLabels().get(0)+"|"+dir+"|"+src+"|"+tgt+"|"
                                +Arrays.stream(parts).filter(x-> !Objects.equals(x, id) && !Objects.equals(x, src) && !Objects.equals(x, tgt))
                                .collect(Collectors.joining("|"))+"\n");
                    }
                }
            } catch (IOException e) {
                System.err.println("An error occurred while reading edge file "+edgeFilename);
            }
        }
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Writer;

import Objects.Edge;
import Objects.GraphMemory;
import Objects.Node;
import Objects.Property;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ignacioburgos
 */
public class JsonWriter extends Writer{
    
    GraphMemory graph;
    
    public JsonWriter(GraphMemory graph, String pathFile) {
        super(pathFile);
        this.graph = graph;
    }

    @Override
    public void exportData() {
        
        try {
            try (FileWriter fw = new FileWriter(super.pathFile+".json")) {

                ArrayList<String> idNodes = this.graph.getIdNodes();
                
                StringBuilder sb = new StringBuilder();
                
                sb.append("[");
                
                for(String idNode: idNodes) {
                    
                Node node = this.graph.readNode(idNode);

                    sb.append("{\"type\":\"node\",\"id\":\"").append(node.getId()).append("\",\"labels\":");

                    List<String> labels = node.getLabels();
                    for(int i = 0; i < labels.size(); i++){

                        if(i == labels.size() - 1) {
                            sb.append("\"").append(labels.get(i)).append("\"");
                        }
                        else{
                            sb.append("\"").append(labels.get(i)).append("\",");
                        }
                    }

                    List<Property> property = node.getProperties();
                    
                    if(!property.isEmpty()){
                        
                        sb.append(",\"properties\":{");

                        for(int i = 0; i < property.size(); i++){

                            if(i == property.size() - 1){
                                sb.append("\"").append(property.get(i).getKey()).append("\": \"").append(property.get(i).getValue()).append("\"");
                            }
                            else{
                                sb.append("\"").append(property.get(i).getKey()).append("\": \"").append(property.get(i).getValue()).append("\",");
                            }
                        }
                        sb.append("}");
                    }
                    sb.append("},\n");
                    fw.write(sb.toString());
                    sb.setLength(0);
                }
                
                
                ArrayList<String> idEdges = this.graph.getIdEdges();

                int j = 0;
                for(String idEdge: idEdges) {
                    
                    Edge edge = this.graph.readEdge(idEdge);

                    sb.append("{\"id\":\"").append(edge.getId()).append("\",\"type\": \"relationship\",\"label\":\"").append(edge.getLabel()).append("\"");

                    List<Property> property = edge.getProperties();

                    if(!property.isEmpty()){
                        
                        sb.append(",\"properties\":{");

                        for(int i = 0; i < property.size(); i++){

                            if(i == property.size() - 1){
                                sb.append("\"").append(property.get(i).getKey()).append("\": \"").append(property.get(i).getValue()).append("\"");
                            }
                            else{
                                sb.append("\"").append(property.get(i).getKey()).append("\": \"").append(property.get(i).getValue()).append("\",");
                            }
                        }
                        sb.append("}");
                    }
                    
                    sb.append(",\"start\":{\"id\":\"").append(edge.getSource()).append("\"},\"end\":{\"id\":\"").append(edge.getTarget()).append("\"}}");
                    
                    if(j == idEdges.size() - 1) {
                        sb.append("\n");
                    }
                    else{
                        sb.append(",\n");
                    }
                    fw.write(sb.toString());
                    sb.setLength(0);
                    j++;
                }
                fw.write("]");
            }
        } catch (IOException ex) {
            Logger.getLogger(JsonWriter.class.getName()).log(Level.SEVERE, "I/O problems", ex);
        }
    }
    
}

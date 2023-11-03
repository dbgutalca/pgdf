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
public class GraphsonWriter extends Writer {
    
    GraphMemory graph;
    
    
    public GraphsonWriter(GraphMemory graph, String pathFile) {
        super(pathFile);
        this.graph = graph;
    }

    @Override
    public void exportData() {
        
        
        int propertiesCount = 0;

        try {
            try (FileWriter fw = new FileWriter(super.pathFile+"GSON.json")) {

                ArrayList<String> idNodes = this.graph.getIdNodes();
                
                StringBuilder sb = new StringBuilder();
                
                for(String idNode: idNodes) {
                    
                Node node = this.graph.readNode(idNode);
                    
                    sb.append("{\"id\":\"").append(node.getId()).append("\",\"label\": \"");
                    
                    for(int i = 0; i < node.getLabels().size(); i++){

                        if(i == node.getLabels().size() - 1) {
                            sb.append("\"").append(node.getLabels().get(i)).append("\"");
                            
                        }
                        else{
                            sb.append("\"").append(node.getLabels().get(i)).append("\",");
                        }
                        
                        List<Edge> edgesIn = node.getInEdges();
                        
                        if(!edgesIn.isEmpty()){

                            sb.append(",\"inE\": {");
                                                        
                            String labelTagNode = "";

                            for (Edge edgeIn: edgesIn){
                                
                                if(labelTagNode.equals("")){
                                    sb.append("\"").append(edgeIn.getLabel()).append("\":[");
                                    labelTagNode = edgeIn.getLabel();
                                }
                                else if(edgeIn.getLabel().equals(labelTagNode)){
                                    sb.append("},");
                                }
                                else{
                                    sb.append("}],\"").append(edgeIn.getLabel()).append("\":[");
                                    labelTagNode = edgeIn.getLabel();
                                }
                                
                                sb.append("{\"id\": \"").append(edgeIn.getId()).append("\",\"outV\": \"").append(edgeIn.getSource()).append("\"");

                                ArrayList<Property> properties = edgeIn.getProperties();
                                
                                if(!properties.isEmpty()){
                                    
                                    sb.append(",\"properties\":{");
                                    
                                    for(int j = 0; j < properties.size(); j++){
                                        
                                        if(j == properties.size() - 1) {
                                            sb.append("\"").append(properties.get(j).getKey()).append("\": \"").append(properties.get(j).getValue()).append("\"");
                                        }
                                        else{
                                            sb.append("\"").append(properties.get(j).getKey()).append("\": \"").append(properties.get(j).getValue()).append("\",");
                                        }
                                    }
                                    sb.append("}");
                                }
                            }
                            sb.append("}]}");
                        }
                        
                        List<Edge> edgesOut = node.getOutEdges();
                        
                        if(!edgesOut.isEmpty()){

                            sb.append(",\"outE\": {");
                            
                            String labelTagEdge = "";
                            
                            for (Edge edgeOut: edgesOut){
                                
                                if(labelTagEdge.equals("")){
                                    sb.append("\"").append(edgeOut.getLabel()).append("\":[");
                                    labelTagEdge = edgeOut.getLabel();
                                }
                                else if(edgeOut.getLabel().equals(labelTagEdge)){
                                    sb.append("},");
                                }
                                else{
                                    sb.append("}],\"").append(edgeOut.getLabel()).append("\":[");
                                    labelTagEdge = edgeOut.getLabel();
                                }
                                
                                sb.append("{\"id\":\"").append(edgeOut.getId()).append("\",\"inV\":\"").append(edgeOut.getTarget()).append("\"");

                                ArrayList<Property> properties = edgeOut.getProperties();
                                
                                if(!properties.isEmpty()){
                                    
                                    sb.append(",\"properties\":{");
                                    
                                    for(int j = 0; j < properties.size(); j++) {
                                        
                                        if(j == properties.size() - 1) {
                                            sb.append("\"").append(properties.get(j).getKey()).append("\": \"").append(properties.get(j).getValue()).append("\"");
                                        } 
                                        else{
                                            sb.append("\"").append(properties.get(j).getKey()).append("\": \"").append(properties.get(j).getValue()).append("\",");
                                        }
                                    }
                                    sb.append("}");
                                }
                            }
                            sb.append("}]}");
                        }
                    }
                    
                    ArrayList<Property> properties = node.getProperties();
                    
                    if(!properties.isEmpty()){
                        
                        sb.append(",\"properties\": {");
                        
                        
                        for(int j = 0; j < properties.size(); j++) {
                            
                            if(j == properties.size() - 1) {
                                sb.append("\"").append(properties.get(j).getKey()).append("\": [{");
                                sb.append("\"id\": {").append("\"@type\": \"g:Int64\", \"@value\": ").append(propertiesCount).append("},");
                                sb.append("\"value\": \"").append(properties.get(j).getValue()).append("\"}]");
                            }
                            else{
                                sb.append("\"").append(properties.get(j).getKey()).append("\": [{");
                                sb.append("\"id\": {").append("\"@type\": \"g:Int64\", \"@value\": ").append(propertiesCount).append("},");
                                sb.append("\"value\": \"").append(properties.get(j).getValue()).append("\"}],");
                            }
                            propertiesCount++;
                        }
                        sb.append("}");
                    }
                    sb.append("}\n");
                    fw.write(sb.toString());
                    sb.setLength(0);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(JsonWriter.class.getName()).log(Level.SEVERE, "I/O problems", ex);
        }
    }
    
}

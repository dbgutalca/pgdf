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
public class PgdfWriter extends Writer {
    
    GraphMemory graph;
    private String labels;
    
    public PgdfWriter(GraphMemory graph, String pathFile){
        super(pathFile);
        this.graph = graph;
        this.labels = "";
    } 
    
    @Override
    public void exportData() {
        
        try {
            try (FileWriter fw = new FileWriter(super.pathFile+".pgdf")) {
                
                StringBuilder sb = new StringBuilder();
                    
                ArrayList<String> idNodes = this.graph.getIdNodes();
                
                for(String idNode: idNodes){
                    
                    Node node = this.graph.readNode(idNode);
                
                    String label="";
                    List<String> readlabel = node.getLabels();
                    
                    for(int i = 0; i < readlabel.size(); i++){

                        if(i == readlabel.size() - 1) {
                            label = label+readlabel.get(i);
                        }
                        else{
                            label = label+(readlabel.get(i)+",");
                        }
                    }
                    
                    String key="";
                    String values="";
                    
                    List<Property> properties = node.getProperties();
                    
                    for(int i = 0; i < properties.size(); i++){
                        
                        if(i == properties.size() - 1) {
                            key = key+properties.get(i).getKey();
                        }
                        else{
                            key = key+properties.get(i).getKey()+"|";
                        }
                        values = values+properties.get(i).getValue()+"|";
                    }
                    
                    // false means that it is a header
                    if(!verifyLabel(label)){
                        
                        if(!key.equals("")){
                            sb.append("@id|@label|").append(key).append("\n");
                        }
                        else{
                            sb.append("@id|@label\n");
                        }
                        
                    }
                    sb.append(node.getId()).append("|").append(label).append("|").append(values).append("\n"); 
                    
                    fw.write(sb.toString());
                    sb.setLength(0);
                    
                }
                
                this.labels = "";
                
                ArrayList<String> idEdges = this.graph.getIdEdges();
                                
                for(String idEdge: idEdges){
                    
                    Edge edge = this.graph.readEdge(idEdge);
                    
                    String key="";
                    String values="";
                    
                    List<Property> properties = edge.getProperties();

                    for(int i = 0; i < properties.size(); i++){

                        if(i == properties.size() - 1) {
                            key = key+properties.get(i).getKey();
                        }
                        else{
                            key = key+properties.get(i).getKey()+"|";
                        }
                        values = values+properties.get(i).getValue()+"|";
                    }
                    
                    // false means that it is a header
                    if(!verifyLabel(edge.getLabel())){
                        
                        if(!key.equals("")){
                            sb.append("@id|@label|@undirected|@source|@target|").append(key).append("\n");
                        }
                        else{
                            sb.append("@id|@label|@undirected|@source|@target\n");
                        }
                    }
                    
                    sb.append(edge.getId()).append("|").append(edge.getLabel()).append("|T|").append(edge.getSource()).append("|").append(edge.getTarget()).append("|").append(values).append("\n");
                    fw.write(sb.toString());
                    sb.setLength(0);
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(JsonWriter.class.getName()).log(Level.SEVERE, "I/O problems", ex);
        } 
    }
    
    public boolean verifyLabel( String label){
        
        if(!this.labels.equals(label)){
            this.labels = label;
            return false;
            
        }
        return true;
    }
    
}

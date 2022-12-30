/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Writer;

import Direct.Memory;
import Objects.Edge;
import Objects.GraphMemory;
import Objects.Node;
import Objects.Property;
import java.io.FileNotFoundException;
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
public class GraphMLWriter extends Writer {
    
    
    GraphMemory graph;
    private final ArrayList<String> propertiesNode;
    private final ArrayList<String> propertiesEdge;
    
    public GraphMLWriter(GraphMemory graph, String pathFile){
        super(pathFile);
        this.graph = graph;
        this.propertiesEdge = new ArrayList<>();
        this.propertiesNode = new ArrayList<>();
    }

    @Override
    public void exportData() {
        
        try {
            try (FileWriter fw = new FileWriter(super.pathFile+".xml")) {
                    
                readPropertiesGraphML();
                StringBuilder sb = new StringBuilder();
                
                ArrayList<String> idNodes = this.graph.getIdNodes();
                
                for(String idNode: idNodes){

                    Node node = this.graph.readNode(idNode);
                    
                    sb.append("<node id=\"").append(node.getId()).append("\">\n");
                    sb.append("<data key=\"labelV\">");

                    List<String> labels = node.getLabels();

                    for(int i = 0; i < labels.size(); i++){

                        if(i == labels.size() - 1) {
                            sb.append(labels.get(i));
                        }
                        else{
                            sb.append(labels.get(i)).append(",");
                        }
                    }
                    sb.append("</data>\n");

                    List<Property> properties = node.getProperties();

                    for(Property property: properties){
                        sb.append("<data key=\"").append(property.getKey()).append("\">").append(property.getValue()).append("</data>\n");
                    }
                    sb.append("</node>\n");
                    
                    fw.write(sb.toString());
                    sb.setLength(0);
                    
                }
                
                ArrayList<String> idEdges = this.graph.getIdEdges();
                
                for(String idEdge: idEdges){
                    
                    Edge edge = this.graph.readEdge(idEdge);
                    
                    sb.append("<edge id=\"").append(edge.getId()).append("\" source=\"").append(edge.getSource()).append("\" target=\"").append(edge.getTarget()).append("\">\n");
                    sb.append("<data key=\"labelE\">").append(edge.getLabel()).append("</data>\n");
                    
                    List<Property> properties = edge.getProperties();
                    
                    for(Property property: properties){
                        
                        sb.append("<data key=\"").append(property.getKey()).append("\">").append(property.getValue()).append("</data>\n");
            
                    }
                    sb.append("</edge>\n");
                    fw.write(sb.toString());
                    sb.setLength(0);
                }
                sb.append("</graph>\n");
                sb.append("</graphml>\n");
                fw.write(sb.toString());
            }

        } catch (IOException ex) {
            Logger.getLogger(JsonWriter.class.getName()).log(Level.SEVERE, "I/O problems", ex);
        } 
    }
    
    
    public void readPropertiesGraphML(){
        
        try {
            try (FileWriter fw = new FileWriter(super.pathFile+".xml")) {
                

                StringBuilder sb = new StringBuilder();
                
                sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
                sb.append("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\" ");
                sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
                sb.append("xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns ");
                sb.append("http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\n");
                
                fw.write(sb.toString());
                sb.setLength(0);
                
                boolean isNode = true;
                boolean isEdge = true;
                
                ArrayList<String> idNodes = this.graph.getIdNodes();
                
                for(String idNode: idNodes){

                    Node node = this.graph.readNode(idNode);
                    
                    if(isNode){
                        sb.append("<key id=\"labelV\" for=\"node\" attr.name=\"labelV\" attr.type=\"string\"/>\n");
                        isNode = false;
                    }

                    List<Property> properties = node.getProperties();
                    
                    if(!properties.isEmpty()){

                        for(Property property: properties){

                            if(!this.propertiesNode.contains(property.getKey())){ 
                                sb.append("<key id=\"").append(property.getKey()).append("\" for=\"node\" attr.name=\"").append(property.getKey()).append("\" attr.type=\"string\"/>\n");
                                this.propertiesNode.add(property.getKey());
                            }
                        }
                    }
                }
                
                fw.write(sb.toString());
                sb.setLength(0);
                
                
                ArrayList<String> idEdges = this.graph.getIdEdges();
                
                for(String idEdge: idEdges){
                    
                    Edge edge = this.graph.readEdge(idEdge);
                    
                    if(isEdge){
                        fw.write("<key id=\"labelE\" for=\"edge\" attr.name=\"labelE\" attr.type=\"string\" />\n");
                        isEdge = false;
                    }
                    
                    List<Property> properties = edge.getProperties();
                    
                    
                    if(!properties.isEmpty()){
                    
                        for(Property property: properties){

                            if(!this.propertiesEdge.contains(property.getKey())){ 
                                sb.append("<key id=\"").append(property.getKey()).append("\" for=\"edge\" attr.name=\"").append(property.getKey()).append("\" attr.type=\"string\"/>\n");
                                this.propertiesEdge.add(property.getKey());
                            }
                        }
                    }
                }
                
                fw.write(sb.toString());
                sb.setLength(0);
                
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Memory.class.getName()).log(Level.SEVERE, "The file with the specified path name doesn't exist", ex);
        } catch (IOException ex) {
           Logger.getLogger(Memory.class.getName()).log(Level.SEVERE, "I/O Problem", ex);
        }
    }
       

}

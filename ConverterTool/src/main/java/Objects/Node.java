/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Objects;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author ignacioburgos
 */
public class Node {
    
    private final String id;
    private final ArrayList<String> labels;
    private final ArrayList<Edge> inEdges;
    private final ArrayList<Edge> outEdges;
    private final ArrayList<Property> properties;
    
    
    public Node(String id) {
        
        this.id = id;
        this.labels = new ArrayList<>();
        this.inEdges = new ArrayList<>();
        this.outEdges = new ArrayList<>();
        this.properties = new ArrayList<>();
        
    }
    
    public void addEdgeOutEdges(Edge edge) {
        this.outEdges.add(edge);
    }
    
    public void addEdgeInEdges(Edge edge) {
        this.inEdges.add(edge);
    }
    
    public boolean addLabel(String label) {
        if(this.labels.isEmpty()) {
            if(!this.labels.contains(label)){
                this.labels.add(label);
                return true;
            }
        }
        else{
            this.labels.add(label);
            return true;
        }
        return false;
    }
    
    public boolean addProperty(Property property) {
        if(!this.properties.contains(property)){
            this.properties.add(property);
            return true;
        }
        return false;
    }
    
    
    public boolean verifyLabels(String labels) {
        return labels.contains(",");
    }
    
    public String getId() {
        return id;
    }
    
    public ArrayList<String> getLabels() {
        return labels;
    }
    
    public ArrayList<Edge> getInEdges() {
        return inEdges;
    }
    
    public ArrayList<Edge> getOutEdges() {
        return outEdges;
    }
    
    public ArrayList<Property> getProperties() {
        return properties;
    }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ignacioburgos
 */
public class Node {
    
    private final String id;
    private final ArrayList<String> labels;
    private final ArrayList<Property> properties;
    
    
    public Node(String id) {
        
        this.id = id;
        this.labels = new ArrayList<>();
        this.properties = new ArrayList<>();
        
    }
    
    public boolean addLabel(String label) {
        return this.labels.add(label);
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
    
    public ArrayList<Property> getProperties() {
        return properties;
    }

    public void addLabels(List<String> labels) {
        this.labels.addAll(labels);
    }

    public void addProperties(List<Property> properties) {
        this.properties.addAll(properties);
    }

    @Override
    public String toString() {
        return "Node{" +
                "id='" + id + '\'' +
                ", labels=" + labels +
                ", properties=" + properties +
                '}';
    }
}

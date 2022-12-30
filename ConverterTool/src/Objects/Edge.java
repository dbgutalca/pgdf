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
public class Edge {
    
    private final String id;
    private final String label;
    private final String source;
    private final String target;
    private final ArrayList<Property> properties;
    private Boolean undirected;
    
    public Edge(String id, String label, String source, String target){
        this.id = id;
        this.label = label;
        this.source = source;
        this.target = target;
        this.properties = new ArrayList<>();
        this.undirected = false;
    }
    
    public boolean addProperty(Property property){
        
        if(!this.properties.contains(property)){
            this.properties.add(property);
            return true;
        }
        return false;
    }
    
    public void changeDirection(String direction){
        if(direction.equals("T")){
            setUndirected(true);
        }
        else{
            setUndirected(false);
        }
    }
    
     public void setUndirected(Boolean undirected) {
        this.undirected = undirected;
    }
    
    public String getId() {
       return id;
    }
    
    public String getLabel() {
        return label;
    }
    
    public String getSource(){
        return source;
    }
    
    public String getTarget(){
        return target;
    }
    
    public ArrayList<Property> getProperties() {
        return properties;
    }

    public Boolean getUndirected() {
        return undirected;
    }

}

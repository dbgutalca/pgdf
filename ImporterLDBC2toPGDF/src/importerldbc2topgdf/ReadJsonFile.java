/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package importerldbc2topgdf;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;


/**
 *
 * @author ignacio
 */
class ReadJsonFile {
    
    File jsonFile;
    boolean withHeaders;
    String undirected;
    Integer countNodes;
    Integer countEdges;
    
    ReadJsonFile(File json, boolean headers, String undirected){
        this.jsonFile = json;
        this.withHeaders = headers;
        this.undirected = undirected;
        this.countNodes = 0;
        this.countEdges = 0;
    }
    
    
     public void importarCSV(String pathFile){
        
        JsonParser parser = new JsonParser();
        ReadFile rf = new ReadFile(pathFile);
        
        long startTime;
        long endTime;
        long timeElapsed;
        
        try {
            
            startTime = System.nanoTime();
            
            JsonElement fileElement = parser.parse(new FileReader(jsonFile));
            JsonObject fileObject = fileElement.getAsJsonObject();
        
            JsonArray jsonArrayOfNodes = fileObject.get("nodes").getAsJsonArray();
            JsonArray jsonArrayOfEdges = fileObject.get("edges").getAsJsonArray();
            
            // Reading Nodes
            for(JsonElement nodeElement: jsonArrayOfNodes) {
                
                // Get the JsonObject
                JsonObject nodeJsonObject = nodeElement.getAsJsonObject();
                
                // Extract data
                String file = nodeJsonObject.get("file").getAsString();
                Integer id = nodeJsonObject.get("id").getAsInt();
                JsonArray labels = nodeJsonObject.get("labels").getAsJsonArray();
                JsonArray properties = nodeJsonObject.get("properties").getAsJsonArray();
                
                ArrayList<String> labelsNames = new ArrayList<>();
                
                for(int i = 0; i < labels.size(); i++){
                   labelsNames.add(labels.get(i).toString().replaceAll("\"", ""));
                }
                
                HashMap<String, Integer> propertiesNode = new HashMap<>();
                
                for(int i = 0; i < properties.size(); i++){
                   propertiesNode.put(properties.get(i).toString().replaceAll("\"", ""), i);
                }
                  
                countNodes += rf.readNode(file, id, labelsNames, propertiesNode);

            }

            // Reading Edges
            for(JsonElement edgeElement: jsonArrayOfEdges) {
                
                // Get the JsonObject
                JsonObject edgeJsonObject = edgeElement.getAsJsonObject();
                
                // Extract data
                String file = edgeJsonObject.get("file").getAsString();
                String label = edgeJsonObject.get("label").getAsString();
                Integer source = edgeJsonObject.get("source").getAsInt();
                Integer target = edgeJsonObject.get("target").getAsInt();
                JsonArray properties = edgeJsonObject.get("properties").getAsJsonArray();
                                
                HashMap<String, Integer> propertiesEdge = new HashMap<>();
                
                for(int i = 0; i < properties.size(); i++){
                   propertiesEdge.put(properties.get(i).toString().replaceAll("\"", ""), i);
                }       
                
                countEdges += rf.readEdge(file, source, target, label, propertiesEdge, "F");
                
            }
            
            endTime = System.nanoTime();
            System.out.println("Nodes: " + " " + countNodes);
            System.out.println("Edges: " + " " + countEdges);
            timeElapsed = endTime - startTime;
            System.out.println("time PGDF file: " + timeElapsed / 1000000 + " ms");
            
            rf.closeFile();
        
        } catch (FileNotFoundException ex) {
            throw new IllegalArgumentException("Schema file was not found!!");
        }

    }
    
}

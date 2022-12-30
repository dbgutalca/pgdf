/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Direct;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ignacioburgos
 */
public class Memory extends Converter {
    
    
    public Memory(String inputFile, String outputFile){
        super(inputFile, outputFile);
    }

    @Override
    public void PgdfToJson() {   
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(super.inputFile));
            try (FileWriter fw = new FileWriter(super.outputFile+".json")) {
                String line;
                StringBuilder sb = new StringBuilder();
                HashMap<String, Integer> properties = new HashMap<>();
                boolean isNode = false;             
                int countProperties;
                
                sb.append("[");
                
                long last = 0;
                long linesFile = linesJson() - 1;

                while((line = br.readLine()) != null) { 
                    
                    String[] props = line.split("\\|");
                    
                    if(props[0].equals("@id")){
                        properties.clear();
                        
                        for (int i = 0; i < props.length; i++) {
                            if(!props[i].contains("@"))
                                properties.put(props[i], i);
                        }
                        isNode = !line.contains("@source");
                    }
                    // is Node
                    else if(isNode){
                        
                        sb.append("{\"type\":\"node\",\"id\":\"").append(props[0]).append("\",\"labels\":");
                        
                        String[] labels = props[1].split(",");
                        for(int i = 0; i < labels.length; i++){
                            
                            if(i == labels.length - 1) {
                                sb.append("\"").append(labels[i]).append("\"");
                            }
                            else{
                                sb.append("\"").append(labels[i]).append("\",");
                            }
                        }
                        
                        if(!properties.isEmpty()){
                            sb.append(",\"properties\":{");
                            
                            countProperties = 0;
                            // add properties
                            for (String clave : properties.keySet()) {
                                
                                if(countProperties == properties.size() - 1){
                                    sb.append("\"").append(clave).append("\": \"").append(props[properties.get(clave)]).append("\"");
                                    
                                }
                                else{
                                    sb.append("\"").append(clave).append("\": \"").append(props[properties.get(clave)]).append("\",");
                                }
                                countProperties++;
                            }
                            sb.append("}");
                        }
                        sb.append("},\n");
                        fw.write(sb.toString());
                        sb.setLength(0);
                    }
                    // is Edge  
                    else{
                        sb.append("{\"id\":\"").append(props[0]).append("\",\"type\": \"relationship\",\"label\":\"").append(props[1]).append("\"");
                        
                        if(!properties.isEmpty()){
                            sb.append(",\"properties\":{");
                            
                            countProperties = 0;
                            // add properties
                            for (String clave : properties.keySet()) {
                                
                                if(countProperties == properties.size() - 1){
                                    sb.append("\"").append(clave).append("\": \"").append(props[properties.get(clave)]).append("\"");
                                    
                                }
                                else{
                                    sb.append("\"").append(clave).append("\": \"").append(props[properties.get(clave)]).append("\",");
                                }
                                countProperties++;
                            }
                            sb.append("}");
                        }
                        
                        sb.append(",\"start\":{\"id\":\"").append(props[3]).append("\"},\"end\":{\"id\":\"").append(props[4]).append("\"}}");
                        
                        if(linesFile == last){
                            sb.append("\n");
                        }
                        else{
                            sb.append(",\n");
                        }
                        
                        fw.write(sb.toString());
                        sb.setLength(0);
                    }
                    last +=1;
                }
                fw.write("]"); 
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Memory.class.getName()).log(Level.SEVERE, "The file with the specified path name doesn't exist", ex);
        } catch (IOException ex) {
           Logger.getLogger(Memory.class.getName()).log(Level.SEVERE, "I/O Problem", ex);
        }
    }
    
    
    public Long linesJson(){
        
        long lines = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(super.inputFile));
            
            lines = br.lines().count();
            
        } catch (FileNotFoundException ex) {
             Logger.getLogger(Memory.class.getName()).log(Level.SEVERE, "The file with the specified path name doesn't exist", ex);
        }
        
        return lines;
    }

    @Override
    public void PgdfToYarsPg() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(super.inputFile));
            try (FileWriter fw = new FileWriter(super.outputFile+".ypg")) {
                String line;
                StringBuilder sb = new StringBuilder();
                HashMap<String, Integer> properties = new HashMap<>();
                boolean isNode = false;
                int countProperties;
                
                while((line = br.readLine()) != null) { 
                    
                    String[] props = line.split("\\|");
                    
                    if(props[0].equals("@id")){
                        properties.clear();
                        
                        for (int i = 0; i < props.length; i++) {
                            if(!props[i].contains("@"))
                                properties.put(props[i], i);
                        }
                        isNode = !line.contains("@source");
                    }
                    // is Node
                    else if(isNode){
                        
                        sb.append("<\"").append(props[0]).append("\">{");
                       
                        String[] labels = props[1].split(",");
                        for(int i = 0; i < labels.length; i++){
                            
                            if(i == labels.length - 1) {
                                sb.append("\"").append(labels[i]).append("\"");
                            }
                            else{
                                sb.append("\"").append(labels[i]).append("\",");
                            }
                        }
                        sb.append("}");
                        
                        if(!properties.isEmpty()){
                            sb.append("[");
                            
                            countProperties = 0;
                            // add properties
                            for (String clave : properties.keySet()) {
                                
                                if(countProperties == properties.size() - 1){
                                    sb.append("\"").append(clave).append("\": \"").append(props[properties.get(clave)]).append("\"");
                                }
                                else{
                                    sb.append("\"").append(clave).append("\": \"").append(props[properties.get(clave)]).append("\",");
                                }
                                countProperties++;
                            }
                            sb.append("]");  
                        }
                        sb.append("\n");
                        fw.write(sb.toString());
                        sb.setLength(0);  
                    }
                    // is Edge
                    else{
                        sb.append("(\"").append(props[3]).append("\")-{\"").append(props[1]).append("\"}");
                        
                        if(!properties.isEmpty()){
                            sb.append("[");
                            
                            countProperties = 0;
                            // add properties
                            for (String clave : properties.keySet()) {
                                
                                if(countProperties == properties.size() - 1){
                                    sb.append("\"").append(clave).append("\": \"").append(props[properties.get(clave)]).append("\"");
                                }
                                else{
                                    sb.append("\"").append(clave).append("\": \"").append(props[properties.get(clave)]).append("\",");
                                }
                                countProperties++;
                            }
                            sb.append("]");
                        }
                        sb.append("->(\"").append(props[4]).append("\")\n"); 
                        fw.write(sb.toString());
                        sb.setLength(0);
                    }
                }
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Memory.class.getName()).log(Level.SEVERE, "The file with the specified path name doesn't exist", ex);
        } catch (IOException ex) {
           Logger.getLogger(Memory.class.getName()).log(Level.SEVERE, "I/O Problem", ex);
        }
    }

    @Override
    public void PgdfToGraphML() {
        
        readPropertiesGraphML();
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(super.inputFile));
            try (FileWriter fw = new FileWriter(super.outputFile+".xml")) {
                String line;
                StringBuilder sb = new StringBuilder();
                HashMap<String, Integer> properties = new HashMap<>();
                boolean isNode = false;
                
                while((line = br.readLine()) != null) { 
                    
                    String[] props = line.split("\\|");
                    
                    if(props[0].equals("@id")){
                        properties.clear();
                        for (int i = 0; i < props.length; i++) {
                            if(!props[i].contains("@"))
                                properties.put(props[i], i);
                        }
                        isNode = !line.contains("@source");
                    }
                    // is Node
                    else if(isNode){
                        
                        sb.append("<node id=\"").append(props[0]).append("\">\n");
                        sb.append("<data key=\"labelV\">");
                       
                        String[] labels = props[1].split(",");
                        for(int i = 0; i < labels.length; i++){
                            
                            if(i == labels.length - 1) {
                                sb.append(labels[i]);
                            }
                            else{
                                sb.append(labels[i]).append(",");
                            }
                        }
                        sb.append("</data>\n");
                        
                        if(!properties.isEmpty()){
                            
                            // add properties
                            for (String clave : properties.keySet()) {
                                sb.append("<data key=\"").append(clave).append("\">").append(props[properties.get(clave)]).append("</data>\n");
                            }
                        }
                        sb.append("</node>\n");  
                        fw.write(sb.toString());
                        sb.setLength(0);  
                    }
                    // is Edge
                    else{
                        sb.append("<edge id=\"").append(props[0]).append("\" source=\"").append(props[3]).append("\" target=\"").append(props[4]).append("\">\n");
                        sb.append("<data key=\"labelE\">").append(props[1]).append("</data>\n");
                        
                        if(!properties.isEmpty()){
                            
                            // add properties
                            for (String clave : properties.keySet()) {
                                sb.append("<data key=\"").append(clave).append("\">").append(props[properties.get(clave)]).append("</data>\n");
                            }
                        }
                        sb.append("</edge>\n");
                        fw.write(sb.toString());
                        sb.setLength(0);
                    }
                }
                fw.write("</graph>\n");
                fw.write("</graphml>\n");
                fw.close();
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Memory.class.getName()).log(Level.SEVERE, "The file with the specified path name doesn't exist", ex);
        } catch (IOException ex) {
           Logger.getLogger(Memory.class.getName()).log(Level.SEVERE, "I/O Problem", ex);
        }
    } 
    
    public void readPropertiesGraphML(){
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(super.inputFile));
            try (FileWriter fw = new FileWriter(super.outputFile+".xml")) {
                
                ArrayList<String> propertiesNode =  new ArrayList<>();
                ArrayList<String> propertiesEdge = new ArrayList<>();
                boolean node = true;
                boolean edge = true;
                String line;

                fw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
                fw.write("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\" ");
                fw.write("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
                fw.write("xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns ");
                fw.write("http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\n");

                while((line = br.readLine()) != null) { 
                    
                    String[] props = line.split("\\|");
                    
                    if(props[0].equals("@id")){
                        
                        if(!line.contains("@source")) {
                            
                            if(node){
                                fw.write("<key id=\"labelV\" for=\"node\" attr.name=\"labelV\" attr.type=\"string\"/>\n");
                                node = false;
                            }
                            
                            for (String prop : props) {
                                if ((!prop.contains("@")) && (!propertiesNode.contains(prop))) {
                                    fw.write("<key id=\""+prop+"\" for=\"node\" attr.name=\""+prop+"\" attr.type=\"string\"/>\n");
                                    propertiesNode.add(prop);
                                }
                            }
                        }
                        else{
                            if(edge){
                                fw.write("<key id=\"labelE\" for=\"edge\" attr.name=\"labelE\" attr.type=\"string\" />\n");
                                edge = false;
                            }
                            for (String prop : props) {
                                if ((!prop.contains("@")) && (!propertiesEdge.contains(prop))) {
                                    fw.write("<key id=\""+prop+"\" for=\"edge\" attr.name=\""+prop+"\" attr.type=\"string\"/>\n");
                                    propertiesEdge.add(prop);
                                }
                            }
                        }
                    }
                }
                fw.write("<graph id=\"G\" edgedefault=\"undirected\">\n");
                fw.close();
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Memory.class.getName()).log(Level.SEVERE, "The file with the specified path name doesn't exist", ex);
        } catch (IOException ex) {
           Logger.getLogger(Memory.class.getName()).log(Level.SEVERE, "I/O Problem", ex);
        }
    }

    @Override
    public void PgdfToGraphSon() {
        throw new UnsupportedOperationException("It is not possible to export from PGDF to GRAPHSON directly.");
    }
    
    @Override
    public void PgdfToPgdf() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

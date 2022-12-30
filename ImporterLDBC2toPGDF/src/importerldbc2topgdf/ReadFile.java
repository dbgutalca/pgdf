/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package importerldbc2topgdf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ignacio
 */
public class ReadFile {
       
    FileWriter fw;
    Integer idEdge;
    Integer nodes;
    Integer edges;
    

    ReadFile(String rutaFile) {
        try { 
            this.fw = new FileWriter(rutaFile);
            this.idEdge = 0;
            this.nodes = 0;
            this.edges = 0;
        } catch (IOException ex) {
            Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Integer readNode(String file, Integer id, ArrayList labels, HashMap properties ){
        
        String nodeHeaders = generateNodeHeaders(properties);
        
        String[] headers = nodeHeaders.split("\\|");
        
        String label = "";
        for(int i = 0; i < labels.size(); i++){
            if(i == labels.size() - 1){
                label = label+labels.get(i).toString().replaceAll("\"","");
            }
            else{
                label = label+labels.get(i).toString().replaceAll("\"","")+",";
            }
        }
        
        boolean headersNode = true;
        
        nodes = 0;
        
        try {
            
            fw.write(nodeHeaders+"\n");
            
            BufferedReader br = new BufferedReader(new FileReader(file));

            String linea;
            
            try {
                while ((linea  = br.readLine()) != null) {
                    
                    if(!headersNode) {
                        
                        String[] valores = linea.split("\\|");
                        
                        String lineaFile = "";
                        
                        for(int i = 0; i < headers.length; i++) {
                            
                            switch (headers[i]) {
                                case "@id" -> {
                                    Integer valueId = getValue("@id", properties);
                                    lineaFile = lineaFile+id+"-"+valores[valueId]+"|";
                                }
                                case "@label" -> lineaFile = lineaFile+label+"|";
                                default -> {
                                        
                                    Integer value = getValue(headers[i], properties);
                                    if(valores[value].equals("")){
                                        lineaFile = lineaFile+" "+"|";
                                    }
                                    else{
                                       lineaFile = lineaFile+valores[value]+"|";
                                    }
                                    
                                            
                                }
                            }
                        }
                        fw.write(lineaFile+"\n");
                        nodes++;
                        
                    }else{
                        headersNode = false;
                    }
                }

                
            } catch (IOException ex) {
                Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
            }            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return nodes;
       
    }
    
    
    public Integer getValue(String key, HashMap prop){

        Iterator it = prop.entrySet().iterator();
        
        while (it.hasNext()) {
	        Map.Entry<String, Integer> entry = (Map.Entry)it.next();
                
                if(entry.getKey().equals(key)) {
                    return entry.getValue();
                }
        }
        return -1;
    }
    
    public String generateNodeHeaders(HashMap prop){
        String headers = "@id|@label";
        
        Iterator it = prop.entrySet().iterator();
        
        while (it.hasNext()) {
	        Map.Entry<String, Integer> entry = (Map.Entry)it.next();
                
                if(!entry.getKey().equals("@id")) {
                    headers = headers+ "|"+entry.getKey(); 
                }
        }
        return headers;
    }   
    
    public String generateEdgeHeaders(HashMap prop) {
        String headers = "@id|@label|@undirected|@source|@target";
        Iterator it = prop.entrySet().iterator();
        
        while (it.hasNext()) {
	        Map.Entry<String, Integer> entry = (Map.Entry)it.next();
                
                if(!(entry.getKey().equals("@target")) && !(entry.getKey().equals("@source"))) {
                    headers = headers+"|"+entry.getKey(); 
                }
        }
        return headers;
        
    }
    
    public Integer readEdge(String file, Integer source, Integer target, String label, HashMap properties, String undirected){
    
        String edgeHeaders = generateEdgeHeaders(properties);
        
        String[] headers = edgeHeaders.split("\\|");
        
        boolean headersNode = true;
        
        
        edges = 0;
        
        try {
            
            fw.write(edgeHeaders+"\n");
            
            BufferedReader br = new BufferedReader(new FileReader(file));
            
            String linea;
            
            try {
                while ((linea  = br.readLine()) != null) {
                    
                    if(!headersNode) {
                        
                        String[] valores = linea.split("\\|");
                        
                        String lineaFile = "";
                        
                        for(int i = 0; i < headers.length; i++) {
                            
                            switch (headers[i]) {
                                case "@id" -> {
                                    lineaFile = lineaFile+"e"+idEdge+"|";
                                }
                                case "@label" -> lineaFile = lineaFile+label+"|";
                                case "@source" -> {
                                    Integer sourceId = getValue("@source", properties);
                                    lineaFile = lineaFile+source+"-"+valores[sourceId]+"|";
                                }
                                case "@target" -> {
                                    Integer targetId = getValue("@target", properties);
                                    lineaFile = lineaFile+target+"-"+valores[targetId]+"|";
                                }
                                case "@undirected" -> {
                                    lineaFile = lineaFile+undirected+"|";
                                }
                                default -> {
                                    Integer value = getValue(headers[i], properties);
                                    lineaFile = lineaFile+valores[value]+"|";
                                }
                            }
                        }
                        fw.write(lineaFile+"\n");
                        idEdge++;
                        edges++;
                        
                    }else{
                        headersNode = false;
                    }
                }
                
            } catch (IOException ex) {
                Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
            }            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return edges;
        
    }
    
   
    public void closeFile(){
        try {
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

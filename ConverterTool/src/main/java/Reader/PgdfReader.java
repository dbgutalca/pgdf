/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Reader;

import Objects.Edge;
import Objects.GraphMemory;
import Objects.Node;
import Objects.Property;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ignacioburgos
 */
public class PgdfReader extends Reader{
    
    GraphMemory graph;
    private boolean headersInitial;
    private boolean isNode;
    private HashMap<String, Integer> propsLabels;
    private String[] valuesOfLine;
    
    public PgdfReader(GraphMemory item, String pathFile){
        super(pathFile);
        this.graph = item;
        this.headersInitial = false;
        this.isNode = false;
        this.valuesOfLine = null;
    }

    @Override
    public void importData() {
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(super.pathFile));
            
            String linea;
            
            while((linea = br.readLine()) != null) {
                
                if(!linea.isEmpty()){
              
                    this.valuesOfLine = linea.split("\\|");
                    
                    if(linea.contains("@id")){
                        this.headersInitial = true;
                        this.propsLabels = new HashMap<>();
                        int format = 0; 

                        for (int i = 0; i < this.valuesOfLine.length; i++) {
                            if (this.valuesOfLine[i].contains("@")){
                                format++;
                            }
                            this.propsLabels.put(this.valuesOfLine[i], i);
                        }

                        this.isNode = verifyFormat(format);
                        
                    }
                    else if(this.headersInitial) {

                        Integer id = getIndexProperty("@id");
                        Integer label = getIndexProperty("@label");

                        if(this.isNode) {

                            // create Node
                            Node newNode = new Node(this.valuesOfLine[id]);

                            // add labels
                            String nameLabels = valuesOfLine[label];

                            if(newNode.verifyLabels(nameLabels)){

                                String[] values = nameLabels.split(",");

                                for (String value : values) {
                                    newNode.addLabel(value);
                                }

                            }
                            else{
                                newNode.addLabel(nameLabels);
                            }

                            
                            // add properties
                            for (String clave : this.propsLabels.keySet()) {
                                int valor = this.propsLabels.get(clave);

                                if(!clave.contains("@")){
                                    Property newProperty = new Property(clave, this.valuesOfLine[valor]);
                                    newNode.addProperty(newProperty);
                                } 
                            }   

                            this.graph.addNode(newNode);

                        }
                        else{

                            Integer undirected = getIndexProperty("@undirected");
                            Integer source = getIndexProperty("@source");
                            Integer target = getIndexProperty("@target");

                            Edge newEdge = new Edge(this.valuesOfLine[id], this.valuesOfLine[label], this.valuesOfLine[source], this.valuesOfLine[target]);

                            // set edge direction 
                            newEdge.changeDirection(this.valuesOfLine[undirected]);

                            // add properties
                            for (String clave : this.propsLabels.keySet()) {
                                int valor = this.propsLabels.get(clave);

                                if(!clave.contains("@")){
                                    Property newProperty = new Property(clave, this.valuesOfLine[valor]);
                                    newEdge.addProperty(newProperty);
                                } 
                            }

                            this.graph.addEdge(newEdge);

                        }
                    }
                    else{
                        throw new IllegalArgumentException("The file must contain an initial header");
                    }
                }
                else{
                    throw new IllegalArgumentException("Error reading the data. The file contains lines without data");
                }
            }
              
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PgdfReader.class.getName()).log(Level.SEVERE, "The file with the specified path name doesn't exist", ex);
        } catch (IOException ex) {
            Logger.getLogger(PgdfReader.class.getName()).log(Level.SEVERE, "I/O Problem", ex);
        }
        
    }
    
    private boolean verifyFormat(Integer propertiesLabels){
        
        if(propertiesLabels == 2){
            return true;
        }
        else if(propertiesLabels == 5) {
            return false;
        }
        throw new IllegalArgumentException("Wrong format");
    }
    
    private Integer getIndexProperty(String key){
        
        if(this.propsLabels.containsKey(key)) {
            return this.propsLabels.get(key);
        }
        throw new IllegalArgumentException("property index not found"); 
    }
    

}

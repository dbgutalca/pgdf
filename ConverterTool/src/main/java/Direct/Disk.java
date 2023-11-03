/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Direct;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ignacioburgos
 */
public class Disk extends Converter{
    
    Connection conectar;
    private final ArrayList<String> propertiesNode;
    private final ArrayList<String> propertiesEdge;
    private String labels;
    
    
    public Disk(String inputFile, String outputFile){
        super(inputFile, outputFile);
        this.propertiesEdge = new ArrayList<>();
        this.propertiesNode = new ArrayList<>();
        this.labels = "";
        Graph();
    }
    
    public final void Graph(){
        
        try {    
            File nameDatabase = new File("storageDisk.db");
            nameDatabase.delete();

            String url = "jdbc:sqlite:storageDisk.db";

            this.conectar = DriverManager.getConnection(url);
            Statement stmt = this.conectar.createStatement();

            // Table Node
            String sql = """
                         CREATE TABLE IF NOT EXISTS node (
                         \tnodeId text PRIMARY KEY,
                         \tnodeLabels text,
                         \tproperty_values text
                         );""";
            stmt.execute(sql);

            // Table Edge
            sql = """
                  CREATE TABLE IF NOT EXISTS edge (
                  \tid text PRIMARY KEY, 
                   edgeLabel text, 
                   source text, 
                   target text,
                   property_values text
                  );""";
            stmt.execute(sql);
            
            
            // INDEX EDGE TABLE
            sql = "Create index source_idex on edge(source)";
            stmt.execute(sql);
            
            sql = "Create index target_idex on edge(target)";
            stmt.execute(sql);
            
            sql = "Create index propertiesNode_idex on node(property_values)";
            stmt.execute(sql);
            
            sql = "Create index propertiesEdge_idex on edge(property_values)";
            stmt.execute(sql);
            
            this.conectar.setAutoCommit(false);
            importPgdf();

        } catch (SQLException ex) {
            Logger.getLogger(Disk.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    @Override
    public void PgdfToJson() {
                
        try (FileWriter fw = new FileWriter(super.outputFile+".json")) {
            
            PreparedStatement selectNodes = this.conectar.prepareStatement("SELECT * FROM node");
            ResultSet resultNode = selectNodes.executeQuery();
            
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            
            while(resultNode.next()){
                
                sb.append("{\"type\":\"node\",\"id\":\"").append(resultNode.getString(1)).append("\",\"labels\":");
                        
                String[] labels = resultNode.getString(2).split(",");
                for(int i = 0; i < labels.length; i++){

                    if(i == labels.length - 1) {
                        sb.append("\"").append(labels[i]).append("\"");
                    }
                    else{
                        sb.append("\"").append(labels[i]).append("\",");
                    }
                }

                if(!resultNode.getString(3).isEmpty()){
                    sb.append(",\"properties\":{");
                    
                    if(!resultNode.getString(3).contains("|")){
                        
                        String[] property = resultNode.getString(3).split(":=:");
                        sb.append("\"").append(property[0]).append("\": \"").append(property[1]).append("\"");
                    }
                    else{
                        String[] properties = resultNode.getString(3).split("\\|");
                        
                        for(int i = 0; i < properties.length; i++){
                            
                            String[] property = properties[i].split(":=:");
                            
                            if(i == properties.length - 1){
                                
                                sb.append("\"").append(property[0]).append("\": \"").append(property[1]).append("\"");
                            }
                            else{
                                sb.append("\"").append(property[0]).append("\": \"").append(property[1]).append("\",");
                            }
                        }
                    }
                    sb.append("}");
                }
                sb.append("},\n");
                fw.write(sb.toString());
                sb.setLength(0);
            }
            
            int rowcount = RowsEdge();

            PreparedStatement selectEdges = this.conectar.prepareStatement("SELECT * FROM edge");
            ResultSet resultEdge = selectEdges.executeQuery();
                        
            int filasEdges = 0;
            while(resultEdge.next()){
                
                sb.append("{\"id\":\"").append(resultEdge.getString(1)).append("\",\"type\": \"relationship\",\"label\":\"").append(resultEdge.getString(2)).append("\"");
                
                if(!resultEdge.getString(5).isEmpty()){
                    
                    sb.append(",\"properties\":{");
                    
                    if(resultEdge.getString(5).contains("|")){
                        String[] properties = resultEdge.getString(5).split("\\|");
                        
                        for(int i = 0; i < properties.length; i++){
                            
                            String[] property = properties[i].split(":=:");
                            
                            if(i == properties.length - 1){
                                sb.append("\"").append(property[0]).append("\": \"").append(property[1]).append("\"");
                            }
                            else{
                                sb.append("\"").append(property[0]).append("\": \"").append(property[1]).append("\",");
                            }
                        }
                    }
                    else{
                        String[] property = resultEdge.getString(5).split(":=:");
                        sb.append("\"").append(property[0]).append("\": \"").append(property[1]).append("\"");
                    }
                    sb.append("}");
                }
                sb.append(",\"start\":{\"id\":\"").append(resultEdge.getString(3)).append("\"},\"end\":{\"id\":\"").append(resultEdge.getString(4)).append("\"}}");
               
                if(filasEdges == rowcount - 1){
                    sb.append("\n");
                }
                else{
                    sb.append(",\n");
                }
                filasEdges++;
                fw.write(sb.toString());
                sb.setLength(0);
            }
            fw.write("]"); 

        } catch (IOException ex) {
            Logger.getLogger(Disk.class.getName()).log(Level.SEVERE, "I/O Problem", ex);
        } catch (SQLException ex) {
            Logger.getLogger(Disk.class.getName()).log(Level.SEVERE, "problems when connecting to the database", ex);
        }          
    }
    

    public Integer RowsEdge(){
        
        int counts = 0;
        PreparedStatement countEdges;
        try {
            countEdges = this.conectar.prepareStatement("SELECT * FROM edge");
            ResultSet resultEdge = countEdges.executeQuery();
            
            while(resultEdge.next()){
                counts++;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Disk.class.getName()).log(Level.SEVERE, null, ex);
        }
        return counts;
    }


    @Override
    public void PgdfToYarsPg() {
        
        try (FileWriter fw = new FileWriter(super.outputFile+".ypg")) {
            
            PreparedStatement selectNodes = this.conectar.prepareStatement("SELECT * FROM node");
            ResultSet resultNode = selectNodes.executeQuery();
            
            StringBuilder sb = new StringBuilder();
            
            while(resultNode.next()){
                
                sb.append("<\"").append(resultNode.getString(1)).append("\">{");        
                
                String[] labels = resultNode.getString(2).split(",");
                for(int i = 0; i < labels.length; i++){

                    if(i == labels.length - 1) {
                        sb.append("\"").append(labels[i]).append("\"");
                    }
                    else{
                        sb.append("\"").append(labels[i]).append("\",");
                    }
                }
                sb.append("}");

                if(!resultNode.getString(3).isEmpty()){
                    sb.append("[");
                    
                    if(!resultNode.getString(3).contains("|")){
                        
                        String[] property = resultNode.getString(3).split(":=:");
                        sb.append("\"").append(property[0]).append("\": \"").append(property[1]).append("\"");
                    }
                    else{
                        String[] properties = resultNode.getString(3).split("\\|");
                        
                        for(int i = 0; i < properties.length; i++){
                            
                            String[] property = properties[i].split(":=:");
                            
                            if(i == properties.length - 1){
                                
                                sb.append("\"").append(property[0]).append("\": \"").append(property[1]).append("\"");
                            }
                            else{
                                sb.append("\"").append(property[0]).append("\": \"").append(property[1]).append("\",");
                            }
                        }
                    }
                    sb.append("]"); 
                }
                sb.append("\n");
                fw.write(sb.toString());
                sb.setLength(0);  
            }
            
            PreparedStatement selectEdges = this.conectar.prepareStatement("SELECT * FROM edge");
            ResultSet resultEdge = selectEdges.executeQuery();

            while(resultEdge.next()){
                sb.append("(\"").append(resultEdge.getString(3)).append("\")-{\"").append(resultEdge.getString(2)).append("\"}");
                
                if(!resultEdge.getString(5).isEmpty()){
                    sb.append("[");
                    
                    if(!resultEdge.getString(5).contains("|")){
                        
                        String[] property = resultEdge.getString(5).split(":=:");
                        sb.append("\"").append(property[0]).append("\": \"").append(property[1]).append("\"");
                    }
                    else{
                        String[] properties = resultEdge.getString(5).split("\\|");
                        
                        for(int i = 0; i < properties.length; i++){
                            
                            String[] property = properties[i].split(":=:");
                            
                            if(i == properties.length - 1){
                                sb.append("\"").append(property[0]).append("\": \"").append(property[1]).append("\"");
                            }
                            else{
                                sb.append("\"").append(property[0]).append("\": \"").append(property[1]).append("\",");
                            }
                        }
                    }
                    sb.append("]");
                }
                sb.append("->(\"").append(resultEdge.getString(4)).append("\")\n");
                fw.write(sb.toString());
                sb.setLength(0);
            }
        } catch (IOException ex) {
            Logger.getLogger(Disk.class.getName()).log(Level.SEVERE, "I/O Problem", ex);
        } catch (SQLException ex) {
            Logger.getLogger(Disk.class.getName()).log(Level.SEVERE, "problems when connecting to the database", ex);
        }  
    }
    
     @Override
    public void PgdfToPgdf() {
        
        try (FileWriter fw = new FileWriter(super.outputFile+".pgdf")) {
                        
            PreparedStatement selectNodes = this.conectar.prepareStatement("SELECT * FROM node");
            ResultSet resultNode = selectNodes.executeQuery();
                        
            StringBuilder sb = new StringBuilder();
            
            String key="";
            String values="";

            while(resultNode.next()){
                
                if(!resultNode.getString(3).isEmpty()){
                    
                    if(!resultNode.getString(3).contains("|")){
                        
                        String[] property = resultNode.getString(3).split(":=:");
                        key = key+property[0];
                        values = values+property[1]+"|";
                        
                    }
                    else{
                        String[] props = resultNode.getString(3).split("\\|");
                        
                        for(int i = 0; i < props.length; i++){
                            
                            String[] property = props[i].split(":=:");
                                                        
                            if((i == props.length - 1)){
                                
                                key = key+property[0];
                            }
                            else{
                                key = key+property[0]+"|";
                            }
                            values = values+property[1]+"|";
                        }
                    }
                }
                if(!verifyLabel(resultNode.getString(2))){
                    
                    if(!key.equals("")){
                        sb.append("@id|@label|").append(key).append("\n");
                    }
                    else{
                        sb.append("@id|@label\n");
                    }
                }
                sb.append(resultNode.getString(1)).append("|").append(resultNode.getString(2)).append("|").append(values).append("\n");
                
                fw.write(sb.toString());
                sb.setLength(0);
                key="";
                values="";
            }
            
            key = "";
            values = "";
            this.labels = "";

            PreparedStatement selectEdges = this.conectar.prepareStatement("SELECT * FROM edge");
            ResultSet resultEdge = selectEdges.executeQuery();
            
            while(resultEdge.next()){
                
                if(!resultEdge.getString(5).isEmpty()){
                    
                    if(!resultEdge.getString(5).contains("|")){
                        
                        String[] property = resultEdge.getString(5).split(":=:");
                        
                        key = key+property[0];
                        values = values+property[1]+"|";
                    }
                    else{
                        String[] props = resultEdge.getString(5).split("\\|");
                        
                        for(int i = 0; i < props.length; i++){
                            
                            String[] property = props[i].split(":=:");
                            
                            if(i == props.length - 1){
                                key = key+property[0];
                            }
                            else{
                                key = key+property[0]+"|";
                            }
                            values = values+property[1]+"|";
                        }
                    }
                }
                if(!verifyLabel(resultEdge.getString(2))){
                    
                    if(!key.equals("")){
                        sb.append("@id|@label|@undirected|@source|@target|").append(key).append("\n");
                    }
                    else{
                        sb.append("@id|@label|@undirected|@source|@target\n");
                    }
                }
                sb.append(resultEdge.getString(1)).append("|").append(resultEdge.getString(2)).append("|T|").append(resultEdge.getString(3)).append("|").append(resultEdge.getString(4)).append("|").append(values).append("\n");
                fw.write(sb.toString());
                sb.setLength(0);
                key="";
                values="";
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Disk.class.getName()).log(Level.SEVERE, "I/O Problem", ex);
        } catch (SQLException ex) {
            Logger.getLogger(Disk.class.getName()).log(Level.SEVERE, "problems when connecting to the database", ex);
        }  
    }
    
    public boolean verifyLabel(String labels){
        
        if(!this.labels.equals(labels)){
            this.labels = labels;
            return false;
            
        }
        return true;
    }
    
    

    @Override
    public void PgdfToGraphML() {
        
         try (FileWriter fw = new FileWriter(super.outputFile+".xml")) {
             
            PreparedStatement selectpropsNode = this.conectar.prepareStatement("SELECT property_values FROM node");
            ResultSet resultPropsNode = selectpropsNode.executeQuery();
                
            StringBuilder sb = new StringBuilder();
            
            sb.append(("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"));
            sb.append("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\" ");
            sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
            sb.append("xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns ");
            sb.append("http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\n");
            boolean node = true;
            boolean edge = true;
            
            fw.write(sb.toString());
            sb.setLength(0);
            
            while(resultPropsNode.next()){
                
                if(!resultPropsNode.getString(1).isEmpty()){
                    
                    if(node){
                        fw.write("<key id=\"labelV\" for=\"node\" attr.name=\"labelV\" attr.type=\"string\"/>\n");
                        node = false;
                    }
                    
                    else if(!resultPropsNode.getString(1).contains("|")){
                        
                        String[] property = resultPropsNode.getString(1).split(":=:");
                        
                        addPropertyNode(property[0], sb);
                    }
                    else{
                        String[] properties = resultPropsNode.getString(1).split("\\|");
                        
                        for (String propert : properties) {
                            String[] property = propert.split(":=:");
                            addPropertyNode(property[0], sb);
                        }
                    }
                }
            }
                        
            PreparedStatement selectpropsEdge = this.conectar.prepareStatement("SELECT property_values FROM edge");
            ResultSet resultPropsEdge = selectpropsEdge.executeQuery();
            
            while(resultPropsEdge.next()){
                
                if(!resultPropsEdge.getString(1).isEmpty()){
                    
                    if(edge){
                        sb.append("<key id=\"labelE\" for=\"edge\" attr.name=\"labelE\" attr.type=\"string\" />\n");
                        edge = false;
                    }
                    
                    else if(!resultPropsEdge.getString(1).contains("|")){
                        
                        String[] property = resultPropsEdge.getString(1).split(":=:");
                        
                        addPropertyEdge(property[0], sb);
                    }
                    else{
                        String[] properties = resultPropsEdge.getString(1).split("\\|");
                        
                        for (String propert : properties) {
                            String[] property = propert.split(":=:");
                            addPropertyEdge(property[0], sb);
                        }
                    }
                }
            }
            
            sb.append("<graph id=\"G\" edgedefault=\"undirected\">\n");
            
            fw.write(sb.toString());
            sb.setLength(0);
            
            //nodes and edges
            
            PreparedStatement selectNodes = this.conectar.prepareStatement("SELECT * FROM node");
            ResultSet resultNode = selectNodes.executeQuery();
            
            while(resultNode.next()){
                
                sb.append("<node id=\"").append(resultNode.getString(1)).append("\">\n");
                sb.append("<data key=\"labelV\">").append(resultNode.getString(2)).append("</data>\n");
                
                if(!resultNode.getString(3).isEmpty()){
                    
                    if(!resultNode.getString(3).contains("|")){
                        
                        String[] property = resultNode.getString(3).split(":=:");
                        sb.append("<data key=\"").append(property[0]).append("\">").append(property[1]).append("</data>\n");
                    }
                    else{
                        String[] properties = resultNode.getString(3).split("\\|");
                        
                        for (String propert : properties) {
                            String[] property = propert.split(":=:");
                            sb.append("<data key=\"").append(property[0]).append("\">").append(property[1]).append("</data>\n");
                        }
                    } 
                }
                sb.append("</node>\n");
                fw.write(sb.toString());
                sb.setLength(0);
            }
            
            PreparedStatement selectEdges = this.conectar.prepareStatement("SELECT * FROM edge");
            ResultSet resultEdge = selectEdges.executeQuery();
            
            
            while(resultEdge.next()) {
                
                sb.append("<edge id=\"").append(resultEdge.getString(1)).append("\" source=\"").append(resultEdge.getString(3)).append("\" target=\"").append(resultEdge.getString(4)).append("\">\n");
                sb.append("<data key=\"labelE\">").append(resultEdge.getString(2)).append("</data>\n");
             
                if(!resultEdge.getString(5).isEmpty()){
                    
                    if(!resultEdge.getString(5).contains("|")){
                        
                        String[] property = resultEdge.getString(5).split(":=:");
                        sb.append("<data key=\"").append(property[0]).append("\">").append(property[1]).append("</data>\n");
                    }
                    else{
                        String[] properties = resultEdge.getString(5).split("\\|");
                        
                        for (String propert : properties) {
                            String[] property;
                            property = propert.split(":=:");
                            sb.append("<data key=\"").append(property[0]).append("\">").append(property[1]).append("</data>\n");
                        }
                    } 
                }
                sb.append("</edge>\n");
                fw.write(sb.toString());
                sb.setLength(0);
                
            }
            sb.append("</graph>\n");
            sb.append("</graphml>\n");
            fw.write(sb.toString());
            fw.close();
            
        } catch (IOException ex) {
            Logger.getLogger(Disk.class.getName()).log(Level.SEVERE, "I/O Problem", ex);
        } catch (SQLException ex) {
            Logger.getLogger(Disk.class.getName()).log(Level.SEVERE, "problems when connecting to the database", ex);
        } 
    }
    
    public void addPropertyEdge(String props, StringBuilder sb) {
        
        if(!this.propertiesEdge.contains(props)){
            sb.append("<key id=\"").append(props).append("\" for=\"edge\" attr.name=\"").append(props).append("\" attr.type=\"string\"/>\n");
            this.propertiesEdge.add(props);
        }
    }
    
    public void addPropertyNode(String props, StringBuilder sb) {
          
        if(!this.propertiesNode.contains(props)){
            sb.append("<key id=\"").append(props).append("\" for=\"node\" attr.name=\"").append(props).append("\" attr.type=\"string\"/>\n");
            this.propertiesNode.add(props);
        }
    }

    @Override
    public void PgdfToGraphSon() {
        
        long propertiesCount = 0;
        try (FileWriter fw = new FileWriter(super.outputFile+"GSON.json")) {
            
            PreparedStatement selectNodes = this.conectar.prepareStatement("SELECT * FROM node");
            ResultSet resultNode = selectNodes.executeQuery();
            
            StringBuilder sb = new StringBuilder();
            
            while(resultNode.next()){
                
                sb.append("{\"id\":\"").append(resultNode.getString(1)).append("\",\"label\": \"");      
                
                String[] labels = resultNode.getString(2).split(",");
                for(int i = 0; i < labels.length; i++){

                    if(i == labels.length - 1) {
                        sb.append("\"").append(labels[i]).append("\"");
                    }
                    else{
                        sb.append("\"").append(labels[i]).append("\",");
                    }
                    
                    // hay que hacer una llamada a la base de datos para obtener el target
                    PreparedStatement selectEdgeIn = this.conectar.prepareStatement("SELECT * FROM edge WHERE target='"+resultNode.getString(1)+"'");
                    ResultSet resultEdgesIn = selectEdgeIn.executeQuery();
                    
                    String label = "";
                    boolean edgesIn = true;
                    while(resultEdgesIn.next()){
                                                
                        if(edgesIn){
                            sb.append(",\"inE\": {");
                            edgesIn = false;
                        }
                        if(label.equals("")){
                            sb.append("\"").append(resultEdgesIn.getString(2)).append("\":[");
                            label = resultEdgesIn.getString(2);
                        }
                        else if(resultEdgesIn.getString(2).equals(label)){
                            sb.append("},");
                        }
                        else if(!resultEdgesIn.getString(2).equals(label)){
                            sb.append("}],\"").append(resultEdgesIn.getString(2)).append("\":[");
                            label = resultEdgesIn.getString(2);
                        }
                                      
                        sb.append("{\"id\": \"").append(resultEdgesIn.getString(1)).append("\",\"outV\": \"").append(resultEdgesIn.getString(3)).append("\"");
                        
                        if(!resultEdgesIn.getString(5).isEmpty()){
                           
                            sb.append(",\"properties\":{");
                             
                            if(!resultEdgesIn.getString(5).contains("|")){
                        
                                String[] property = resultEdgesIn.getString(5).split(":=:");
                                sb.append("\"").append(property[0]).append("\": \"").append(property[1]).append("\"");
                            }
                            else{
                                
                                String[] properties = resultEdgesIn.getString(5).split("\\|");

                                for(int j = 0; j < properties.length; j++){

                                    String[] property = properties[j].split(":=:");

                                    if(j == properties.length - 1){
                                        sb.append("\"").append(property[0]).append("\": \"").append(property[1]).append("\"");
                                    }
                                    else{
                                        sb.append("\"").append(property[0]).append("\": \"").append(property[1]).append("\",");
                                    }
                                }
                            }
                            sb.append("}");
                        }
                    }
                    if(!edgesIn){
                        sb.append("}]}");
                    } 
                                
                    // hay que hacer una llamada a la base de datos para obtener el source
                    PreparedStatement selectEdgeOut = this.conectar.prepareStatement("SELECT * FROM edge WHERE source='"+resultNode.getString(1)+"'");
                    ResultSet resultEdgesOut = selectEdgeOut.executeQuery();
                    
                    String labelEdge = "";
                    boolean edgesOut = true;
                    while(resultEdgesOut.next()){
                       
                        if(edgesOut){
                            sb.append(",\"outE\": {");
                            edgesOut = false;
                        }
                        
                        if(labelEdge.equals("")){
                            sb.append("\"").append(resultEdgesOut.getString(2)).append("\":[");
                            labelEdge = resultEdgesOut.getString(2);
                        }
                        else if(resultEdgesOut.getString(2).equals(labelEdge)){
                            sb.append("},");
                        }
                        else if(!resultEdgesOut.getString(2).equals(labelEdge)){
                            sb.append("}],\"").append(resultEdgesOut.getString(2)).append("\":[");
                            labelEdge = resultEdgesOut.getString(2);
                        }
                        
                        sb.append("{\"id\":\"").append(resultEdgesOut.getString(1)).append("\",\"inV\":\"").append(resultEdgesOut.getString(4)).append("\"");
                    
                        if(!resultEdgesOut.getString(5).isEmpty()){
                           
                            sb.append(",\"properties\":{");
                             
                            if(!resultEdgesOut.getString(5).contains("|")){
                        
                                String[] property = resultEdgesOut.getString(5).split(":=:");
                                sb.append("\"").append(property[0]).append("\": \"").append(property[1]).append("\"");
                            }
                            else{
                                
                                String[] properties = resultEdgesOut.getString(5).split("\\|");

                                for(int j = 0; j < properties.length; j++){

                                    String[] property = properties[j].split(":=:");

                                    if(j == properties.length - 1){
                                        sb.append("\"").append(property[0]).append("\": \"").append(property[1]).append("\"");
                                    }
                                    else{
                                        sb.append("\"").append(property[0]).append("\": \"").append(property[1]).append("\",");
                                    }
                                }
                            }
                            sb.append("}");
                        }
                    }
                    if(!edgesOut){
                        sb.append("}]}");
                    } 
                    if(!resultNode.getString(3).isEmpty()){
                        sb.append(",\"properties\": {");

                        if(!resultNode.getString(3).contains("|")){
                            
                            String[] property = resultNode.getString(3).split(":=:");
                            sb.append("\"").append(property[0]).append("\": \"").append(property[1]).append("\"");
                        }
                        else{
                            String[] properties = resultNode.getString(3).split("\\|");

                            for(int k = 0; k < properties.length; k++){

                                String[] property = properties[k].split(":=:");

                                if(k == properties.length - 1){

                                    sb.append("\"").append(property[0]).append("\": [{");
                                    sb.append("\"id\": {").append("\"@type\": \"g:Int64\", \"@value\": ").append(propertiesCount).append("},");
                                    sb.append("\"value\": \"").append(property[1]).append("\"}]");
                                }
                                else{
                                    sb.append("\"").append(property[0]).append("\": [{");
                                    sb.append("\"id\": {").append("\"@type\": \"g:Int64\", \"@value\": ").append(propertiesCount).append("},");
                                    sb.append("\"value\": \"").append(property[1]).append("\"}],");
                                }
                                propertiesCount++;
                            }
                        }
                        sb.append("}");
                    }
                    sb.append("}\n");
                    fw.write(sb.toString());
                    sb.setLength(0);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Disk.class.getName()).log(Level.SEVERE, "I/O Problem", ex);
        } catch (SQLException ex) {
            Logger.getLogger(Disk.class.getName()).log(Level.SEVERE, "problems when connecting to the database", ex);
        }      
    }

    public void importPgdf(){
        
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(super.inputFile));
            
            String line;
            
            StringBuilder stmtNodeValues = new StringBuilder();
            StringBuilder stmtEdgeValues = new StringBuilder();
            
            PreparedStatement pstmtNode = this.conectar.prepareStatement("INSERT INTO node VALUES(?,?,?);");
            PreparedStatement pstmtEdge = this.conectar.prepareStatement("INSERT INTO edge VALUES(?,?,?,?,?);");
            HashMap<String, Integer> properties = new HashMap<>();
            boolean isNode = false;
            
            int sendData = 0;
            while((line = br.readLine()) != null) {
                
                String[] props = line.split("\\|");
                
                 //es un header
                    if(props[0].equals("@id")){
                        properties.clear();
                       
                        for (int i = 0; i < props.length; i++) {
                            if(!props[i].contains("@"))
                                properties.put(props[i], i);
                        }
    
                        isNode = !line.contains("@source");
                    }
                    else if(isNode){
                        
                        pstmtNode.clearParameters();
                        pstmtNode.setString(1,props[0]);
                        pstmtNode.setString(2, props[1]);
                        
                        if(!properties.isEmpty()){
                            // add properties
                            
                            int count = 0;
                            int countProperties = properties.size();
                           
                            for (String key : properties.keySet()) {
                                int valor = properties.get(key);
                                
                                if(count == countProperties - 1){
                                    stmtNodeValues.append(key).append(":=:").append(props[valor]);
                                }else{
                                    stmtNodeValues.append(key).append(":=:").append(props[valor]).append("|");
                                }
                                count++;
                            }
                            pstmtNode.setString(3, stmtNodeValues.toString());
                        }
                        else{
                            pstmtNode.setString(3, "");
                        }
                        pstmtNode.executeUpdate();
                        stmtNodeValues.setLength(0);
                    }
                    else{
                                                
                        pstmtEdge.clearParameters();
                        pstmtEdge.setString(1,props[0]);
                        pstmtEdge.setString(2, props[1]);
                        pstmtEdge.setString(3, props[3]);
                        pstmtEdge.setString(4, props[4]);
                        
                        if(!properties.isEmpty()){
                            // add properties
                            int count = 0;
                            int countProperties = properties.size();
                            for (String key : properties.keySet()) {
                                int valor = properties.get(key);
                                
                                if(count == countProperties - 1){
                                    stmtEdgeValues.append(key).append(":=:").append(props[valor]);
                                }else{
                                    stmtEdgeValues.append(key).append(":=:").append(props[valor]).append("|");
                                }
                                count++;
                            }
                            pstmtEdge.setString(5, stmtEdgeValues.toString());
                        }
                        else{
                            pstmtEdge.setString(5, "");
                        }
                        pstmtEdge.executeUpdate();
                        stmtEdgeValues.setLength(0);
                    }
                    if((sendData % 100000) == 0 ){
                        this.conectar.commit();
                    }
                    sendData++;
            }
            this.conectar.commit();
            
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Disk.class.getName()).log(Level.SEVERE, "The file with the specified path name doesn't exist", ex);
        } catch (SQLException ex) {
            Logger.getLogger(Disk.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Disk.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        /*
        try {
            BufferedReader br = new BufferedReader(new FileReader(super.inputFile));
            
            String line;
            String[] props;
            StringBuilder sb =  new StringBuilder();
            HashMap<String, Integer> properties = new HashMap<>();
            boolean isNode = false;
            
            try {
                int sendData = 0;
                while((line = br.readLine()) != null) {
                    
                    props = line.split("\\|");
                    
                    //es un header
                    if(props[0].equals("@id")){
                        properties.clear();
                       
                        for (int i = 0; i < props.length; i++) {
                            if(!props[i].contains("@"))
                                properties.put(props[i], i);
                        }
    
                        isNode = !line.contains("@source");
                    }
                    else if(isNode){
                        
                        if(!properties.isEmpty()){
                            // add properties
                            
                            int count = 0;
                            int countProperties = properties.size();
                           
                            for (String key : properties.keySet()) {
                                int valor = properties.get(key);
                                
                                if(count == countProperties - 1){
                                    sb.append(key).append(":=:").append(props[valor]);
                                }else{
                                    sb.append(key).append(":=:").append(props[valor]).append("|");
                                }
                                count++;
                            }
                        }
            
                        try {
                            // sql statement to insert data
                            PreparedStatement insertNode = this.conectar.prepareStatement("INSERT INTO node VALUES(?,?,?);");
            
                            // Insert Node into database
                            insertNode.setString(1, props[0]);
                            insertNode.setString(2, props[1]);
                            insertNode.setString(3, sb.toString());
                            insertNode.executeUpdate();
                            
                        } catch (SQLException ex) {
                            Logger.getLogger(Disk.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        sb.setLength(0);
                    }
                    else{
                   
                        if(!properties.isEmpty()){
                            // add properties
                            
                            int count = 0;
                            int countProperties = properties.size();
                            for (String key : properties.keySet()) {
                                int valor = properties.get(key);
                                
                                if(count == countProperties - 1){
                                    sb.append(key).append(":=:").append(props[valor]);
                                }else{
                                    sb.append(key).append(":=:").append(props[valor]).append("|");
                                }
                                count++;
                            }
                        }

                        try {
                            PreparedStatement insertEdge = this.conectar.prepareStatement("INSERT INTO edge VALUES(?,?,?,?,?);");
                            
                            // Insert Node into database
                            insertEdge.setString(1, props[0]);
                            insertEdge.setString(2, props[1]);
                            insertEdge.setString(3, props[3]);
                            insertEdge.setString(4, props[4]);
                            insertEdge.setString(5, sb.toString());
                            insertEdge.executeUpdate();

                        } catch (SQLException ex) {
                            Logger.getLogger(Disk.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        sb.setLength(0);
                    }
                    sendData++;
                    
                    if( (sendData % 10000) == 0 ){
                        try {
                            this.conectar.commit();
                        } catch (SQLException ex) {
                            Logger.getLogger(Disk.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                try {
                    this.conectar.commit();
                } catch (SQLException ex) {
                    Logger.getLogger(Disk.class.getName()).log(Level.SEVERE, null, ex);
                } 
            } catch (IOException ex) {
                Logger.getLogger(Disk.class.getName()).log(Level.SEVERE, "I/O Problem", ex);
            }
        } catch (FileNotFoundException ex) {
             Logger.getLogger(Disk.class.getName()).log(Level.SEVERE, "The file with the specified path name doesn't exist", ex);
        }
        */
    }
}

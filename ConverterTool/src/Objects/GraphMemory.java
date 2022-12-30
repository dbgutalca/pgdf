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
public class GraphMemory extends Graph{
    
    
    public HashMap<String, Node> nodeIdToNode;
    public HashMap<String, Edge> edgeIdToEdge;
    public ArrayList<String> idNodes;
    public ArrayList<String> idEdges;
    public Integer quantityInitial;

    @Override
    public void Graph() {
        
        this.quantityInitial = 0;
        this.nodeIdToNode = new HashMap<>();
        this.edgeIdToEdge = new HashMap<>();
        this.idNodes = new ArrayList<>();
        this.idEdges = new ArrayList<>();
        
    }

    @Override
    public void addNode(Node node) {
        
        //verify if the id node is in the HashMap
        if(this.nodeIdToNode.containsKey(node.getId())) {
            throw new IllegalArgumentException("Duplicate node, error on insertion");
        }
        
        // add Node to HashMap
        this.nodeIdToNode.put(node.getId(), node);
        
        // add IdNode to ArrayList
        this.idNodes.add(node.getId());
        
    }

    @Override
    public void addEdge(Edge edge) {
        
        final Node fromNode = readNode(edge.getSource());
        final Node toNode = readNode(edge.getTarget());
       
        // check if one of the nodes is null
        if(fromNode == null || toNode == null) {
            throw new IllegalArgumentException("The edge " + edge.getId() + " cannot be linked because one of the nodes doesn't exist");
        }
        
        // add the edge to the nodes according to whether it's a directed or undirected edge
        if(edge.getUndirected()) {
            
            fromNode.addEdgeOutEdges(edge);
            toNode.addEdgeInEdges(edge);
            
            toNode.addEdgeOutEdges(edge);
            fromNode.addEdgeInEdges(edge);
        }
        else{
            
            fromNode.addEdgeOutEdges(edge);
            toNode.addEdgeInEdges(edge);
        }
       
        this.edgeIdToEdge.put(edge.getId(), edge);
        
                
        // add IdEdge to ArrayList
        this.idEdges.add(edge.getId());

    }

    @Override
    public Node readNode(String idNode) {
        
        if(this.nodeIdToNode.containsKey(idNode)) {
            return this.nodeIdToNode.get(idNode);
        }
        throw new IllegalArgumentException("Error reading node. The node is null");
        
    }

    @Override
    public Edge readEdge(String idEdge) {
        
         if(this.edgeIdToEdge.containsKey(idEdge)){
            return this.edgeIdToEdge.get(idEdge);
        }
        throw new IllegalArgumentException("Error reading edge. The edge is null");
        
    }

    public ArrayList<String> getIdNodes() {
        return idNodes;
    }

    public ArrayList<String> getIdEdges() {
        return idEdges;
    }


}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Objects;

/**
 *
 * @author ignacioburgos
 */
public abstract class Graph {
    
    
    public abstract void Graph();
    
    public abstract void addNode(Node node);
    public abstract void addEdge(Edge edge);
    
    public abstract Node readNode(String idNode);
    public abstract Edge readEdge(String idEdge);
    
}

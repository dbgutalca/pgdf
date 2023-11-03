/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Direct;

/**
 *
 * @author ignacioburgos
 */
public abstract class Converter {
    
    final String inputFile;
    final String outputFile;
    
    public Converter(String input, String output){
        this.inputFile = input;
        this.outputFile = output;
    }
    
    public abstract void PgdfToJson();
    public abstract void PgdfToYarsPg();
    public abstract void PgdfToGraphML();
    public abstract void PgdfToGraphSon();
    public abstract void PgdfToPgdf();
    
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Writer;

/**
 *
 * @author ignacioburgos
 */
public abstract class Writer {
    
    public final String pathFile;
    
    public Writer(String pathFile){
        this.pathFile = pathFile;
    }
    
    public abstract void exportData();
    
}

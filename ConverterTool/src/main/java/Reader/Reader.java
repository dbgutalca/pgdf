/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Reader;

/**
 *
 * @author ignacioburgos
 */
public abstract class Reader {
    
    public final String pathFile;
    
    public Reader(String pathFile){
        this.pathFile = pathFile;
    }
    
    public abstract void importData();
}

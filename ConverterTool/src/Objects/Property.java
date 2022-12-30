/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Objects;

/**
 *
 * @author ignacioburgos
 */
public class Property {
    
    private final String key;
    private final String value;
    
    
    public Property(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
    public String getKey(){
        return key;
    }
    
    public String getValue(){
        return value;
    }
    
}

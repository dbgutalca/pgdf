/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package importerldbc2topgdf;

import java.io.File;

/**
 *
 * @author ignacio
 */
public class ImporterLDBC2toPGDF {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        File input = new File(args[0]);
        boolean withHeaders =  true;
        String undirected = "F";
        String rutaFile = args[1];
        ReadJsonFile json = new ReadJsonFile(input, withHeaders, undirected);
        json.importarCSV(rutaFile);
    }
}

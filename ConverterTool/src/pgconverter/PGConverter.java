/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pgconverter;

import Direct.Converter;
import Direct.Disk;
import Direct.Memory;
import Objects.GraphMemory;
import Reader.PgdfReader;
import Writer.GraphMLWriter;
import Writer.GraphsonWriter;
import Writer.JsonWriter;
import Writer.PgdfWriter;
import Writer.Writer;
import Writer.YarsPGWriter;


/**
 *
 * @author ignacioburgos
 */
public class PGConverter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      
        GraphMemory memory = new GraphMemory();
        memory.Graph();

               
        //esto me permite probar los grafos en Google Cloud Platform
        
        if(args.length != 4){
            help();
        }
        
        switch (args[0]) {
            case "--direct" -> {
                
                Converter graph = new Memory(args[2], args[3]);
                
                // Time to export PGDF

                long startTimePgdf = System.nanoTime();
                graph.PgdfToPgdf();
                long endTimePgdf = System.nanoTime();
                long timePgdf = endTimePgdf - startTimePgdf;
                System.out.println("Time PGDF file(direct): " + timePgdf / 1000000 + " ms");

                //----------------------

                // Time to export Yars-PG
                
                long startTimeYarsPG = System.nanoTime();
                graph.PgdfToYarsPg();
                long endTimeYarsPG = System.nanoTime();
                long timeYarsPG = endTimeYarsPG - startTimeYarsPG;
                System.out.println("Time Yars-PG file(direct): " + timeYarsPG / 1000000 + " ms");
                
                //----------------------

                // Time to export JSON
                
                long startTimeJSON = System.nanoTime();
                graph.PgdfToJson();
                long endTimeJSON = System.nanoTime();
                long timeJSON = endTimeJSON - startTimeJSON;
                System.out.println("Time JSON file(direct): " + timeJSON / 1000000 + " ms");
                
                //----------------------
                
                // Time to export GraphML
                
                long startTimeGraphML = System.nanoTime();
                graph.PgdfToGraphML();
                long endTimeGraphML = System.nanoTime();
                long timeGraphML = endTimeGraphML - startTimeGraphML;
                System.out.println("Time GraphML file(direct): " + timeGraphML / 1000000 + " ms");
                
                //----------------------
                
                // Time to export GraphSON
                
                long startTimeGraphSON = System.nanoTime();
                PgdfReader reader = new PgdfReader(memory, args[2]);
                reader.importData();
                Writer gw = new GraphsonWriter(memory,args[3]);
                gw.exportData();
                long endTimeGraphSON = System.nanoTime();
                long timeGraphSON = endTimeGraphSON - startTimeGraphSON;
                System.out.println("Time GraphSON file(direct): " + timeGraphSON / 1000000 + " ms");
    
                //----------------------
                
            }
            case "--disk" -> {
                Converter graph = new Disk(args[2], args[3]);
                
                // Time to export PGDF
                
                long startTimePgdf = System.nanoTime();
                graph.PgdfToPgdf();
                long endTimePgdf = System.nanoTime();
                long timePgdf = endTimePgdf - startTimePgdf;
                System.out.println("Time PGDF file(disk): " + timePgdf / 1000000 + " ms");
                 
                //----------------------

                // Time to export Yars-PG
                
                long startTimeYarsPG = System.nanoTime();
                graph.PgdfToYarsPg();
                long endTimeYarsPG = System.nanoTime();
                long timeYarsPG = endTimeYarsPG - startTimeYarsPG;
                System.out.println("Time Yars-PG file(disk): " + timeYarsPG / 1000000 + " ms");
                
                //----------------------

                // Time to export JSON
                
                long startTimeJSON = System.nanoTime();
                graph.PgdfToJson();
                long endTimeJSON = System.nanoTime();
                long timeJSON = endTimeJSON - startTimeJSON;
                System.out.println("Time JSON file(disk): " + timeJSON / 1000000 + " ms");
                
                //----------------------
                
                // Time to export GraphML
                
                long startTimeGraphML = System.nanoTime();
                graph.PgdfToGraphML();
                long endTimeGraphML = System.nanoTime();
                long timeGraphML = endTimeGraphML - startTimeGraphML;
                System.out.println("Time GraphML file(disk): " + timeGraphML / 1000000 + " ms");
                
                //----------------------
                
                // Time to export GraphSON

                /*
                long startTimeGraphSON = System.nanoTime();
                graph.PgdfToGraphSon();
                long endTimeGraphSON = System.nanoTime();
                long timeGraphSON = endTimeGraphSON - startTimeGraphSON;
                System.out.println("Time GraphSON file(disk): " + timeGraphSON / 1000000 + " ms");

                */
                //----------------------
            }
            default -> {
                PgdfReader reader = new PgdfReader(memory, args[2]);
                reader.importData();
                    
                // Time to export PGDF
                    
                long startTimePgdf = System.nanoTime();
                Writer pw = new PgdfWriter(memory, args[3]);
                pw.exportData();
                long endTimePgdf = System.nanoTime();
                long timePgdf = endTimePgdf - startTimePgdf;
                System.out.println("Time PGDF file(memory): " + timePgdf / 1000000 + " ms");
                    
                //----------------------

                // Time to export Yars-PG
                    
                long startTimeYarsPG = System.nanoTime();
                Writer yw = new YarsPGWriter(memory, args[3]);
                yw.exportData();
                long endTimeYarsPG = System.nanoTime();
                long timeYarsPG = endTimeYarsPG - startTimeYarsPG;
                System.out.println("Time Yars-PG file(memory): " + timeYarsPG / 1000000 + " ms");
                    
                //----------------------
                    
                // Time to export JSON
                    
                long startTimeJSON = System.nanoTime();
                Writer jw = new JsonWriter(memory, args[3]);
                jw.exportData();
                long endTimeJSON = System.nanoTime();
                long timeJSON = endTimeJSON - startTimeJSON;
                System.out.println("Time JSON file(memory): " + timeJSON / 1000000 + " ms");
                    
                //----------------------
                    
                // Time to export GraphML
                        
                long startTimeGraphML = System.nanoTime();
                Writer gmw = new GraphMLWriter(memory, args[3]);
                gmw.exportData();
                long endTimeGraphML = System.nanoTime();
                long timeGraphML = endTimeGraphML - startTimeGraphML;
                System.out.println("Time GraphML file(memory): " + timeGraphML / 1000000 + " ms");
                    
                //----------------------
                
                // Time to export GraphSON
                    
                long startTimeGraphSON = System.nanoTime();
                Writer gsw = new GraphsonWriter(memory,args[3]);
                gsw.exportData();
                long endTimeGraphSON = System.nanoTime();
                long timeGraphSON = endTimeGraphSON - startTimeGraphSON;
                System.out.println("Time GraphSON file(memory): " + timeGraphSON / 1000000 + " ms");

                //----------------------
                
            }
        }

    }
    
    public static void help(){

        System.out.println("Execute instruction and arguments:");
        System.out.println("java -jar ConverterTool.jar <storage> <file format output> <input path> <output path>");
        System.out.println("storage: Indicate the storage type to use (--disk, --memory or --direct).");
        System.out.println("file format output: Indicate the export file format (--graphml, --json, --graphson, --yarspg).");
        System.out.println("input path: Indicate the path to the input pgdf file.");
        System.out.println("output path: Indicate the path where the file's to be saved. (the file mustn't have the extension, only the name under which it will be saved)");

    }
}

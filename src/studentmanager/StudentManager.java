/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package studentmanager;

import java.io.File;

/**
 *
 * @author ZiloTech
 */
public class StudentManager {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File dll = new File("native/opencv_java4120.dll");
if (dll.exists()) {
    System.load(dll.getAbsolutePath());
    System.out.println("Found");
} else {
    System.err.println("DLL not found: " + dll.getAbsolutePath());
}
    }
    
}

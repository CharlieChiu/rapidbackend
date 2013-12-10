package com.rapidbackend.util.io;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
/**
 * 
 * @author chiqiu
 *
 */
public class ObjectIoUtil {
    public static void writeObj(Object q, String fileName) throws IOException{
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
        oos.writeObject(q);
        oos.flush();
        oos.close();
    }
    public static Object loadObj(String file) throws FileNotFoundException,IOException, ClassNotFoundException{
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        Object qa = ois.readObject();
        ois.close();
        return qa;
    }
}

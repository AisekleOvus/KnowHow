import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;

/**
 * AppendableSerialization is for cases, when you need to append serializable objects
 * to single file.
 * The Class has two constructors one of wich accepts String pathToFile and Object serializableObject,
 * another accepts File file and Object serializableObject
 * Method appendableObjectWriting(), actually, write your Object to File. It can write to empty file
 * or in file where some objects just had been written
 *
 */
class AppendableSerialization {
    private final String path;
    private final Object object;
    private final boolean appendable;
    private final File file;

    public AppendableSerialization(String path, Object object) {
        this.path = path;
        this.object = object;
        this.file = new File(path);
        this.appendable = isFileAppendable();
    }
    public AppendableSerialization(File file, Object object) {
        this.file = file;
        this.path = file.getPath();
        this.object = object;
        this.appendable = isFileAppendable();
    }

// Check if there are any 'objects' in file
    private boolean isFileAppendable() {
       if(file.exists() && !file.isDirectory()) {
           try(FileInputStream fis = new FileInputStream(path)) {
               BufferedInputStream bis = new BufferedInputStream(fis);
               ObjectInputStream ois = new ObjectInputStream(bis);
               ois.readObject();
           } catch(Exception e) {
               return false;
           }
       return true;  
       }
       return false;      
    }

    public boolean isAppendable() { return appendable; }

    public boolean appendableObjectWriting() throws FileNotFoundException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(object);
        oos.close();
        byte[] output = baos.toByteArray();
        if(appendable) {
            output = Arrays.copyOfRange(output, 1, output.length);
            output[0] = 119;
            output[1] = 1;
            output[2] = 1;
        }
        if(file.length() == 0 || appendable) {      
            FileOutputStream fos = new FileOutputStream(path,true);
            fos.write(output);
            fos.close();
            return true;
        }
        return false;
    }
}

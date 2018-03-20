package encryptedobjectstreamexample;

import java.io.*;     import java.security.*;
import javax.crypto.*;import static javax.crypto.Cipher.*;
import javax.crypto.spec.*;

/**
 * @author CodeLiberator
 */
public class EncryptedObjectStreamExample {
    static boolean cipherOn = true;
    static final String fn = "C:/Temp/object.";
    static final byte[] keyBytes = "MySecretPass1234".getBytes();
    static final byte[] iv = "initialialvector".getBytes();
    static String testObject = "Test";
    Cipher c;
    AlgorithmParameters ap;
    IvParameterSpec ivp;
    Key k;

    /**
     * Constructor initializes the crypto structures
     */
    public EncryptedObjectStreamExample() {
        try {
            c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            ap = AlgorithmParameters.getInstance("AES");
            ivp = new IvParameterSpec(iv);
            ap.init(ivp);
            k = new SecretKeySpec(keyBytes, "AES");
        } catch (Exception ex) {
            System.err.println("Failed Constructor:\n" + ex);
            System.exit(1);
        }
    }

    public void writeTest() {
        // Object -> Serialize -> Cipher Stream -> File
        try {
            c.init(ENCRYPT_MODE, k, ap);
            OutputStream ostrp = new FileOutputStream(fn+"p");
            OutputStream ostrx = new CipherOutputStream(new FileOutputStream(fn+"x"),c);
            try (ObjectOutputStream oos = new ObjectOutputStream(ostrp)) {
                oos.writeObject(new SealedObject(testObject, c));
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(ostrx)) {
                oos.writeObject(new SealedObject(testObject, c));
            }
        } catch (Exception e) {
            System.err.println("Exception in writeDb: \n" + e);
        }
    }

    private void readTest() {
        // File -> DeCipher Stream -> DeSerialize -> Object
        String result = "";
        try {
            c.init(DECRYPT_MODE, k, ap);
            InputStream istrp = new FileInputStream("C:/Temp/object.p");
            InputStream istrx = new CipherInputStream(new FileInputStream("C:/Temp/object.x"),c);
            try (ObjectInputStream ois = new ObjectInputStream(istrp)) {
                result = (String) (((SealedObject) ois.readObject()).getObject(c));
                System.out.println("Read Object (plain): " + result);
            }
            try (ObjectInputStream ois = new ObjectInputStream(istrx)) {
                result = (String) (((SealedObject) ois.readObject()).getObject(c));
                System.out.println("Read Object (encrypt): " + result);
            }
        } catch (Exception e) {
            System.err.println("Exception in readDb: \n" + e);
        }
    }

    public static void main(String[] args) {
        EncryptedObjectStreamExample eos = new EncryptedObjectStreamExample();
        eos.writeTest();
        eos.readTest();
    }

}

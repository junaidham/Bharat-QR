package qrscan.ng.com.ngqrcode;

/**
 * Created by pramod on 30-10-2017.
 */

import java.nio.charset.StandardCharsets;
import java.security.Key;
//import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;


//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;

public class AES {
    static LoadPropertyFile loadPropertyFile = new LoadPropertyFile();
    private static final String ALGORITHM = loadPropertyFile.getPropertyValue(SystemConstants.Encryption);
    private static final int ITERATIONS = 2;
    private static final byte[] keyValue =
            new byte[] { 'T', 'h', 'i', 's', 'I', 's', 'A', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};


   /* byte[] original = "Base64".getBytes(StandardCharsets.UTF_8);

    // encode
    byte[] toBase64 = Base64.getEncoder().encode(original);

    // decode
    byte[] fromBase64 = Base64.getDecoder().decode(toBase64);
    */

    /**
     * https://www.programmersought.com/article/52305073277/
     * https://stackoverflow.com/questions/7102605/how-to-get-the-jar-file-for-sun-misc-base64encoder-class
     */

    public static String encrypt(String value, String salt) throws Exception {
        // Create key and cipher
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);

        // encrypt the text
        c.init(Cipher.ENCRYPT_MODE, key);
        String valueToEnc = null;
        String eValue = value;
        for (int i = 0; i < ITERATIONS; i++) {
            valueToEnc = salt + eValue;
            byte[] encValue = c.doFinal(valueToEnc.getBytes());

            // old
          //  eValue = new BASE64Encoder().encode(encValue);

            //new
            eValue = Base64.encodeBase64String(encValue);

        }

        return eValue;
    }

    public static String decrypt(String value, String salt) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);

        String dValue = null;
        String valueToDecrypt = value;
        for (int i = 0; i < ITERATIONS; i++) {
            // old
           // byte[] decordedValue = new BASE64Decoder().decodeBuffer(valueToDecrypt);

            //new
            byte[] decordedValue = Base64.decodeBase64(valueToDecrypt);

            byte[] decValue = c.doFinal(decordedValue);
            dValue = new String(decValue).substring(salt.length());
            valueToDecrypt = dValue;
        }
        return dValue;
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGORITHM);
        return key;
    }
}


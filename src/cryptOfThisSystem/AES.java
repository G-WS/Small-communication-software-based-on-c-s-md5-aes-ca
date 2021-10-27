package cryptOfThisSystem;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class AES {


    static final String ALGORITHM = "AES";

    public static SecretKey generateKey() throws NoSuchAlgorithmException { // 生成密钥
        KeyGenerator secretGenerator = KeyGenerator.getInstance(ALGORITHM);
        SecureRandom secureRandom = new SecureRandom();
        secretGenerator.init(secureRandom);
        SecretKey secretKey = secretGenerator.generateKey();
        return secretKey;
    }

    static Charset charset = Charset.forName("UTF-8");

    public static byte[] encrypt(String content, SecretKey secretKey) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException { // 加密
        return aes(content.getBytes(charset), Cipher.ENCRYPT_MODE, secretKey);
    }

    public static String decrypt(byte[] contentArray, SecretKey secretKey) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException { // 解密
        byte[] result = aes(contentArray, Cipher.DECRYPT_MODE, secretKey);
        return new String(result, charset);
    }

    private static byte[] aes(byte[] contentArray, int mode, SecretKey secretKey)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(mode, secretKey);
        byte[] result = cipher.doFinal(contentArray);
        return result;
    }

    public static void main(String[] args) {
        String content = "你好这里是AES";
        SecretKey secretKey;

        try {
            long timeStart = System.currentTimeMillis();
            secretKey = generateKey();
            // System.out.println(secretKey);
            //System.out.println(secretKey);
            MD5 md5 = new MD5();
            content = content + "!!" + md5.start(content);
            byte[] encryptResult = encrypt(content, secretKey);
            long timeEnd = System.currentTimeMillis();
            System.out.println("加密后的结果为：" + new String(encryptResult, charset));

            String decryptResult = decrypt(encryptResult, secretKey);
            int id = decryptResult.indexOf("!!");

            System.out.println(id);
            String message = decryptResult.substring(0, id);
            System.out.println(message);
            String md = decryptResult.substring(id + 2, decryptResult.length());
            if (md5.start(message).substring(0, md.length()).equals(md)) {
                System.out.println("true");
            } else {
                System.out.println("false");
            }
            //System.out.println(md5.start(message)+"\n"+md5.start(content));
            //System.out.println(md);
            System.out.println("解密后的结果为：" + decryptResult);
            System.out.println("加密用时：" + (timeEnd - timeStart));
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
    }
}
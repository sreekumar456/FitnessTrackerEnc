package com.ft.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;
import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.salt.ZeroSaltGenerator;



/**
 * Functions to encrypt and decrypt.
 */
public final class CryptoFunctions {

    /**
     * Private Constructor .
     */
    public CryptoFunctions() {

    }

    /**
     * Increase in data size after each 1 MB encryption . This happens due to
     * padding by jasypt Password Based Encryption .
     */
    public static final int ENCRYPT_PADDED_BYTES = 16;
    /**
     * string object for osName.
     */
    private static String osName = System.getProperty("os.name");
    /**
     * Password.
     */
    
    // private final static String password = "dmp-dev-key";
    private final static String password = "Vdsa<fq09094>!./sf:}{2115855^@%^!asf";
    
    private final static String agentAdmin = "GWagent.admin@covidien.com";
    
    private final static String agentAdminPassword = "6bOZ0qDGtcnLaCbnNtH4P3+B+zUzWo44";
    
    // private final static String h2DBPassword = "dmp-dev-key";
    private final static String h2DBPassword = "Vdsa<fq09094>!./sf:}{2115855^@%^!asf";

    
    /**
     * The system environment variable for agent encryption password.
     */
    
    private static final String COV_DMA_PROD_KEY = "COV_DMA_PROD_KEY";
    /**
     * Mega byte .
     */
    public static final int ENCRYPT_BLOCK_SIZE = 1024 * 1024;
    /**
     * String Encryptor.
     */
    private static StandardPBEStringEncryptor stringEncryptor = null;

    /**
     * StandardPBEStringEncryptor .
     */
    private static StandardPBEStringEncryptor configStringEncryptor = null;

    /**
     * Byte Encryptor.
     */
    private static StandardPBEByteEncryptor byteEncryptor = null;
    /**
     * Reference to a Cipher object.
     */
    private static Cipher cipher = null;

    /**
     * Reference to a SecretKeySpec object.
     */
    private static SecretKeySpec key = null;

    /**
     * Reference to an Initialisation Vector.
     */
    private static byte[] initialisationVector = null;

    /**
     * The Encryption algorithm to be used on the download File.
     */
    
    private final static String downloadFileEncryptAlgo = "AES";

    // /**
    // * The Encryption mode to be used on the download file.
    // */
    // @Obfuscate
    // private final static String downloadFileEncryptMode = "CFB8";
    //
    // /**
    // * The Encryption Padding Scheme to be used on the download file.
    // */
    // @Obfuscate
    // private final static String downloadFileEncryptPadding = "NOPADDING";

    /**
     * The Encoding scheme to be used on the String Iv and Password .
     */
    
    private final static String downloadFileEncryptEncode = "UTF-8";
    /**
     * downloadFileEncryptMode +　downloadFileEncryptMode　+
     * downloadFileEncryptPadding
     */
    
    private final static String downloadFileEncryptTransformation = "AES/CFB8/NOPADDING";

    /**
     * The string Initialisation Vector.
     */
    
    private final static String downloadFileEncryptIv = "123^&6781#@$456%";

    /**
     * Static block to load and read properties.
     */
    static {
        stringEncryptor = new StandardPBEStringEncryptor();
        stringEncryptor.setPassword(password);
        byteEncryptor = new StandardPBEByteEncryptor();
        byteEncryptor.setPassword(password);

  //      String prodKey = System.getenv(COV_DMA_PROD_KEY);
  //      if (prodKey == null || "".equals(prodKey.trim())) {
  //          System.out.println("The system environment variable " + COV_DMA_PROD_KEY + " is not set");
 //       } else {
            configStringEncryptor = new StandardPBEStringEncryptor();
            configStringEncryptor.setSaltGenerator(new ZeroSaltGenerator());
//            configStringEncryptor.setPassword(prodKey);
            configStringEncryptor.setPassword(COV_DMA_PROD_KEY);
            
 //       }

        try {
            initialisationVector = downloadFileEncryptIv.getBytes(downloadFileEncryptEncode);
            cipher = Cipher.getInstance(downloadFileEncryptTransformation);
            key = new SecretKeySpec(DigestUtils.md5Hex(password).getBytes(downloadFileEncryptEncode),
                    downloadFileEncryptAlgo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the osName.
     */
    public static String getOsName() {
        return osName;
    }

    /**
     * Decrypt an encrypted File and return it .
     * 
     * @param encryptedFile
     *        .
     * @param destinationFilePath
     *        .
     * @return decryptedFile .
     * @throws IOException
     *         if occurs.
     */
    public static File decryptFile(final File encryptedFile, final String destinationFilePath)
        throws IOException {
        if (encryptedFile == null || !encryptedFile.isFile()) {
            return null;
        }

        String decryptFilePath = null;
        if (destinationFilePath == null || destinationFilePath.equals(encryptedFile.getAbsolutePath())) {
            decryptFilePath = fileNameAppend(encryptedFile, "Decrypted");
        } else {
            decryptFilePath = destinationFilePath;
        }

        File decryptedFile = new File(decryptFilePath);
        if (!decryptedFile.isFile()) {
        	if( decryptedFile.getParentFile() != null ){
            decryptedFile.getParentFile().mkdirs();
        	}
            if (osName.contains("Windows")) {
                try {
                    Runtime.getRuntime().exec("attrib +H +S " + decryptedFile.getPath());
                } catch (IOException e) {
                    System.out.println("Error in creating Directory" + e.getMessage());
                }
            }
            decryptedFile.createNewFile();
        } else {
            // If decrypted File already exists, return it.
            return decryptedFile;
        }

        FileInputStream inFile = new FileInputStream(encryptedFile);
        FileOutputStream outFile = new FileOutputStream(decryptedFile);
        byte[] tempData = new byte[ENCRYPT_BLOCK_SIZE + ENCRYPT_PADDED_BYTES];
        int bytesRead = 0;
        while ((bytesRead = inFile.read(tempData)) != -1) {
            ByteArrayOutputStream bufOut = new ByteArrayOutputStream();
            bufOut.write(tempData, 0, bytesRead);
            byte[] decryptedOut = decrypt(bufOut.toByteArray());
            outFile.write(decryptedOut);
            outFile.flush();
        }
        inFile.close();
        outFile.close();
        return decryptedFile;
    }

    /**
     * Encrypts a file and returns it .
     * 
     * @param file
     *        .
     * @param destinationFilePath
     *        .
     * @return encrypted File .
     * @throws IOException
     *         if occurs.
     */
    public static File encryptFile(final File file, final String destinationFilePath)
        throws IOException {
        if (file == null || !file.isFile()) {
            return null;
        }

        String encryptFilePath = null;
        if (destinationFilePath == null || destinationFilePath.equals(file.getAbsolutePath())) {
            encryptFilePath = fileNameAppend(file, "Encrypted");
        } else {
            encryptFilePath = destinationFilePath;
        }

        File encryptedFile = new File(encryptFilePath);
        if (!encryptedFile.isFile()) {
        	if( encryptedFile.getParentFile() != null ){
            encryptedFile.getParentFile().mkdirs();
        	}
            encryptedFile.createNewFile();
        } else {
            // If encrypted File already exists, return it.
            return encryptedFile;
        }

        FileInputStream inFile = new FileInputStream(file);
        FileOutputStream outFile = new FileOutputStream(encryptedFile);

        // Hide the directory. Must do it here, and not before otherwise creating the streams above will fail.
        if (osName.contains("Windows")) {
            try {
                Runtime.getRuntime().exec("attrib +H +S " + encryptedFile.getPath());
            } catch (IOException e) {
                System.out.println("Error in creating Directory" + e.getMessage());
            }
        }
        
        // Read infile, encrypt and put encrypted data in outfile
        byte[] tempData = new byte[ENCRYPT_BLOCK_SIZE];
        int bytesRead = 0;
        while ((bytesRead = inFile.read(tempData)) != -1) {
            ByteArrayOutputStream bufOut = new ByteArrayOutputStream();
            bufOut.write(tempData, 0, bytesRead);
            byte[] encryptedOut = encrypt(bufOut.toByteArray());
            outFile.write(encryptedOut);
            outFile.flush();
        }
        inFile.close();
        outFile.close();
        return encryptedFile;
    }

    /**
     * Decrypt the downloaded Bytes .
     * 
     * @param in
     *        .
     * @return byte[] .
     * @throws BadPaddingException .
     * @throws IllegalBlockSizeException .
     * @throws InvalidAlgorithmParameterException .
     * @throws InvalidKeyException .
     */
    public static byte[] decryptDownloadBytes(final byte[] in)
        throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        // Return the input byte array if it is null or empty
        if (in == null || in.length == 0) {
            return in;
        }
        byte[] decryptedBytes = null;
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(initialisationVector));
        decryptedBytes = cipher.doFinal(in);
        return decryptedBytes;
    }

    

    /**
     * Decrypt the downloaded File.
     * 
     * @param encryptedFile
     *        .
     * @param destinationFilePath
     *        .
     * @return File .
     * @throws IOException
     *         if occurs.
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     */
    public static File decryptDownloadFile(final File encryptedFile, final String destinationFilePath)
        throws IOException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException,
        BadPaddingException {
        // logger.debug("destinationFilePath = " + destinationFilePath);
        if (encryptedFile == null || !encryptedFile.isFile()) {
            return null;
        }
        // logger.debug("encryptedFile = " + encryptedFile.getAbsolutePath());
        String decryptFilePath = null;
        if (destinationFilePath == null || destinationFilePath.equals(encryptedFile.getAbsolutePath())) {
            decryptFilePath = fileNameAppend(encryptedFile, "Decrypted");
        } else {
            decryptFilePath = destinationFilePath;
        }
        File decryptedFile = new File(decryptFilePath);
        if (decryptedFile.exists()) {
            return decryptedFile;
        }
        if (!decryptedFile.isFile()) {
        	if( decryptedFile.getParentFile() != null ){
            decryptedFile.getParentFile().mkdirs();
        	}
            if (osName.contains("Windows")) {
                try {
                    Runtime.getRuntime().exec("attrib +H +S " + decryptedFile.getPath());
                } catch (IOException e) {
                    System.out.println("Error in creating Directory" + e.getMessage());
                }
            }
            decryptedFile.createNewFile();
        }
        FileInputStream inFile = new FileInputStream(encryptedFile);
        FileOutputStream outFile = new FileOutputStream(decryptedFile);
        byte[] tempData = new byte[ENCRYPT_BLOCK_SIZE];
        int bytesRead = 0;
        while ((bytesRead = inFile.read(tempData)) != -1) {
            // logger.trace("bytesRead = " + bytesRead);
            ByteArrayOutputStream bufOut = new ByteArrayOutputStream();
            bufOut.write(tempData, 0, bytesRead);
            byte[] decryptedOut = decryptDownloadBytes(bufOut.toByteArray());
            if (decryptedOut == null) {
                System.out.println("Unable to decrypt the file.");
                return null;
            }
            outFile.write(decryptedOut);
            outFile.flush();
        }
        inFile.close();
        outFile.close();
        return decryptedFile;
    }

    /**
     * Encrypt a string and return it .
     * 
     * @param in
     *        .
     * @return String .
     */
    public static String encrypt(final String in) {
        // Return the input string if it is null or empty
        if (in == null || in.isEmpty()) {
            return in;
        }

        return stringEncryptor.encrypt(in);
    }

    /**
     * Decrypt an encrypted string and return it .
     * 
     * @param in
     *        .
     * @return String .
     */
    public static String decrypt(final String in) {
        // Return the input string if it is null or empty
        if (in == null || in.isEmpty()) {
            return in;
        }

        return stringEncryptor.decrypt(in);
    }

    /**
     * Encrypt a byte array and return it .
     * 
     * @param in
     *        .
     * @return byte[] .
     */
    public static byte[] encrypt(final byte[] in) {
        // Return the input byte array if it is null or empty
        if (in == null || in.length == 0) {
            return in;
        }

        return byteEncryptor.encrypt(in);
    }

    /**
     * Decrypt an encrypted byte array and return it .
     * 
     * @param in
     *        .
     * @return byte[] .
     */
    public static byte[] decrypt(final byte[] in) {
        // Return the input byte array if it is null or empty
        if (in == null || in.length == 0) {
            return in;
        }

        return byteEncryptor.decrypt(in);
    }

    public static String getGwagentUser() {
        return agentAdmin;
    }

    public static String getGwagentPass() {
        return agentAdminPassword;
    }
    
    public static String getH2DBPass() {
        return h2DBPassword;
    }

    /**
     * Encrypt a configuration string and return it .
     * 
     * @param in
     *        .
     * @return String .
     */
    public static String encryptConfigString(final String in) {
        // Return the input string if it is null or empty
    	if (in == null || in.isEmpty() || configStringEncryptor == null) {
  //      if (in == null || in.isEmpty() || configStringEncryptor == null) {
            return in;
        }
        return configStringEncryptor.encrypt(in);
    }

    /**
     * Decrypt an encrypted configuration string and return it .
     * 
     * @param in
     *        .
     * @return String .
     */
    public static String decryptConfigString(final String in) {
        // Return the input string if it is null or empty
        if (in == null || in.isEmpty() || configStringEncryptor == null) {
            return in;
        }
        return configStringEncryptor.decrypt(in);
    }
        

    /**
     * Encrypt bytes to test download module .
     * 
     * @param in
     *        .
     * @return byte[] .
     */
    public static byte[] encryptDownloadBytes(final byte[] in) {
        // Return the input byte array if it is null or empty
        if (in == null || in.length == 0) {
            return in;
        }
        byte[] encryptedBytes = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(initialisationVector));
            encryptedBytes = cipher.doFinal(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedBytes;
    }

    /**
     * Encrypt a file for testing the download module .
     * 
     * @param file
     *        .
     * @param destinationFilePath
     *        .
     * @return File .
     * @throws IOException
     *         if occurs.
     */
    public static File encryptDownloadFile(final File file, final String destinationFilePath)
        throws IOException {
        if (file == null || !file.isFile()) {
            return null;
        }
        String encryptFilePath = null;
        if (destinationFilePath == null || destinationFilePath.equals(file.getAbsolutePath())) {
            encryptFilePath = fileNameAppend(file, "Encrypted");
        } else {
            encryptFilePath = destinationFilePath;
        }
        File encryptedFile = new File(encryptFilePath);
        if (!encryptedFile.isFile()) {
            File parentDir = encryptedFile.getParentFile();
            if (parentDir != null) {
                parentDir.mkdirs();
                if (osName.contains("Windows")) {
                    try {
                        Runtime.getRuntime().exec("attrib +H +S " + parentDir.getPath());
                    } catch (IOException e) {
                        System.out.println("Error in creating Directory" + e.getMessage());
                    }
                }
            }
            encryptedFile.createNewFile();
        }
        FileInputStream inFile = new FileInputStream(file);
        FileOutputStream outFile = new FileOutputStream(encryptedFile);
        byte[] tempData = new byte[ENCRYPT_BLOCK_SIZE];
        int bytesRead = 0;
        while ((bytesRead = inFile.read(tempData)) != -1) {
            ByteArrayOutputStream bufOut = new ByteArrayOutputStream();
            bufOut.write(tempData, 0, bytesRead);
            byte[] encryptedOut = encryptDownloadBytes(bufOut.toByteArray());
            outFile.write(encryptedOut);
            outFile.flush();
        }
        inFile.close();
        outFile.close();
        return encryptedFile;
    }

    /**
     * Append a name to the input file name .
     * 
     * @param inFile
     *        .
     * @param appendName
     *        .
     * @return String .
     */
    public static String fileNameAppend(final File inFile, final String appendName) {
        // Only checking for NULL value. Deliberately NOT checking for existence
        // of file.
        if (inFile == null) {
            System.out.println("input is null.");
            return null;
        }
        File destFile = null;
        String randomName = null;
        if (appendName == null) {
            Date date = new Date();
            SimpleDateFormat currentTime = new SimpleDateFormat("-MM-dd-yyyy-h:mm:ss:a", Locale.ENGLISH);
            randomName = currentTime.format(date);
        } else {
            randomName = appendName;
        }
        String parentDir = inFile.getParent();
        String fileName = inFile.getName();
        String[] fileNameParts = fileName.split("\\.", 2);
        String fileNameNoExtension = null;
        String fileExtension = null;
        if (fileNameParts.length == 2) {
            // File name is in proper format
            fileNameNoExtension = fileNameParts[0];
            fileExtension = fileNameParts[1];
        } else {
            // File name is in wrong format.
            destFile = new File(inFile.getAbsolutePath() + randomName);
            return destFile.getAbsolutePath();
        }
        StringBuffer destFilePath = new StringBuffer();
        destFilePath.append(parentDir);
        destFilePath.append(File.separator);
        destFilePath.append(fileNameNoExtension);
        destFilePath.append(randomName);
        destFilePath.append(".");
        destFilePath.append(fileExtension);
        destFile = new File(destFilePath.toString());
        return destFile.getAbsolutePath();
    }
    
    /**
     * get decrypt file stream from given file
     * @param file
     * @return decrypt file stream
     * @throws IOException
     */
    public static CipherInputStream decryptFileStream(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        try {
        cipher.init(Cipher.DECRYPT_MODE, key,
                new IvParameterSpec(initialisationVector));
        } catch (Exception e) {
        	System.out.println("Error when init cipher" + e.getMessage());
        }
        return new CipherInputStream(fis, cipher);
    }
    public static void main(String[] args){
//    	String s1="<?xml version='1.0' encoding='utf-8'?><message schema_version='3644767c-2632-411a-9416-44f8a7dee08e'><request type='createsession' xaction_guid='0f16a851-a043-498d-a857-77ee5c59499c'></request></message>";
//    	String s2= "<?xml version='1.0' encoding='utf-8'?><message schema_version='3644767c-2632-411a-9416-44f8a7dee08e'><request type='opensession' xaction_guid='0f16a851-a043-498d-a857-77ee5c59499c'><params><timestamp type='network'>1441780442</timestamp><sessionID>95f77eb0-0bbd-4f47-9eee-7b41a661b953</sessionID></params></request></message>";
    	String s3="<?xml version='1.0' encoding='utf-8'?><message schema_version='3644767c-2632-411a-9416-44f8a7dee08e' session_guid='95f77eb0-0bbd-4f47-9eee-7b41a661b953'> <request type='postnotification' xaction_guid='e1c38f30-50a0-4ccf-a74a-c7ec11b54544'><params><mailbox>3bd775ad-7250-42ff-adf4-188e0b691bff</mailbox></params><notification oid='95db4769-701b-4aa4-8f6f-f47e124459bf'><header notification_type='event' notification_action='create'><pertinent_type>CEFC1E07-CFF6-4F27-AB05-4577A33A1BA8/8E74DB99-F0A3-4DE4-AEED-F17AFB6896FC</pertinent_type><pertinent_identifier>980abc1234567</pertinent_identifier></header><body><logs><log name='EventLog'><meta http-equiv=\"transfer-encoding\" content=\"gzip\"/><uri_inc> C:/Logs/980abc1234567__EventLog_03Jun2015_12h_12m_34s_201ms-06-10-2015.dat.gz</uri_inc></log></logs></body></notification></request></message>";    	
//    	String s4="<?xml version='1.0' encoding='utf-8'?><message schema_version='3644767c-2632-411a-9416-44f8a7dee08e' session_guid='95f77eb0-0bbd-4f47-9eee-7b41a661b953'> <request type='postnotification' xaction_guid='e1c38f30-50a0-4ccf-a74a-c7ec11b54544'><params><mailbox>3bd775ad-7250-42ff-adf4-188e0b691bff</mailbox></params><notification oid='95db4769-701b-4aa4-8f6f-f47e124459bf'><header notification_type='event' notification_action='create'><pertinent_type>CEFC1E07-CFF6-4F27-AB05-4577A33A1BA8/2F62D564-A162-440A-A5F6-ED16E7E632D5</pertinent_type><pertinent_identifier>980abc1234567</pertinent_identifier></header><body><logs><log name='EventLog'><meta http-equiv=\"transfer-encoding\" content=\"gzip\"/><uri_inc> C:/Logs/980abc1234567__EventLog_03Jun2015_12h_12m_34s_201ms-06-10-2015.dat.gz</uri_inc></log></logs></body></notification></request></message>";
    	System.out.println(""+encrypt(s3));
    }
    
   /* public static void main(String[] args){
    	String s1="mY7+NGPEcJLFi1URvXwlewMxPq0z8922MAiRlJgChm9E2DG8mDvVRAt0ntjr9cT9S9KGg+mBDV8cvLAyFUdxxfQyO/OTxPOsuO46w8xktRh1B6JXWALdlLoJUObh4x9IDuJik5Nclu76To8btSVctTn9gYbxYV9atJmfjuclL+qZsxoGK1nHt9AOd202TajcbaaUjCmngv+gGJXkZ/DFYJYGt2Oj31bkDabzWtIQff9rwWxp7t2zghh9jjQewpPgsuzET50ObngLamDvCC/2vzcWwEt1sUTY7e+DzpVtWAghd719E2L63/+jUYsdMcUH3IkW/jIDgE8vkBjn5AZu94vsPNHDzrIX0s40oUI3TSphnunicrz98c2zPhAKi5+UxqwDFctQ1TLcrDz+r5hqcDvTI0aH7xA4xxaOs1HtmDYsHjzhqTDOwlDoIAwEyXtQom4QncXMSsCJHuJJViqfWUcsfnbG9rb/0hth7yPTqnt1a+P/HRax/w3557Co2mZD7FW90+LfMblmYenLXdsHwIhwZYXEbn4uIihfH7IUeLq5hPzKGy50hn32slq8/HkAgKZDf4sBGQ6k741tnGw9PE909eBbGqDoeiipombROBhXKTa0eUKaYjDVzGrK/82P3H0MzNGHccjyIVe96ftJhSnzKaI7jAjBM18N6ZiN8/Q=";
      	//String s4="rqq+tsZY7/o37sfjDcHLg6d8/BDH9iMLMf77FgrmFRPylz2A51jdANKhsBAQsYOuepK1bjHqHkUihQ8D8sxhjCWI1hw4UoB7q0lU0D79r6iQ1TN0xX6SD/YytZP8FROu/czVK1umChnYy+OqZ4cKMDbVXaJ3V3omzB2KUGcCQ+PN/bgoJ8UQAtN0oEVTTz9zVVC/NQmCyq9hCgVYbS2bcioLmfgkUfPn5KqTZl6kkUpyPEtHT12kehkn385Rmbf7iBkNDIFKXBOYtoUqragY1cX6QFQclxCGBoXwK6xEvDsEOu+GO5kFbPpTFGqAbDUK3li7ZkpquY0USX8A4YnXrxuvs5lXuSIP7Z0TlSxu679UJhceSAxs6ky77st6b4vHWmM9t5mztr4lRcz8aSLhn4eeemnnJn+35RxknAlil7mXxqJ+fwU3Oztbz9O8kiGg";
    	//String s5="h1/Q1iQ1LerZIJ1RPZUnazZyLKU6V/nKqP/Bqt7FoLKFeofjybIAFGgUnRy7zoq8RctJp2B1qRmn2mSV4u6XsFS4Ez4fZQ7lZcVoNMUnTeQoa91Wkihsx/jNl6bGn7bWMIPthjsaPXNq2SwUfDcMWObM76iX7uHavVislg1AhNqHabpjUJUMVdhJcY5JbWjq1Gme0w1z3puobrc6y2lsoGFwdR+KJ37KYYqzCwlmFAxSuyjuD/gODz56XT2UVKXlH0isdQjNcbuIKCLRpB4hTFNjmfgoPj+zfa8kUSfbj/mqrkJRo92MPgIBTC44+puViXi9eYqemZ3HxylFcCaZfbqcc6ksDehF8o40jcLiqHV4H6ArpR0tn8Mk+hjZCXkDxxMDoIFmeg1Xi26ZDNyie6RajXMGPoF1ttHOI2DauAYBguUeZtNnw4koYRflquEU2jw1tWq5NorFyD8wZYCGMHhlhHOHqFrNiMgzy+gvdWQMNYN975RV/187e6lbL24TyV14Fd+oGjoc9zTJYaTYuyk6WG0EmWCuVFUw+Bj5hMZvgdOnjODm02yLoNhYFNGkn4cmiLnMnOsm4xB088ZEufhw7DVb6JYKN9TvdegtlMjElatJIcvMBLFI5O3LoUoJJGBuCB6r2psj//skRUcmE5XgUES9ObBQNGWqHj8wdoN5z6K4al7/bRImgRSGP5WEwlZOFBRtIKQGcfGRloLHu3csggBe+gDKYfai3vYL9p24dbwC9exl5PsdOYNpglB/ra62EcbG0pc7d8EQgxYoJg5AqachGJoT6e4ydad0ZwPTi25YNqeVNJTPMlYuvPZyNmPWo04zH2Wd1InYpwXt/YS2Kf+YFMT4CMxAbHqptQ5Yrkj1LGtzr1rWREi4SRKk8DzKgsEMwGYW32mv+B9mlgxoCJlag+FrhVTONslpHLBVnVKPtBb7WGsiTmStHWnzhZ/GbgxFB1k5FCqhEWuJVMiulmyW5STRhYdlo9o9xWgKfcn2FWZOD7OjzVgbUVuz7dXukX6bm2CcoTHGcilWcQr1x5s/hYiL0R9nXN2K37rS/JofmkYrUabFgd5Nr+TQi2nsNhFtR8lj4iyXvJzHnjQofeTSQCW93mmbY9ndtggvHZgUJCo/dVjuMmYCv71ROp1fK46o5CzKndp88u910NqDfxtpgcJwJr9N+cmvHJ9xFeGPKKUkjp41tnARAUg5khNz614BWeKgszwJ/Ib8kC2ifjf++PqF2+038UET1szl/7ch+uRNqL4dt+aM0f5P9WS29sXgl6NOlx3lcbUfMOGKA28bTmUESc6JdoL17tCxWsdvsm6n4Z0RrgiylV/ciYEZwciqYN7htqF+SDNm6Y7pNE1LJuZ9zXAAn94H3i3CKo1PuoQIKL2x7FDUxnsD";
//    	String vi="Hv2aOKyrCdQVq56+HAmZn5jTbkQq/XRodXGdNj9kh3sSO8XRg6qVsoQoXI/4JaXkGXCl1unwf83gx9pngfdXqfHuowXbhEymMqXkQVRem6+LwAod52efTD4TVbTbggSA8XSIRgp75OWoQL3bRcIeh4fOSxQ41RLh81dJM7BzB0mOVslzHej6stQhbfz2U8NsaFgzJwvxpWXHe0zRlo30mLQ50rUknHM4au2XSvQq0QXaygGxZCgpYUcy9mWJcU+Z8hWdo5vL+7cOSDU1HfA1cx5TPH5569mtbYdM7RyzTfnb7Q6yXfNXwPdPODuKelrNLyKy71Ca1fbl3uTa/tVfAn0IeILa/Sa9XAXehmAr+EFjN03U0yA4SxdkMaEzc+mG81jD+y4xih8eBvrjHkTXDE7x3X1AvvF2JPi3FqJIaVa5ba0UhcnqfBw6L05aOhkIu4vR/fzy442Wzt/QQ9Yrh4esTBouC4AWDruRVtbMT/0R1PqGle9q88bIYmeCs8Ew83kpbBcS/yaeC9mgqIH27OsiqNed6fUI9bhEV/TliJYLciM1a9Q4DfPtGTS6oEhCxFrv2bKUjhk1Tgm+WN/S5PXT8Kaw561qJ3LNIWlQN5bo2rbi+ZtZGA==";
    	System.out.println(""+decrypt(s1));
   } */
    
}

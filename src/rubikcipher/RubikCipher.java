/*
 * This code is implementation of our paper's at : 
 */

package rubikcipher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Khoirunnisa Afifah (k.afis3@rocketmail.com)
 *         Yollanda Sekarrini 
 */
public class RubikCipher {
    private String inputBlock; //input teks : 12 char
    private String inputBinary; //input teks in BinaryString
    private String extKey;  //key from user
    private int numIteration = 12; //length of extKey
    private String outBlock; //output teks
    private String outBinary; //output teks in BinaryString
    
    private String[] L; //left part String in fiestel
    private String[] R; //right part String in fiestel
    private String[] key; //internal key

    public Rubik rubik = new Rubik();
    
    /**
     * MAIN
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        RubikCipher rc = new RubikCipher();
        String input = "Tes1234";
//        String input = "Haloapakabar";
        String key = "haloapakabar";
//        System.out.println(input.length());
//        rc.prepareKey(key);
        
        String ct = rc.EcbEncrypt(input, key);
        System.out.println();
        //decrypt
        
        String pt = rc.EcbDecrypt(ct, key);
        
//        rc.setValidInput(input);
//        rc.prepareKey1(key);
//        rc.doFeistel();
//        System.out.println(rc.getOutBinary() + " " + rc.getOutBlock());
//        rc.setValidInput(rc.getOutBlock());
//        rc.reverseFeistel();
//        System.out.println(rc.getOutBinary() + " " + rc.getOutBlock());
    }
    
    /**
     * ECB mode encryption
     */
    public String EcbEncrypt(String plaintext, String key) {
        int numDo;
        String inputB, outB, result="";
        
        if (plaintext.length() % 12 == 0)
            numDo = plaintext.length()/12;
        else 
            numDo = (plaintext.length()/12) + 1;
        int idx = 0;
        prepareKey(key);
            
        
        for (int i=0; i<numDo; i++) {
            if (i != numDo-1)
                inputB = plaintext.substring(idx, idx+12);
            else 
                inputB = plaintext.substring(idx);
            setValidInput(inputB);
            doFeistel();
            outB = outBlock;
            result+=outB;
            idx +=12;
        }
        
//        System.out.println("ciphertext " + result);
        return result;
    }
    
    /**
     * ECB mode decryption
     */
    public String EcbDecrypt(String ciphertext, String key){
        int numDo, idx = 0;
        String result ="", outB, inputB;
        if (ciphertext.length() % 12 == 0)
            numDo = ciphertext.length()/12;
        else 
            numDo = (ciphertext.length()/12) + 1;
        prepareKey(key);
        
        for (int i=0; i<numDo; i++) {
            if (i != numDo-1)
                inputB = ciphertext.substring(idx, idx+12);
            else 
                inputB = ciphertext.substring(idx);
            setValidInput(inputB);
            reverseFeistel();
            outB = outBlock;
                result+=outB;
            idx +=12;
        }
        
//        System.out.println("plaintext : " + result);
        return result;
    }
    
    /**
     * CBC mode encryption
     */
    public String CbcEncrypt(String plaintext, String key) {
        int numDo;
        String inputB, outB, result="";
        String outBefore=""; 
        
        if (plaintext.length() % 12 == 0)
            numDo = plaintext.length()/12;
        else 
            numDo = (plaintext.length()/12) + 1;
        int idx = 0;
        prepareKey(key);
            
        
        for (int i=0; i<numDo; i++) {
            if (i != numDo-1)
                inputB = plaintext.substring(idx, idx+12);
            else 
                inputB = plaintext.substring(idx);
            if (i!=0)
                inputB = XOR(inputB,outBefore);
            setValidInput(inputB);
            doFeistel();
            outB = outBlock;
            outBefore = outBinary;
            result+=outB;
            idx +=12;
        }
        
        System.out.println("ciphertext " + result);
        return result;
    }
    
    /**
     * CBC mode decryption
     * BELUM BENER : Syaratnya text yg di dekript panjangnya harus kelipatan 12
     */
    public String CbcDecrypt(String ciphertext, String key){
        int numDo, idx = ciphertext.length();
        String result ="", outB, inputB, outBin;
        
        if (ciphertext.length() % 12 == 0)
            numDo = ciphertext.length()/12;
        else 
            numDo = (ciphertext.length()/12) + 1;
        prepareKey(key);
        
        for (int i=numDo-1; i>=0; i--) {
            if (i != numDo-1)
                inputB = ciphertext.substring(idx-12);
            else 
                inputB = ciphertext.substring(idx-12, idx);
            setValidInput(inputB);
            reverseFeistel();
            outBin = XOR(outBinary, ciphertext.substring(idx-24, idx-12));
            outB = fromBinary(outBin);
            result+=outB;
            idx -=12;
        }
        
        System.out.println("plaintext : " + result);
        return result;
    }
    
    /** 
     * Set InputBlock
     * @return 
     * If size of input > 12 character return -1 (error)
     * If size of input = 12 InpurBlock set as input, return 1
     * If size of input < 12 append 0 until length equals 12, return 1
     */
    public int setValidInput(String input) {
        int retVal=-1;
        if (input.length() == 12) {
            inputBlock = input;
            inputBinary = toBinary(inputBlock);
            retVal = 1;
        }
        else if (input.length()>12) {
            retVal = -1;
        }
        else if (input.length()<12) {
            String zero="";
            for (int i=0; i<12-input.length();i++) {
                zero += "0";
            }
            inputBlock = input+zero;
            inputBinary = toBinary(inputBlock);
            retVal = 1;
        }
        return retVal;
    }
    
    /**
     * eKey/2 XOR eKey/2 = key1 + key2
     * key 3 = NOT(key1)
     * key 4 = NOT(key2)
     * key internal = key1 + key3 + key2 + key4
     * @return 1 if success, -1 if failed
     */
    public int prepareKey1(String bKey) {
        int retVal=-1;
        String tkey, left, right, xorKey, key1, key2, key3, key4;
        String eKey = toBinary(bKey);
        if (eKey.length() == 96) {
            key = new String[numIteration];
            left = eKey.substring(0, 48);
            right = eKey.substring(48,96);
            System.out.println(fromBinary(left));
            for (int i = 0; i < numIteration; i++) {
                xorKey = XOR(left, right);
                key1 = xorKey.substring(0,24);
                key2 = xorKey.substring(24,48);
                key3 = NOT(key1);
                key4 = NOT(key2);
                
                tkey = key1+key3+key2+key4;
                
                left = tkey.substring(0,48);
                right = tkey.substring(48,96);
                
                key[i] = tkey;
            }
            
            
            retVal = 1;
        }
        return retVal;
    }
    
    /**
     * Set numIteration, internal key, and external key
     * length of key must > 1
     * @return 1 if success, -1 if failed
     */
    public int prepareKey(String eKey) {
        int retVal=-1;
        if (eKey.length() > 1) {
            key = new String[numIteration];
            String temp = eKey;
            String res;
            for (int j=0; j<numIteration;j++){
                res = "";
                for (int i=0; i<temp.length();i++) {
                    res += (char) ((temp.charAt(i) + (int) temp.charAt(0)- (int)'a') % 255 + (int)'a');
                }
                key[j] = res;
                temp = key[j];
                //System.out.println(key[j]);
            }
         
            retVal = 1;
        }
        return retVal;
    }
    
    /**
     * should be called after setValidInput and prepareKey
     * processed in String of bit
     */
    public void doFeistel(){
        String rubikResult;
        L = new String[numIteration+1];
        R = new String[numIteration+1];
        
        L[0] = inputBinary.substring(0, 48);
        R[0] = inputBinary.substring(48, 96);
        
        for (int i=1; i<numIteration+1; i++) {
            L[i] = R[i-1];
            //DO RUBIK PERMUTATION
            rubik.putAllBit(R[i-1]);
            rubik.doRotation(key[i-1]);
            rubikResult = rubik.readAllBit();
            R[i] = XOR(L[i-1], rubikResult);
           // System.out.println(i + " " + L[i] + " " + R[i]);
        }
        
        outBinary = L[numIteration] + R[numIteration];
        outBlock = fromBinary(outBinary);
    }
    
    /**
     * used for decrypt the message
     * perform Feistel from down to top
     * using reverseRotation of Rubik
     */
    public void reverseFeistel(){
        String rubikResult;
        L = new String[numIteration+1];
        R = new String[numIteration+1];
        
        L[numIteration] = inputBinary.substring(0, 48);
        R[numIteration] = inputBinary.substring(48, 96);
        
        for (int i=numIteration; i>0; i--) {
            R[i-1] = L[i];
            //DO RUBIK PERMUTATION
            rubik.putAllBit(L[i]);
            rubik.doRotation(key[i-1]);
            rubikResult = rubik.readAllBit();
            L[i-1] = XOR(R[i], rubikResult);
//            System.out.println(i + " " + L[i] + " " + R[i]);
        }
        
        outBinary = L[0] + R[0];
        outBlock = fromBinary(outBinary);
    }
    
    /**
     * Convert text to binaryString
     * Each character parsed into 8 binary char
     */
    public String toBinary(String teks) {
        String binary="";
        String perChar;
        for (int i=0; i<teks.length(); i++){
            perChar = Integer.toBinaryString(teks.charAt(i));
            if (perChar.length()<8) {
                String zero="";
                for (int j=0; j<8-perChar.length();j++) {
                    zero += "0";
                }
                perChar = zero+perChar;
                //System.out.println(perChar);
            }
            binary += perChar;
        }
        return binary;
    }
    
    /**
     * Convert binary to normal string
     */
    public String fromBinary(String binary){
        String retval ="";
        int ascii;
        String bitProcess;
        while(binary.length() % 8 != 0){
            binary = '0' + binary;
        }
        while(binary.length()>0){
            bitProcess = binary.substring(0,8);
            ascii = Integer.parseInt(bitProcess,2);
            retval += (char)ascii;
            binary = binary.substring(8);
        }
        return retval;
    }
    
    /**
     * XOR operation on Binary String
     * @param left and right are binary string with same size
     */
    public String XOR(String left, String right){
        String retVal="";
        int ch;
        
        if (left.length()==right.length()) {
            for (int i = 0; i<left.length(); i++) {
                if (left.charAt(i) == right.charAt(i)) {
                    ch = 0;
                }
                else {
                    ch = 1;
                }
                retVal += ch;
            }
        }
        
        return retVal;
    }
    
    /**
     * NOT operation on Binary String
     */
    public String NOT(String left){
        String retVal="";
        int ch;
        
        for (int i = 0; i<left.length(); i++) {
            if (left.charAt(i) == '0') {
                ch = 1;
            }
            else {
                ch = 0;
            }
            retVal += ch;
        }
        
        return retVal;
    }
    
    /**
     * Read file text
     * @return String from text
     */
    public String readFile(String path){
        String msg = "";
        try {
            Path p = Paths.get(path);
            byte[] hiddenText = Files.readAllBytes(p);
            String value = new String(hiddenText, "UTF-8");
            msg = value;
        } catch (IOException ex) {
            Logger.getLogger(RubikCipher.class.getName()).log(Level.SEVERE, null, ex);
        }
        return msg;
    }
    
    
    /// GETTER AND SETTER
    
    /**
     * @return the inputBlock
     */
    public String getInputBlock() {
        return inputBlock;
    }

    /**
     * @return the extKey
     */
    public String getExtKey() {
        return extKey;
    }

    /**
     * @param extKey the extKey to set
     */
    public void setExtKey(String extKey) {
        this.extKey = extKey;
    }

    /**
     * @return the numIteration
     */
    public int getNumIteration() {
        return numIteration;
    }

    /**
     * @return the outBlock
     */
    public String getOutBlock() {
        return outBlock;
    }

    /**
     * @return the inputBinary
     */
    public String getInputBinary() {
        return inputBinary;
    }

    /**
     * @return the outBinary
     */
    public String getOutBinary() {
        return outBinary;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ecdsa;

import java.math.BigInteger;
import javafx.util.Pair;

/**
 *
 * @author edmundophie
 */
public class Main {
    public static void main(String[] args) {
        // Hash stub
        String hash = "86f7e437faa5a7fce15d1ddcb9eaeaea377667b8";
        BigInteger hashVal = new BigInteger(hash, 16);
        
        // Generate private & public key
        ECElGamal el = new ECElGamal();
        BigInteger privateKey = el.generatePrivateKey();
        BigPoint publicKey = el.generatePublicKey(privateKey);
        
        // Generate siganture
        Pair<BigInteger, BigInteger> signature = el.generateSignature(hashVal);
        System.out.println("DS: (" + signature.getKey().toString(16) + ", " + signature.getValue().toString(16) +  ")");
        
        // Verify signature
        BigInteger r = signature.getKey();
        BigInteger s = signature.getValue();
        if(el.verifySignature(r, s, hashVal, publicKey))
            System.out.println("Signature match!");
        else
            System.out.println("Signature doesn't match!");
    }
    
}

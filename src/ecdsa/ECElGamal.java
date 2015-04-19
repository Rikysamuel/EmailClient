/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ecdsa;

import java.math.BigInteger;
import java.util.Random;
import javafx.util.Pair;

/**
 *
 * @author calvin-pc
 */
public class ECElGamal {
   private EllipticCurvePF ellipticCurve;
   private BigPoint G; //Basis point
   private BigInteger Q; //prime order of basis point
   private BigPoint[] encodeTable;
   private BigInteger privateKey;
   private BigPoint publicKey;
   
   public ECElGamal () {
       String A = "340E7BE2A280EB74E2BE61BADA745D97E8F7C300";
       String B = "1E589A8595423412134FAA2DBDEC95C8D8675E58";
       String P = "E95E4A5F737059DC60DFC7AD95B3D8139515620F";
       String X = "BED5AF16EA3F6A4F62938C4631EB5AF7BDBCDBC3";
       String Y = "1667CB477A1A8EC338F94741669C976316DA6321";
       String primeOrder = "E95E4A5F737059DC60DF5991D45029409E60FC09";
       encodeTable = new BigPoint[256];
       generateEncodeTable();
       
       //Generate ellipticCurve 
       ellipticCurve = new EllipticCurvePF(new BigInteger(A,16), new BigInteger(B,16), new BigInteger(P,16));
       
       //Generate G & QTODO    
       G = new BigPoint(new BigInteger(X,16), new BigInteger(Y,16));
       Q = new BigInteger(primeOrder,16);
   }
   
    public Pair<BigPoint,BigPoint> cipherPoint(BigPoint plainPoint, BigPoint publicKey) {
        // generate K [1,q-1] , exclusive
        Random r = new Random();
        // [0,q-4]
        BigInteger K = new BigInteger(Q.bitLength(), r).mod(Q.subtract(BigInteger.valueOf(4)));
        // [2,q-2]
        K = K.add(BigInteger.valueOf(2));
        
        //return <K.B, Plain + K.PublickKey>
        BigPoint first = getEllipticCurve().scalarMulti(G, K);
        BigPoint second = getEllipticCurve().pointAddition(plainPoint, getEllipticCurve().scalarMulti(publicKey, K));
        Pair<BigPoint,BigPoint> chiper = new Pair<BigPoint,BigPoint>(first, second);
        // TO TESTq
        return chiper;
    }
    
    public BigPoint decipherPoint (Pair <BigPoint,BigPoint> chiper, BigInteger privateKey) {
        BigPoint temp = getEllipticCurve().scalarMulti(chiper.getKey(),privateKey);
        return getEllipticCurve().pointSubtract(chiper.getValue(), temp);
    }
    
    public BigPoint generatePublicKey (BigInteger privateKey) {
        publicKey =  getEllipticCurve().scalarMulti(G, privateKey);
        return publicKey;
    }
    
    public BigInteger generatePrivateKey () {
        // generate K [1,q-1] , exclusive
        Random r = new Random();
        // [0,q-4]
        BigInteger K = new BigInteger(Q.bitLength(), r).mod(Q.subtract(BigInteger.valueOf(4)));
        // [2,q-2]
        K = K.add(BigInteger.valueOf(2));
        privateKey = K;
        return privateKey;
    }
    
    public BigInteger getPrivateKey() {
        return privateKey;
    }
    
    public BigPoint getPublicKey() {
        return publicKey;
    }
    
    public void setPrivateKey(BigInteger pK) {
        privateKey = pK;
        generatePublicKey(privateKey);
    }
    /**
     * @return the ellipticCurve
     */
    public EllipticCurvePF getEllipticCurve() {
        return ellipticCurve;
    }
    
    private void generateEncodeTable() {
        for(int ascii=0;ascii<256;++ascii) {
            int y = ascii/16;
            int x = ascii%16;
            encodeTable[ascii] = new BigPoint(BigInteger.valueOf(x), BigInteger.valueOf(y));
        }
    }
    
    public BigPoint getEncodeValue(char C) {
        return encodeTable[(int)C];
    }
    
    public char decodePoint(BigPoint P) {
        return (char)(16*P.y.intValue() + P.x.intValue());
    }
    
    public Pair<BigInteger, BigInteger> generateSignature(BigInteger hash) {
       BigInteger r;
       BigInteger s;
        do{
        // generate K [1,q-1] , exclusive
        Random rand = new Random();
        // [0,q-4]
        BigInteger K = new BigInteger(Q.bitLength(), rand).mod(Q.subtract(BigInteger.valueOf(4)));
        // [2,q-2]
        K = K.add(BigInteger.valueOf(2));
        
        BigPoint x1y1 = getEllipticCurve().scalarMulti(G, K);
        r = x1y1.x.mod(Q);
        s = K.modInverse(Q).multiply(hash.add(privateKey.multiply(r))).mod(Q);
        } while(r.equals(BigInteger.ZERO) || s.equals(BigInteger.ZERO));
        
        Pair<BigInteger, BigInteger> ds = new Pair<BigInteger, BigInteger>(r, s);
        return ds;
    }
    
    public boolean verifySignature(BigInteger r, BigInteger s, BigInteger e, BigPoint publicKey) {
        BigInteger w = s.modInverse(Q);
        BigInteger u1 = e.multiply(w).mod(Q);
        BigInteger u2 = r.multiply(w).mod(Q);
        BigPoint point1 = getEllipticCurve().scalarMulti(G, u1);
        BigPoint point2 = getEllipticCurve().scalarMulti(publicKey, u2);
        BigPoint x1y1 = getEllipticCurve().pointAddition(point1, point2);
        BigInteger rmodQ = r.mod(Q);
        BigInteger x1 = x1y1.x;
        
        return x1.equals(rmodQ);
    }
}

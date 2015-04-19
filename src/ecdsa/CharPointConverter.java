/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ecdsa;

import java.math.BigInteger;

/**
 *
 * @author calvin-pc
 */
public class CharPointConverter {
    private EllipticCurvePF ellipticCurve;
    private BigPoint pointCode [] = new BigPoint[256];
    private int baseParameter = 20;
    
    /**
     * the elliptic curve domain parameter P (prime)
     * must be congruent to 3 if moduli by 4
     * @param ec Elliptic curve over prime field
     *
     */
    public CharPointConverter(EllipticCurvePF ec) {
        assert ec.getPrime().mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3));
        ellipticCurve = ec;
        boolean finished = false;
        
        while (!finished) {
            finished = true;
            for (int i = 0 ; i < 256 && finished; i++) {
                pointCode[i] = searchPointFromChar((char)i);
                if (pointCode[i] == null) {
                    finished = false;
                    baseParameter = baseParameter + 1;
                }
            }
        }
    }
    
    /**
     * encode c to point in elliptic curve
     * @param c
     * @return 
     */
    public BigPoint encode (char c) {
        return pointCode[(int)c];
    }
    
    /**
     * decode point into char c
     * @param point
     * @return 
     */
    public char decode (BigPoint point) {
        //c = (point.x - 1) / k
        return (char)point.x.subtract(BigInteger.ONE).divide(BigInteger.valueOf(baseParameter)).intValue();
    }
    /**
     * Search the corresponding point over elliptic curve 
     * using koblitz method, return null if there is no point
     * that corresponding
     * @param c
     * @return 
     */
    private BigPoint searchPointFromChar (char c) {
        BigInteger x = BigInteger.valueOf(((int)c) * baseParameter + 1);
        //exponent in euler criterion, which value is (p-1)/2
        BigInteger exponent = getEllipticCurve().getPrime().subtract(BigInteger.ONE).divide(BigInteger.valueOf(2));
        
        //try from x = mk+1 until x = mk + k - 1
        for (int i = 1; i < baseParameter - 1; i++,x=x.add(BigInteger.ONE)) {
            // a = x*x*x + A*x + B, where A and B is from elliptic curve
            BigInteger a = x.pow(3).add(x.multiply(getEllipticCurve().getA())).add(getEllipticCurve().getB()).mod(ellipticCurve.getPrime());
            
            // now determine if y exist where y*y = a mod p, where p is from elliptic curve
            // using euler criterion which says that an integer a relatively prime to p 
            // is a quadratic residue (mod p) if and only if a^((p-1)/2) ≡ 1 (mod p).
            // which means if a^((p-1)/2) ≡ 1 (mod p) then y exist
            boolean isExist = a.modPow(exponent, getEllipticCurve().getPrime()).compareTo(BigInteger.ONE) == 0;
            if (isExist) {
                //the solution (y) is a^((p+1)/4) because p = 3 mod 4 
                BigInteger y = a.modPow(getEllipticCurve().getPrime().add(BigInteger.ONE).divide(BigInteger.valueOf(4)), getEllipticCurve().getPrime());
                assert y.modPow(BigInteger.valueOf(2), ellipticCurve.getPrime()).equals(a);
                return new BigPoint (x,y);
            }
        }
        //no solution exist
        return null;
    }

    /**
     * @return the ellipticCurve
     */
    public EllipticCurvePF getEllipticCurve() {
        return ellipticCurve;
    }
}

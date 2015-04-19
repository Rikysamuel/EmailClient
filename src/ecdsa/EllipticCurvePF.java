/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ecdsa;

import java.awt.Point;
import java.math.BigInteger;

/**
 * This class is to represent the Elliptic Curve over Prime Field
 * @author calvin-pc
 */
public class EllipticCurvePF {
    private BigInteger prime;
    private BigInteger A;
    private BigInteger B;
    
    /**
     * 
     * @param p the prime in mod p
     */
    public EllipticCurvePF(BigInteger a, BigInteger b ,BigInteger p) {
        prime = p;
        A =a;
        B = b;
    }
    
    /**
     * return the addition of point p1 and p2 over prime field,
     * however, Xp1 must not same as Xp2, use scalarMulti
     * if you want to get p1 + p1 (2p)
     * @param p1 a point in PrimeField
     * @param p2 a point in PrimeField
     * @return p1 + p2
     */
    public BigPoint pointAddition (BigPoint p1, BigPoint p2) {
        BigInteger xp = p1.x;
        BigInteger yp = p1.y;
        BigInteger xq = p2.x;
        BigInteger yq = p2.y;
        
        assert xp.subtract(xq).compareTo(BigInteger.ZERO) != 0;
        
        //Lambda = (Yp - Yq) / (Xp - Xq) mod p
        BigInteger lambda = yp.subtract(yq).multiply(xp.subtract(xq).modInverse(getPrime())).mod(getPrime());
        
        //Xr = Lambda * Lambda - Xp - Xq mod p
        BigInteger xr = lambda.multiply(lambda).subtract(xp).subtract(xq).mod(getPrime());
        
        //Yr = Lambda * (Xp - Xr) - Yp mod p
        BigInteger yr = lambda.multiply(xp.subtract(xr)).subtract(yp).mod(getPrime());
        
        BigPoint ret = new BigPoint();
        ret.x = xr;
        ret.y = yr;
        return ret;
    }
    
    /**
     * return p1 - p2 over prime field,
     * p1 must not the same as p2, because
     * p - p is O , a point in infinity,
     * which is out of the prime field
     * @param p1 a point in prime field
     * @param p2 a point in prime field
     * @return p1 - p2
     */
    public BigPoint pointSubtract (BigPoint p1, BigPoint p2) {
        BigPoint minusP2 = new BigPoint (p2.x, p2.y.negate());
        return pointAddition(p1, minusP2);
    }
    
    /**
     * return scalar multiplication of point
     * by using double and add algorithm,
     * k must be greater than or same as 2
     * @return k.p
     */
    public BigPoint scalarMulti (BigPoint p, BigInteger k) {
        if (k.compareTo(BigInteger.valueOf(2)) == 0) {
            BigInteger xp = p.x;
            BigInteger yp = p.y;
            
            //Lambda = (3 (Xp * Xp) + a)/ (2 * Yp) mod p
            BigInteger lambda = BigInteger.valueOf(3).multiply(xp.pow(2)).add(A);
            lambda = lambda.multiply(yp.multiply(BigInteger.valueOf(2)).modInverse(prime)).mod(prime);
            
            //Xr = Lambda * Lambda - 2 * Xp mod p
            BigInteger xr = lambda.pow(2).subtract(xp.multiply(BigInteger.valueOf(2))).mod(prime);
            
            //Yr = Lambda * (Xp - Xr) - Yp mod p
            BigInteger yr = lambda.multiply(xp.subtract(xr)).subtract(yp).mod(prime);
            
            return new BigPoint(xr,yr);
        }
        else if (k.mod(BigInteger.valueOf(2)).compareTo(BigInteger.ONE) == 0) {
            // k is odd
            // k.p = (k-1).p + p
            BigPoint temp = scalarMulti(p, k.subtract(BigInteger.ONE));
            return pointAddition(temp, p);
        }
        else {
            //k is even
            // k.p = 2.((k/2).p)
            BigPoint temp = scalarMulti(p, k.divide(BigInteger.valueOf(2)));
            return scalarMulti(temp, BigInteger.valueOf(2));
        }
    }

    /**
     * @return the prime
     */
    public BigInteger getPrime() {
        return prime;
    }

    /**
     * @return the A
     */
    public BigInteger getA() {
        return A;
    }

    /**
     * @return the B
     */
    public BigInteger getB() {
        return B;
    }
}
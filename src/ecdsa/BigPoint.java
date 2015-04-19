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
public class BigPoint {
    public BigInteger x;
    public BigInteger y;
    
    public BigPoint(){}
    public BigPoint(BigInteger X, BigInteger Y) {x=X;y=Y;}
}

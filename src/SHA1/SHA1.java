/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sha.pkg1;

/**
 *
 * @author asus
 */
public class SHA1 {
    // constant value of K
    final public int K1 = 0x5A827999;
    final public int K2 = 0x6ED9EBA1;
    final public int K3 = 0x8F1BBCDC;
    final public int K4 = 0xCA62C1D6;
    
    public byte[] messagePadding(byte[] message) {
        int messageLength = message.length;
        int leftLength = messageLength % 64;
        int paddingLength;
        if(64 - leftLength >= 9) {
            paddingLength = 64 - leftLength;
        } else {
            paddingLength = 128 - leftLength;
        }
        
        byte[] padding = new byte[paddingLength];
        padding[0] = (byte) 0x80;
        
        long messageBitLength = messageLength * 8;
        for(int i = 0; i < 8; i++) {
            padding[padding.length - i - 1] = (byte) ((messageBitLength >> (8 * i)) & 0x00000000000000FF);
        }
        
        byte[] result = new byte[messageLength + paddingLength];
        System.arraycopy(message, 0, result, 0, messageLength);
        System.arraycopy(padding, 0, result, messageLength, paddingLength);

        return result;
    }
    
    public void process(byte[] work, int[] H) {
        int F;
        int temp;
        int A, B, C, D, E;
        
        int[] word = new int[80];
        
        // break 512-bit block into 32-bit word
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 4; j++) {
                temp = (work[i * 4 + j] & 0x000000FF) << (24 - j * 8);
                word[i] = word[i] | temp;
            }
        }
        
        // extends the sixteen 32-bit words into eighty 32-bit word
        for (int j = 16; j < 80; j++) {
            word[j] = rotateLeft(word[j - 3] ^ word[j - 8] ^ word[j - 14] ^ word[j - 16], 1);
        }
        
        // initialize hash value
        A = H[0];
        B = H[1];
        C = H[2];
        D = H[3];
        E = H[4];
        
        // Loop for 0 to 19
        for (int j = 0; j < 20; j++) {
            F = (B & C) | ((~B) & D);
            temp = rotateLeft(A, 5) + F + E + K1 + word[j];
            E = D;
            D = C;
            C = rotateLeft(B, 30);
            B = A;
            A = temp;
        }
        // Loop for 20 to 39
        for (int j = 20; j < 40; j++) {
            F = B ^ C ^ D;
            temp = rotateLeft(A, 5) + F + E + K2 + word[j];
            E = D;
            D = C;
            C = rotateLeft(B, 30);
            B = A;
            A = temp;
        }
        // Loop for 40 to 59
        for (int j = 40; j < 60; j++) {
            F = (B & C) | (B & D) | (C & D);
            temp = rotateLeft(A, 5) + F + E + K3 + word[j];
            E = D;
            D = C;
            C = rotateLeft(B, 30);
            B = A;
            A = temp;
        }
        // Loop for 60 to 79
        for (int j = 60; j < 80; j++) {
            F = B ^ C ^ D;
            temp = rotateLeft(A, 5) + F + E + K4 + word[j];
            E = D;
            D = C;
            C = rotateLeft(B, 30);
            B = A;
            A = temp;
        }
        
        // add result to hash
        H[0] += A;
        H[1] += B;
        H[2] += C;
        H[3] += D;
        H[4] += E;
    }

    final int rotateLeft(int x, int n) {
        int q = (x << n) | (x >>> (32 - n));
        return q;
    }

    private String intArrayToHexStr(int[] data) {
        String result = "";
        String tempStr;
        int tempInt;
        
        for (int i = 0; i < data.length; i++) {
            tempInt = data[i];
            tempStr = Integer.toHexString(tempInt);
            
            // add zero(s) to make data eigth hexa digits
            if (tempStr.length() == 1) {
                tempStr = "0000000" + tempStr;
            } else if (tempStr.length() == 2) {
                tempStr = "000000" + tempStr;
            } else if (tempStr.length() == 3) {
                tempStr = "00000" + tempStr;
            } else if (tempStr.length() == 4) {
                tempStr = "0000" + tempStr;
            } else if (tempStr.length() == 5) {
                tempStr = "000" + tempStr;
            } else if (tempStr.length() == 6) {
                tempStr = "00" + tempStr;
            } else if (tempStr.length() == 7) {
                tempStr = "0" + tempStr;
            }
            result = result + tempStr;
        }
        
        return result;
    }
    
    String digest(byte[] message) {
        // make data length congruen to 448 (mod 512)
        byte[] paddedData = messagePadding(message);

        // initialized disgest message
        int[] H = new int[5];
        H[0] = 0x67452301;
        H[1] = 0xEFCDAB89;
        H[2] = 0x98BADCFE;
        H[3] = 0x10325476;
        H[4] = 0xC3D2E1F0;
        
        int nBlock = paddedData.length / 64;
        byte[] work = new byte[64];
        
        // process block-0 to block-n
        for (int i = 0; i < nBlock; i++) {
            System.arraycopy(paddedData, 64 * i, work, 0, 64);
            process(work, H);
        }

        return intArrayToHexStr(H);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        String text = "a";
        byte[] byteText = text.getBytes();
        
        SHA1 tes = new SHA1();
        System.out.println(tes.digest(byteText));
       
    }
    
}

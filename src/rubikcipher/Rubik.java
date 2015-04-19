/*
 * This code is implementation of our paper's at : 
 * Simulating permutation of Rubik's cube
 */

package rubikcipher;


/**
 *
 * @author Khoirunnisa Afifah (k.afis3@rocketmail.com)
 *         Yollanda Sekarrini 
 */
public class Rubik {
    /**
     * element Rubik[face][id], will be filled by 0/1
     * face number start from 0: up, left, front, right, back, down
     * id number start form 0 to 7, clockwise, without center
     */
    private char elmt[][] = new char[6][8]; 

    /**
     * Perform rotation based on internal key
     * @param iKey is internal key used in Fiestel
     *        from each char of this string, permutation code
     *        compute using getCode function
     */
    public void doRotation(String iKey){
        for(int i=0; i<iKey.length(); i++){
            switch(getCode(iKey.charAt(i))) {
                case 0:
                    rotateR();
                    break;
                case 1:
                    rotateRi();
                    break;
                case 2:
                    rotateL();
                    break;
                case 3:
                    rotateLi();
                    break;
                case 4:
                    rotateU();
                    break;
                case 5:
                    rotateUi();
                    break;
                case 6:
                    rotateD();
                    break;
                case 7:
                    rotateDi();
                    break;
                case 8:
                    rotateF();
                    break;
                case 9:
                    rotateFi();
                    break;
                case 10:
                    rotateB();
                    break;
                case 11:
                    rotateBi();
                    break;
                case 12:
                    rotateM();
                    break;
                case 13:
                    rotateMi();
                    break;
                case 14:
                    rotateE();
                    break;
                case 15:
                    rotateEi();
                    break;
            }
        }
    }
    
    /**
     * Perform reverse of rotation from internal key
     * @param iKey is internal key used in Fiestel
     *        from each char of this string, permutation code
     *        compute using getCode function
     */
    public void reverseRotation(String iKey){
        for(int i=0; i<iKey.length(); i++){
            switch(getCode(iKey.charAt(i))) {
                case 0:
                    rotateRi();
                    break;
                case 1:
                    rotateR();
                    break;
                case 2:
                    rotateLi();
                    break;
                case 3:
                    rotateL();
                    break;
                case 4:
                    rotateUi();
                    break;
                case 5:
                    rotateU();
                    break;
                case 6:
                    rotateDi();
                    break;
                case 7:
                    rotateD();
                    break;
                case 8:
                    rotateFi();
                    break;
                case 9:
                    rotateF();
                    break;
                case 10:
                    rotateBi();
                    break;
                case 11:
                    rotateB();
                    break;
                case 12:
                    rotateMi();
                    break;
                case 13:
                    rotateM();
                    break;
                case 14:
                    rotateEi();
                    break;
                case 15:
                    rotateE();
                    break;
            }
        }
    }
    
    /**
     * @param ch is character used to get the code
     * @return value of four LSB of ch
     *         value is number between 0 to 15 in permutation matrix
     */
    public int getCode(char ch){
        String binary = Integer.toBinaryString(ch);
        if (binary.length()<8) {
            String zero="";
            for (int j=0; j<8-binary.length();j++) {
                zero += "0";
            }
            binary = zero+binary;
        }
       // System.out.println("binary " + binary);
        binary = binary.substring(4);
       // System.out.println("code : " + Integer.parseInt(binary,2));
        return Integer.parseInt(binary,2);
    }
    
    /**
     * Return all Bit from face 0 to face 5
     * @return All bit in BinaryString
     */
    public String readAllBit() {
        String retval="";
        for (int face=0; face<6; face++) {
            for (int id=0; id<8; id++) {
                retval+=elmt[face][id];
            }
        }        
        return retval;
    }
    
    /**
     * Fill the cube with binaryString
     * Size of binaryString should be 48 or only 48 bit will be processed
     * @param binaryString
     */
    public void putAllBit(String binaryString){
        int idx=0;
        for (int face=0; face<6; face++) {
            for (int id=0; id<8; id++) {
                elmt[face][id] = binaryString.charAt(idx);
                idx++;
            }
        } 
    }
    
    /**
     * @param face number of face
     * @param id number
     * @return 
     */
    public char getElement(int face, int id) {
        return elmt[face][id];
    }
    
    /**
     * @param face number of face
     * @param id number
     * @param value 
     */
    public void setElement(int face, int id, char value) {
        elmt[face][id] = value;
    }
    
    /**
     * SIMULATING ALL 16 ROTATION OF RUBIK'S CUBE
     */
    public void rotateR(){
        char temp[][] = new char[6][8];
        //copy elmt to temp
        for (int i = 0; i<6; i++) {
            System.arraycopy(elmt[i], 0, temp[i], 0, 8);
        }
        
        //do rotation on temp
        for (int i = 0; i<8; i++) {
            if (i==0)
                temp[3][i] = elmt[3][6];
            else if (i==1)
                temp[3][i] = elmt[3][7];
            else 
                temp[3][i] = elmt[3][i-2];
        }
        for (int i=2; i<=4; i++) {
            temp[0][i] = elmt[2][i];
            temp[2][i] = elmt[5][i];            
        }
        temp[5][2] = elmt[4][6];
        temp[5][3] = elmt[4][7];
        temp[5][4] = elmt[4][0];
        temp[4][0] = elmt[0][4];
        temp[4][6] = elmt[0][2];
        temp[4][7] = elmt[0][3];
        
        //copy back temp to elmt
        for (int i = 0; i<6; i++) {
            System.arraycopy(temp[i], 0, elmt[i], 0, 8);
        }
    }
    
    public void rotateRi(){
        char temp[][] = new char[6][8];
        //copy elmt to temp
        for (int i = 0; i<6; i++) {
            System.arraycopy(elmt[i], 0, temp[i], 0, 8);
        }
        
        //do rotation on temp
        for (int i = 0; i<8; i++) {
            if (i==7)
                temp[3][i] = elmt[3][1];
            else if (i==6)
                temp[3][i] = elmt[3][0];
            else 
                temp[3][i] = elmt[3][i+2];
        }
        for (int i=2; i<=4; i++) {
            temp[2][i] = elmt[0][i];
            temp[5][i] = elmt[2][i];            
        }
        temp[0][2] = elmt[4][6];
        temp[0][3] = elmt[4][7];
        temp[0][4] = elmt[4][0];
        temp[4][0] = elmt[5][4];
        temp[4][6] = elmt[5][2];
        temp[4][7] = elmt[5][3];
        
        //copy back temp to elmt
        for (int i = 0; i<6; i++) {
            System.arraycopy(temp[i], 0, elmt[i], 0, 8);
        }
    }
    
    public void rotateL(){
        char temp[][] = new char[6][8];
        //copy elmt to temp
        for (int i = 0; i<6; i++) {
            System.arraycopy(elmt[i], 0, temp[i], 0, 8);
        }
        
        //do rotation on temp
        for (int i = 0; i<8; i++) {
            if (i==0)
                temp[1][i] = elmt[1][6];
            else if (i==1)
                temp[1][i] = elmt[1][7];
            else 
                temp[1][i] = elmt[1][i-2];
        }
        temp[2][0] = elmt[0][0];
        temp[2][6] = elmt[0][6];
        temp[2][7] = elmt[0][7];
        temp[5][0] = elmt[2][0];
        temp[5][6] = elmt[2][6];
        temp[5][7] = elmt[2][7];
        
        temp[0][0] = elmt[4][4];
        temp[0][7] = elmt[4][3];
        temp[0][6] = elmt[4][2];
        temp[4][4] = elmt[5][0];
        temp[4][3] = elmt[5][7];
        temp[4][2] = elmt[5][6];
        
        //copy back temp to elmt
        for (int i = 0; i<6; i++) {
            System.arraycopy(temp[i], 0, elmt[i], 0, 8);
        }
        
    }
    
    public void rotateLi(){
        char temp[][] = new char[6][8];
        //copy elmt to temp
        for (int i = 0; i<6; i++) {
            System.arraycopy(elmt[i], 0, temp[i], 0, 8);
        }
        
        //do rotation on temp
        for (int i = 0; i<8; i++) {
            if (i==6)
                temp[1][i] = elmt[1][0];
            else if (i==7)
                temp[1][i] = elmt[1][1];
            else 
                temp[1][i] = elmt[1][i+2];
        }
        temp[2][0] = elmt[5][0];
        temp[2][6] = elmt[5][6];
        temp[2][7] = elmt[5][7];
        temp[0][0] = elmt[2][0];
        temp[0][6] = elmt[2][6];
        temp[0][7] = elmt[2][7];
        
        temp[5][0] = elmt[4][4];
        temp[5][6] = elmt[4][2];
        temp[5][7] = elmt[4][3];
        temp[4][2] = elmt[0][6];
        temp[4][3] = elmt[0][7];
        temp[4][4] = elmt[0][0];
        
        //copy back temp to elmt
        for (int i = 0; i<6; i++) {
            System.arraycopy(temp[i], 0, elmt[i], 0, 8);
        }
        
    }
    
    public void rotateF(){
        char temp[][] = new char[6][8];
        //copy elmt to temp
        for (int i = 0; i<6; i++) {
            System.arraycopy(elmt[i], 0, temp[i], 0, 8);
        }
        
        //do rotation on temp
        for (int i = 0; i<8; i++) {
            if (i==0)
                temp[2][i] = elmt[2][6];
            else if (i==1)
                temp[2][i] = elmt[2][7];
            else 
                temp[2][i] = elmt[2][i-2];
        }
        temp[0][4] = elmt[1][2];
        temp[0][5] = elmt[1][3];
        temp[0][6] = elmt[1][4];
        temp[1][2] = elmt[5][0];
        temp[1][3] = elmt[5][1];
        temp[1][4] = elmt[5][2];
        
        temp[3][0] = elmt[0][6];
        temp[3][7] = elmt[0][5];
        temp[3][6] = elmt[0][4];
        temp[5][0] = elmt[3][6];
        temp[5][1] = elmt[3][7];
        temp[5][2] = elmt[3][0];
        
        //copy back temp to elmt
        for (int i = 0; i<6; i++) {
            System.arraycopy(temp[i], 0, elmt[i], 0, 8);
        }
    }
    
    public void rotateFi(){
        char temp[][] = new char[6][8];
        //copy elmt to temp
        for (int i = 0; i<6; i++) {
            System.arraycopy(elmt[i], 0, temp[i], 0, 8);
        }
        
        //do rotation on temp
        for (int i = 0; i<8; i++) {
            if (i==6)
                temp[2][i] = elmt[2][0];
            else if (i==7)
                temp[2][i] = elmt[2][1];
            else 
                temp[2][i] = elmt[2][i+2];
        }
        temp[0][4] = elmt[3][6];
        temp[0][5] = elmt[3][7];
        temp[0][6] = elmt[3][0];
        temp[1][2] = elmt[0][4];
        temp[1][3] = elmt[0][5];
        temp[1][4] = elmt[0][6];
        temp[3][0] = elmt[5][2];
        temp[3][7] = elmt[5][1];
        temp[3][6] = elmt[5][0];
        temp[5][0] = elmt[1][2];
        temp[5][1] = elmt[1][3];
        temp[5][2] = elmt[1][4];
        
        //copy back temp to elmt
        for (int i = 0; i<6; i++) {
            System.arraycopy(temp[i], 0, elmt[i], 0, 8);
        }
    }
    
    public void rotateB(){
        char temp[][] = new char[6][8];
        //copy elmt to temp
        for (int i = 0; i<6; i++) {
            System.arraycopy(elmt[i], 0, temp[i], 0, 8);
        }
        
        //do rotation on temp
        for (int i = 0; i<8; i++) {
            if (i==0)
                temp[4][i] = elmt[4][6];
            else if (i==1)
                temp[4][i] = elmt[4][7];
            else 
                temp[4][i] = elmt[4][i-2];
        }
        temp[0][0] = elmt[3][2];
        temp[0][1] = elmt[3][3];
        temp[0][2] = elmt[3][4];
        temp[1][0] = elmt[0][2];
        temp[1][7] = elmt[0][1];
        temp[1][6] = elmt[0][0];
        
        temp[3][2] = elmt[5][4];
        temp[3][3] = elmt[5][5];
        temp[3][4] = elmt[5][6];
        temp[5][4] = elmt[1][6];
        temp[5][5] = elmt[1][7];
        temp[5][6] = elmt[1][0];
        
        //copy back temp to elmt
        for (int i = 0; i<6; i++) {
            System.arraycopy(temp[i], 0, elmt[i], 0, 8);
        }
    }
    
    public void rotateBi(){
        char temp[][] = new char[6][8];
        //copy elmt to temp
        for (int i = 0; i<6; i++) {
            System.arraycopy(elmt[i], 0, temp[i], 0, 8);
        }
        
        //do rotation on temp
        for (int i = 0; i<8; i++) {
            if (i==6)
                temp[4][i] = elmt[4][0];
            else if (i==7)
                temp[4][i] = elmt[4][1];
            else 
                temp[4][i] = elmt[4][i+2];
        }
        temp[0][0] = elmt[1][6];
        temp[0][1] = elmt[1][7];
        temp[0][2] = elmt[1][0];
        temp[1][0] = elmt[5][6];
        temp[1][7] = elmt[5][5];
        temp[1][6] = elmt[5][4];
        
        temp[3][2] = elmt[0][0];
        temp[3][3] = elmt[0][1];
        temp[3][4] = elmt[0][2];
        temp[5][4] = elmt[3][2];
        temp[5][5] = elmt[3][3];
        temp[5][6] = elmt[3][4];
        
        //copy back temp to elmt
        for (int i = 0; i<6; i++) {
            System.arraycopy(temp[i], 0, elmt[i], 0, 8);
        }
    }
    
    public void rotateU(){
        char temp[][] = new char[6][8];
        //copy elmt to temp
        for (int i = 0; i<6; i++) {
            System.arraycopy(elmt[i], 0, temp[i], 0, 8);
        }
        
        //do rotation on temp
        for (int i = 0; i<8; i++) {
            if (i==0)
                temp[0][i] = elmt[0][6];
            else if (i==1)
                temp[0][i] = elmt[0][7];
            else 
                temp[0][i] = elmt[0][i-2];
        }
        temp[2][0] = elmt[3][0];
        temp[2][1] = elmt[3][1];
        temp[2][2] = elmt[3][2];
        temp[1][0] = elmt[2][0];
        temp[1][1] = elmt[2][1];
        temp[1][2] = elmt[2][2];
        temp[3][0] = elmt[4][0];
        temp[3][1] = elmt[4][1];
        temp[3][2] = elmt[4][2];
        temp[4][0] = elmt[1][0];
        temp[4][1] = elmt[1][1];
        temp[4][2] = elmt[1][2];
        
        //copy back temp to elmt
        for (int i = 0; i<6; i++) {
            System.arraycopy(temp[i], 0, elmt[i], 0, 8);
        }
    }
    
    public void rotateUi(){
        char temp[][] = new char[6][8];
        //copy elmt to temp
        for (int i = 0; i<6; i++) {
            System.arraycopy(elmt[i], 0, temp[i], 0, 8);
        }
        
        //do rotation on temp
        for (int i = 0; i<8; i++) {
            if (i==6)
                temp[0][i] = elmt[0][0];
            else if (i==7)
                temp[0][i] = elmt[0][1];
            else 
                temp[0][i] = elmt[0][i+2];
        }
        temp[2][0] = elmt[1][0];
        temp[2][1] = elmt[1][1];
        temp[2][2] = elmt[1][2];
        temp[1][0] = elmt[4][0];
        temp[1][1] = elmt[4][1];
        temp[1][2] = elmt[4][2];
        temp[3][0] = elmt[2][0];
        temp[3][1] = elmt[2][1];
        temp[3][2] = elmt[2][2];
        temp[4][0] = elmt[3][0];
        temp[4][1] = elmt[3][1];
        temp[4][2] = elmt[3][2];
        
        //copy back temp to elmt
        for (int i = 0; i<6; i++) {
            System.arraycopy(temp[i], 0, elmt[i], 0, 8);
        }
    }
    
    public void rotateD(){
        char temp[][] = new char[6][8];
        //copy elmt to temp
        for (int i = 0; i<6; i++) {
            System.arraycopy(elmt[i], 0, temp[i], 0, 8);
        }
        
        //do rotation on temp
        for (int i = 0; i<8; i++) {
            if (i==0)
                temp[5][i] = elmt[5][6];
            else if (i==1)
                temp[5][i] = elmt[5][7];
            else 
                temp[5][i] = elmt[5][i-2];
        }
        temp[2][6] = elmt[1][6];
        temp[2][5] = elmt[1][5];
        temp[2][4] = elmt[1][4];
        temp[1][6] = elmt[4][6];
        temp[1][5] = elmt[4][5];
        temp[1][4] = elmt[4][4];
        
        temp[3][6] = elmt[2][6];
        temp[3][5] = elmt[2][5];
        temp[3][4] = elmt[2][4];
        temp[4][6] = elmt[3][6];
        temp[4][5] = elmt[3][5];
        temp[4][4] = elmt[3][4];
        
        //copy back temp to elmt
        for (int i = 0; i<6; i++) {
            System.arraycopy(temp[i], 0, elmt[i], 0, 8);
        }
    }

    public void rotateDi(){
        char temp[][] = new char[6][8];
        //copy elmt to temp
        for (int i = 0; i<6; i++) {
            System.arraycopy(elmt[i], 0, temp[i], 0, 8);
        }
        
        //do rotation on temp
        for (int i = 0; i<8; i++) {
            if (i==6)
                temp[5][i] = elmt[5][0];
            else if (i==7)
                temp[5][i] = elmt[5][1];
            else 
                temp[5][i] = elmt[5][i+2];
        }
        temp[2][6] = elmt[3][6];
        temp[2][5] = elmt[3][5];
        temp[2][4] = elmt[3][4];
        temp[1][6] = elmt[2][6];
        temp[1][5] = elmt[2][5];
        temp[1][4] = elmt[2][4];
        temp[3][6] = elmt[4][6];
        temp[3][5] = elmt[4][5];
        temp[3][4] = elmt[4][4];
        temp[4][6] = elmt[1][6];
        temp[4][5] = elmt[1][5];
        temp[4][4] = elmt[1][4];
        
        //copy back temp to elmt
        for (int i = 0; i<6; i++) {
            System.arraycopy(temp[i], 0, elmt[i], 0, 8);
        }
    }

    public void rotateE(){
        char temp[][] = new char[6][8];
        //copy elmt to temp
        for (int i = 0; i<6; i++) {
            System.arraycopy(elmt[i], 0, temp[i], 0, 8);
        }
        
        //do rotation on temp
        temp[1][7] = elmt[4][7];
        temp[1][3] = elmt[4][3];
        temp[2][7] = elmt[1][7];
        temp[2][3] = elmt[1][3];
        
        temp[3][7] = elmt[2][7];
        temp[3][3] = elmt[2][3];
        temp[4][7] = elmt[3][7];
        temp[4][3] = elmt[3][3];
        
        
        //copy back temp to elmt
        for (int i = 0; i<6; i++) {
            System.arraycopy(temp[i], 0, elmt[i], 0, 8);
        }
    }
    
    public void rotateEi(){
        char temp[][] = new char[6][8];
        //copy elmt to temp
        for (int i = 0; i<6; i++) {
            System.arraycopy(elmt[i], 0, temp[i], 0, 8);
        }
        
        //do rotation on temp
        temp[1][7] = elmt[2][7];
        temp[1][3] = elmt[2][3];
        temp[2][7] = elmt[3][7];
        temp[2][3] = elmt[3][3];
        
        temp[3][7] = elmt[4][7];
        temp[3][3] = elmt[4][3];
        temp[4][7] = elmt[1][7];
        temp[4][3] = elmt[1][3];
        
        
        //copy back temp to elmt
        for (int i = 0; i<6; i++) {
            System.arraycopy(temp[i], 0, elmt[i], 0, 8);
        }
    }
    
    public void rotateM(){
        char temp[][] = new char[6][8];
        //copy elmt to temp
        for (int i = 0; i<6; i++) {
            System.arraycopy(elmt[i], 0, temp[i], 0, 8);
        }
        
        //do rotation on temp
        temp[0][1] = elmt[2][1];
        temp[0][5] = elmt[2][5];
        temp[2][1] = elmt[5][1];
        temp[2][5] = elmt[5][5];
        
        temp[5][1] = elmt[4][5];
        temp[5][5] = elmt[4][1];
        temp[4][5] = elmt[0][1];
        temp[4][1] = elmt[0][5];
        
        //copy back temp to elmt
        for (int i = 0; i<6; i++) {
            System.arraycopy(temp[i], 0, elmt[i], 0, 8);
        }
    }
    
    public void rotateMi(){
        char temp[][] = new char[6][8];
        //copy elmt to temp
        for (int i = 0; i<6; i++) {
            System.arraycopy(elmt[i], 0, temp[i], 0, 8);
        }
        
        //do rotation on temp
        temp[0][1] = elmt[4][5];
        temp[0][5] = elmt[4][1];
        temp[2][1] = elmt[0][1];
        temp[2][5] = elmt[0][5];
        
        temp[5][1] = elmt[2][1];
        temp[5][5] = elmt[2][5];
        temp[4][5] = elmt[5][1];
        temp[4][1] = elmt[5][5];
        
        //copy back temp to elmt
        for (int i = 0; i<6; i++) {
            System.arraycopy(temp[i], 0, elmt[i], 0, 8);
        }
    }
    
}

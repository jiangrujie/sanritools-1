package sanri.utils;
import java.security.*;
import javax.crypto.*;

/*
复杂的对称加密（DES、PBE）、非对称加密算法： 
DES(Data Encryption Standard，数据加密算法) 
PBE(Password-based encryption，基于密码验证) 
RSA(算法的名字以发明者的名字命名：Ron Rivest, AdiShamir 和Leonard Adleman) 
DH(Diffie-Hellman算法，密钥一致协议) 
DSA(Digital Signature Algorithm，数字签名) 
ECC(Elliptic Curves Cryptography，椭圆曲线密码编码学) 
*/
public class DESUtil{
    private static String strDefaultKey = "999999";
    private Cipher encryptCipher = null;
    private Cipher decryptCipher = null;

    /**
     * 默认构造方法，使用默认密钥
     * 
     * @throws Exception
     */
    public DESUtil() throws Exception {
        this(strDefaultKey);
    }

    /**
     * 指定密钥构造方法
     * 
     * @param strKey
     *            指定的密钥
     * @throws Exception
     */
    public DESUtil(String strKey) {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        Key key;
        try {
            key = getKey(strKey.getBytes());
            encryptCipher = Cipher.getInstance("DES");
            encryptCipher.init(Cipher.ENCRYPT_MODE, key);
            decryptCipher = Cipher.getInstance("DES");
            decryptCipher.init(Cipher.DECRYPT_MODE, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 将启动码strIn加密，返回加密后的启动码
     */
    public String encrypt(String strIn) {
   	 byte[] s = null ; StringBuffer sb=null;
   	 try{
    	 s = encryptCipher.doFinal(strIn.getBytes());
    	 int iLen = s.length;
        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
    	 //将byte数组转换为表示16进制值的字符串， 如：byte[]{8,18}转换为：0813
    	 sb= new StringBuffer(iLen * 2);
        for (int i = 0; i < iLen; i++) {
            int intTmp = s[i];
            // 把负数转换为正数
            while (intTmp < 0) {
                intTmp = intTmp + 256;
            }
            // 小于0F的数需要在前面补0
            if (intTmp < 16) {
                sb.append("0");
            }
            sb.append(Integer.toString(intTmp, 16));
        }
   	 }catch(Exception ex){ex.printStackTrace();}
   	 
        return sb.toString();
   }


    /**
     * 解密 strIn 
     */
    public String decrypt(String strIn){
    	String s = null ;
    	byte[] arrB = strIn.getBytes();
    	try{
        int iLen = arrB.length;
        // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
        //将表示16进制值的字符串转换为byte数组
        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i = i + 2) {
            String strTmp = new String(arrB, i, 2);
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
        }
        s = new String(decryptCipher.doFinal(arrOut));//解密字节数组
    	 } catch (Exception e) {
             e.printStackTrace();
         }
        return s;
    }
    /**
     * 从指定字符串生成密钥，密钥所需的字节数组长度为8位 不足8位时后面补0，超出8位只取前8位
     * 
     * @param arrBTmp
     *            构成该字符串的字节数组
     * @return 生成的密钥
     * @throws java.lang.Exception
     */
    private Key getKey(byte[] arrBTmp) {
        // 创建一个空的8位字节数组（默认值为0）
        byte[] arrB = new byte[8];
        // 将原始字节数组转换为8位
        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }
        // 生成密钥
        Key key = new javax.crypto.spec.SecretKeySpec(arrB, "DES");
        return key;
    }
    
    public static void main(String[] args) {
		System.out.println((new DESUtil("sanri")).encrypt("h123"));
	}
}

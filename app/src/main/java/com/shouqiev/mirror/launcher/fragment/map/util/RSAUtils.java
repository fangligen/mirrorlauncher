package com.shouqiev.mirror.launcher.fragment.map.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Base64;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.security.Key;
import javax.crypto.Cipher;

public class RSAUtils {

  private static final String ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";
  static final String PATH_PUBLIC_KEY = "key/PublicKey";
  static final String PATH_SECRET_KEY = "key/SecretKey";
  static final String SIGN_SPLIT = "#";

  private static RSAUtils rsaUtils;

  private RSAUtils() {
  }

  public static RSAUtils getInstance() {
    if (rsaUtils == null) {
      synchronized (RSAUtils.class) {
        if (rsaUtils == null) {
          rsaUtils = new RSAUtils();
        }
      }
    }
    return rsaUtils;
  }

  /**
   * 公钥解密
   *
   * @param data 待解密数据
   * @param inputStream 密钥
   * @return byte[] 解密数据
   */
  public byte[] decryptByPublicKey(String data, InputStream inputStream) throws Exception {
    Key publicKey = getKey(inputStream);
    return decryptByPublicKey(data, publicKey);
  }

  public byte[] decryptByPublicKey(String data, Key publicKey) throws Exception {
    Cipher cipher = Cipher.getInstance(ECB_PKCS1_PADDING);
    cipher.init(Cipher.DECRYPT_MODE, publicKey);
    byte[] t = Base64.decode(data, Base64.DEFAULT);
    return cipher.doFinal(t);
  }

  private Key getKey(InputStream inputStream) throws Exception {
    Key key;
    ObjectInputStream ois = null;
    try {
      ois = new ObjectInputStream(inputStream);
      key = (Key) ois.readObject();
    } catch (Exception e) {
      throw e;
    } finally {
      if (ois != null) {
        ois.close();
      }
    }
    return key;
  }

  /**
   * 用公钥对字符串进行加密
   *
   * @param data 原文
   */
  public byte[] encryptByPublicKey(String data, InputStream keyStream) throws Exception {
    Key keyPublic = getKey(keyStream);
    return encryptByPublicKey(data, keyPublic);
  }

  public byte[] encryptByPublicKey(String data, Key publicKey) throws Exception {
    byte[] t = data.getBytes();
    Cipher cp = Cipher.getInstance(ECB_PKCS1_PADDING);
    cp.init(Cipher.ENCRYPT_MODE, publicKey);
    return cp.doFinal(t);
  }

  public String getSecret(InputStream inputStream) {
    String secret = null;
    ObjectInputStream ois = null;
    try {
      ois = new ObjectInputStream(inputStream);
      secret = (String) ois.readObject();
    } catch (Exception e) {
      e.printStackTrace();
      secret = null;
    } finally {
      if (ois != null) {
        try {
          ois.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return secret;
  }

  public String getSign(Context context) {
    AssetManager assetManager = context.getAssets();
    InputStream pk, sk;
    try {
      pk = assetManager.open(PATH_PUBLIC_KEY);
      sk = assetManager.open(PATH_SECRET_KEY);
      String secret = getSecret(sk);
      Key publicKey = getKey(pk);
      byte[] result = decryptByPublicKey(secret, publicKey);
      String cryptography = new String(result);
      String sig = getCiphertext(CommonUtils.getDeviceId(), "#", CommonUtils.getDeviceId(), "#", cryptography);
      byte[] signB = encryptByPublicKey(sig, publicKey);
      return Base64.encodeToString(signB, Base64.NO_WRAP);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private String getCiphertext(String... pars) {
    StringBuffer stringBuffer = new StringBuffer();
    for (String s : pars) {
      stringBuffer.append(s);
    }
    return stringBuffer.toString();
  }
}

package com.reactnativepbkdf2;

import android.util.Base64;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.digests.SHA1Digest;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.digests.SHA512Digest;
import org.spongycastle.crypto.digests.SHA224Digest;
import org.spongycastle.crypto.digests.SHA384Digest;
import org.spongycastle.crypto.digests.RIPEMD160Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;

@ReactModule(name = Pbkdf2Module.NAME)
public class Pbkdf2Module extends ReactContextBaseJavaModule {
  public static final String NAME = "Pbkdf2";

  public Pbkdf2Module(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  public static String pbkdf2Native(String password, String salt, int iterations, int keySize, String hash) {
    byte[] decodedPassword = android.util.Base64.decode(password, Base64.DEFAULT);
    byte[] decodedSalt = android.util.Base64.decode(salt, Base64.DEFAULT);

    return pbkdf2Native(decodedPassword, decodedSalt, iterations, keySize, hash);
  }

  public static String pbkdf2Native(byte[] password, String salt, int iterations, int keySize, String hash) {
    byte[] decodedSalt = salt.getBytes();
    return pbkdf2Native(password, decodedSalt, iterations, keySize, hash);
  }

  public static String pbkdf2Native(String password, byte[] salt, int iterations, int keySize, String hash) {
    byte[] decodedPassword = password.getBytes();
    return pbkdf2Native(decodedPassword, salt, iterations, keySize, hash);
  }

  public static String pbkdf2Native(byte[] password, byte[] salt, int iterations, int keySize, String hash) {
    Digest digest = getDigestByName(hash);
    PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(digest);
    gen.init(password, salt, iterations);
    byte[] key = ((KeyParameter) gen.generateDerivedParameters(keySize * 8)).getKey();
    return android.util.Base64.encodeToString(key, Base64.DEFAULT);
  }

  private static Digest getDigestByName(String digestName) {
    switch (digestName.toLowerCase()) {
      case "sha1":
        return new SHA1Digest();
      case "sha256":
        return new SHA256Digest();
      case "sha512":
        return new SHA512Digest();
      case "sha224":
        return new SHA224Digest();
      case "sha384":
        return new SHA384Digest();
      case "ripemd160":
        return new RIPEMD160Digest();
      default:
        throw new Error("Invalid digest:" + digestName.toLowerCase());
    }
  }

  @ReactMethod
  public void derive(String password, String salt, int iterations, int keySize, String hash, Promise promise) {
    promise.resolve(pbkdf2Native(password, salt, iterations, keySize, hash));
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public String deriveSync(String password, String salt, int iterations, int keySize, String hash) {
    return pbkdf2Native(password, salt, iterations, keySize, hash);
  }
}

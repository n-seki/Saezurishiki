package com.seki.saezurishiki.file;

import android.content.Context;
import android.util.Base64;

import com.facebook.android.crypto.keychain.AndroidConceal;
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.CryptoConfig;
import com.facebook.crypto.Entity;
import com.facebook.crypto.exception.CryptoInitializationException;
import com.facebook.crypto.exception.KeyChainException;

import java.io.IOException;


public final class EncryptUtil {

    private final static Entity entity = Entity.create("saezurishiki");

    //private final Crypto crypto;

    public static String encrypt(String plainText, Context context) {
        final Crypto crypto = AndroidConceal.get().createDefaultCrypto(new SharedPrefsBackedKeyChain(context, CryptoConfig.KEY_256));
        if (!crypto.isAvailable()) {
            throw new IllegalArgumentException("Conceal is not available");
        }

        try {
            final byte[] data = crypto.encrypt(plainText.getBytes(), entity);
            return Base64.encodeToString(data, Base64.DEFAULT);
        } catch (KeyChainException | CryptoInitializationException | IOException e) {
            throw new IllegalStateException("encrypt is not success");
        }
    }


    public static String decrypt(String cipherText, Context context) {
        final Crypto crypto = AndroidConceal.get().createDefaultCrypto(new SharedPrefsBackedKeyChain(context, CryptoConfig.KEY_256));
        if (!crypto.isAvailable()) {
            throw new IllegalArgumentException("Conceal is not available");
        }

        try {
            byte[] data = Base64.decode(cipherText.getBytes(), Base64.DEFAULT);
            return new String(crypto.decrypt(data, entity));
        } catch (KeyChainException | CryptoInitializationException | IOException e) {
            throw new IllegalStateException("decrypt is not success");
        }

    }

}

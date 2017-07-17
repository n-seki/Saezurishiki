package com.seki.saezurishiki.file;

import android.content.Context;
import android.support.annotation.Nullable;

import com.seki.saezurishiki.entity.UserEntity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import twitter4j.User;

public class Serializer {

    private final static String LOGIN_USER_FILE = "loginUser.dat";

    public static void saveUser(Context context, UserEntity user) {
        if ( user == null ) {
            return;
        }

        ObjectOutput out = null;

        try {
            out = new ObjectOutputStream(context.openFileOutput(LOGIN_USER_FILE, Context.MODE_PRIVATE));
            out.writeObject(user);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public static UserEntity loadUser(Context context) {
        return load(context, UserEntity.class, LOGIN_USER_FILE);
    }


    @Nullable
    private static <T> T load(Context context, Class<T> type, String key) {
        ObjectInputStream in = null;

        try {
            in = new ObjectInputStream(context.openFileInput(key));
            Object tmp = in.readObject();

            return type.cast(tmp);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}

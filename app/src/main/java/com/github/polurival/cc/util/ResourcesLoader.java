package com.github.polurival.cc.util;

import java.lang.reflect.Field;

/**
 * Created by Polurival
 * on 13.07.2016.
 *
 * <p>See <a href="http://stackoverflow.com/a/4428288/5349748">Source</a></p>
 */
public class ResourcesLoader {
    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}

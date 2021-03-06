/**
 * The SecureSharePreference is a custom extension of SharePreference, a light-weight database provided by Android.
 * SecureSharePreference would encrypt the records before storing it in the database file,
 * Instead of storing the plain text.
 *
 * Although Android provides an EncryptedSharePreference, which also encrypts the records,
 * this costumed extension is used in this project because EncryptedSharePreference is only available
 * on API 23 (Android 6 )and above, but the min sdk of this project is API 19 (Android 5)
 */
package com.katcom.androidFileVault.SecureSharePreference;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import java.util.Map;
import java.util.Set;

public class SecureSharePreference implements SharedPreferences {
    private static SharedPreferences share;
    //private static SecureSharePreference secretShare;
    private SecureSharePreference(Context context,String name){
        share = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static SecureSharePreference getInstance(Context context,String name){
            return new SecureSharePreference(context,name);

    }

    @Override
    public Map<String, ?> getAll() {
        return share.getAll();
    }

    @Nullable
    @Override
    public String getString(String key, @Nullable String defValue) {
        return share.getString(key,defValue);
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        return share.getStringSet(key,defValues);
    }

    @Override
    public int getInt(String key, int defValue) {
        return share.getInt(key,defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return share.getLong(key,defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return share.getFloat(key,defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return share.getBoolean(key,defValue);
    }

    @Override
    public boolean contains(String key) {
        return share.contains(key);
    }

    @Override
    public Editor edit() {
        return share.edit();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        share.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        share.unregisterOnSharedPreferenceChangeListener(listener);
    }


}

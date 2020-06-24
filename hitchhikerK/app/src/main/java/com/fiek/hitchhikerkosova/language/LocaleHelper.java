package com.fiek.hitchhikerkosova.language;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import java.util.Locale;

public class LocaleHelper {
    private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";

    public static String getLanguage(Context context) {

        return getPreferredLanguage(context, Locale.getDefault().getLanguage());

    }
    private static String getPreferredLanguage(Context context, String defaultLanguage) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage);

    }
    public static Context setLocale(Context context, String language) {

        setPreferredLanguage(context, language);

        return updateResources(context, language);

    }
    private static void setPreferredLanguage(Context context, String language) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(SELECTED_LANGUAGE, language);

        editor.apply();

    }

    private static Context updateResources(Context context, String language) {

        Locale locale = new Locale(language);

        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();

        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;

    }
}

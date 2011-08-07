package com.orangetentacle.gorillas;

import android.content.SharedPreferences;

/**
 * Configuration Class
 */
public class Configuration {

    public static final String PREF_NAME = "Gorillas";

    public int Rounds;
    public String Player1Name;
    public String Player2Name;

    public Configuration(SharedPreferences pref) {
        loadPrefs(pref);
    }

    public void loadPrefs(SharedPreferences pref)
    {
        Rounds = pref.getInt("Rounds", 3);
        Player1Name = pref.getString("Player1Name", "Player1");
        Player2Name = pref.getString("Player2Name", "Player2");
    }

    public void savePrefs(SharedPreferences pref)
    {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("Rounds", Rounds);
        editor.putString("Player1Name", Player1Name);
        editor.putString("Player2Name", Player2Name);

        editor.commit();
    }
}

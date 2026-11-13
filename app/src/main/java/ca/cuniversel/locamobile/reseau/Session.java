package ca.cuniversel.locamobile.reseau;

import android.content.Context;
import android.content.SharedPreferences;

// Carnet clé -> valeur privé. On NE range JAMAIS le mot de passe.
public class Session {
    private static final String FICHIER = "session";

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(FICHIER, Context.MODE_PRIVATE);
    }

    public static void enregistrer(Context c, String jeton, String nom) {
        prefs(c).edit().putString("jeton", jeton).putString("nom", nom).apply();
    }

    public static String jeton(Context c) {
        return prefs(c).getString("jeton", null);   // null = personne de connecté
    }

    public static String nom(Context c) {
        return prefs(c).getString("nom", null);
    }
}
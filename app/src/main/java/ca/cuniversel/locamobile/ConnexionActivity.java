package ca.cuniversel.locamobile;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ca.cuniversel.locamobile.reseau.ClientApi;
import ca.cuniversel.locamobile.reseau.ConnexionRequete;
import ca.cuniversel.locamobile.reseau.Profil;
import ca.cuniversel.locamobile.reseau.Session;
import ca.cuniversel.locamobile.reseau.TokenReponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConnexionActivity extends AppCompatActivity {

    private static final String TAG = "LocaMobile";

    private EditText champCourriel, champMotDePasse;
    private TextView resultat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        champCourriel = findViewById(R.id.champCourriel);
        champMotDePasse = findViewById(R.id.champMotDePasse);
        resultat = findViewById(R.id.resultat);

        findViewById(R.id.boutonSeConnecter).setOnClickListener(v -> seConnecter());
    }

    private void seConnecter() {
        String courriel = champCourriel.getText().toString().trim();
        String motDePasse = champMotDePasse.getText().toString();

        if (courriel.isEmpty()) { champCourriel.setError("Courriel obligatoire"); return; }
        if (motDePasse.isEmpty()) { champMotDePasse.setError("Mot de passe obligatoire"); return; }

        ClientApi.get().connecter(new ConnexionRequete(courriel, motDePasse))
                .enqueue(new Callback<TokenReponse>() {
                    @Override
                    public void onResponse(@NonNull Call<TokenReponse> call,
                                           @NonNull Response<TokenReponse> reponse) {
                        if (reponse.isSuccessful() && reponse.body() != null) {
                            TokenReponse t = reponse.body();
                            Session.enregistrer(ConnexionActivity.this, t.token, t.nom);
                            // Le jeton existe enfin : on peut appeler l'endpoint protégé, ici et pas avant
                            chargerProfil(t.token);
                        } else {
                            resultat.setText("Erreur " + reponse.code() + " : " + messageDuServeur(reponse));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<TokenReponse> call, @NonNull Throwable t) {
                        resultat.setText("Serveur injoignable : " + t.getMessage());
                    }
                });
    }

    private void chargerProfil(String jeton) {
        // Un seul espace après Bearer
        ClientApi.get().monProfil("Bearer " + jeton).enqueue(new Callback<Profil>() {
            @Override
            public void onResponse(@NonNull Call<Profil> call, @NonNull Response<Profil> reponse) {
                Log.d(TAG, "api/moi : code " + reponse.code());
                if (reponse.isSuccessful() && reponse.body() != null) {
                    Profil p = reponse.body();
                    resultat.setText("Nom : " + p.nom + "\nRôle : " + p.role
                            + "\nCourriel : " + p.courriel + "\nCode HTTP : " + reponse.code());
                } else {
                    resultat.setText("Erreur " + reponse.code() + " : " + messageDuServeur(reponse));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Profil> call, @NonNull Throwable t) {
                resultat.setText("Serveur injoignable : " + t.getMessage());
            }
        });
    }

    // Sur une erreur, body() est null : le message est dans errorBody()
    private String messageDuServeur(Response<?> reponse) {
        try {
            if (reponse.errorBody() != null) {
                String texte = reponse.errorBody().string();
                JsonObject json = new Gson().fromJson(texte, JsonObject.class);
                if (json != null && json.has("message")) {
                    return json.get("message").getAsString();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Lecture du message impossible : " + e.getMessage());
        }
        return "(aucun message du serveur)";
    }
}
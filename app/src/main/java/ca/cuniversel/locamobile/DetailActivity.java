package ca.cuniversel.locamobile;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import ca.cuniversel.locamobile.reseau.ClientApi;
import ca.cuniversel.locamobile.reseau.Logement;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    public static final String CLE_ID = "id";
    private static final String TAG = "LocaMobile";

    private TextView etat, titre, ville, prix, voyageurs, proprietaire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        etat = findViewById(R.id.etat);
        titre = findViewById(R.id.detailTitre);
        ville = findViewById(R.id.detailVille);
        prix = findViewById(R.id.detailPrix);
        voyageurs = findViewById(R.id.detailVoyageurs);
        proprietaire = findViewById(R.id.detailProprietaire);

        int id = getIntent().getIntExtra(CLE_ID, -1);
        // Test du 404 (plus tard) : remplacer temporairement la ligne ci-dessus par
        // int id = 999;

        ClientApi.get().trouverLogement(id).enqueue(new Callback<Logement>() {
            @Override
            public void onResponse(@NonNull Call<Logement> call,
                                   @NonNull Response<Logement> reponse) {
                Log.d(TAG, "URL : " + call.request().url() + " -> code " + reponse.code());
                if (reponse.isSuccessful() && reponse.body() != null) {
                    Logement l = reponse.body();
                    etat.setText("");
                    titre.setText(l.titre);
                    ville.setText(l.ville);
                    prix.setText(String.format(Locale.CANADA_FRENCH, "%.2f $ / nuit", l.prixParNuit));
                    voyageurs.setText(l.nbVoyageurs + " voyageurs");
                    proprietaire.setText("Propriétaire : " + l.proprietaireNom);
                } else if (reponse.code() == 404) {
                    etat.setText("Ce logement n'existe plus (404).");
                } else {
                    etat.setText("Erreur HTTP " + reponse.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Logement> call, @NonNull Throwable t) {
                etat.setText("Serveur injoignable : " + t.getMessage());
            }
        });
    }
}
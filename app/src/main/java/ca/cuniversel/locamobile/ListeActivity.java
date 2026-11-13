package ca.cuniversel.locamobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;

import ca.cuniversel.locamobile.reseau.ClientApi;
import ca.cuniversel.locamobile.reseau.Logement;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListeActivity extends AppCompatActivity {

    private static final String TAG = "LocaMobile";

    private LinearLayout conteneurLogements;
    private TextView messageErreur;
    private EditText champVille;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste);

        conteneurLogements = findViewById(R.id.conteneurLogements);
        messageErreur = findViewById(R.id.messageErreur);
        champVille = findViewById(R.id.champVille);

        findViewById(R.id.boutonChercher).setOnClickListener(v -> charger());
        findViewById(R.id.boutonConnexion).setOnClickListener(v ->
                startActivity(new Intent(this, ConnexionActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        charger();
    }

    // Champ vide = liste complète, sinon recherche par ville
    private void charger() {
        final String ville = champVille.getText().toString().trim();
        Call<List<Logement>> appel = ville.isEmpty()
                ? ClientApi.get().listerLogements()
                : ClientApi.get().chercherParVille(ville);

        appel.enqueue(new Callback<List<Logement>>() {
            @Override
            public void onResponse(@NonNull Call<List<Logement>> call,
                                   @NonNull Response<List<Logement>> reponse) {
                Log.d(TAG, "URL : " + call.request().url() + " -> code " + reponse.code());
                if (reponse.isSuccessful() && reponse.body() != null) {
                    List<Logement> liste = reponse.body();
                    if (liste.isEmpty()) {
                        afficherErreur("Aucun logement à " + ville + ".");
                    } else {
                        afficher(liste);
                    }
                } else {
                    afficherErreur("Erreur HTTP " + reponse.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Logement>> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure : " + t.getMessage());
                afficherErreur("Serveur injoignable : " + t.getMessage());
            }
        });
    }

    private void afficher(List<Logement> logements) {
        messageErreur.setVisibility(View.GONE);
        conteneurLogements.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Logement l : logements) {
            View carte = inflater.inflate(R.layout.item_logement, conteneurLogements, false);
            ((TextView) carte.findViewById(R.id.carteTitre)).setText(l.titre);
            ((TextView) carte.findViewById(R.id.carteVille)).setText(l.ville);
            ((TextView) carte.findViewById(R.id.cartePrix)).setText(
                    String.format(Locale.CANADA_FRENCH, "%.2f $ / nuit", l.prixParNuit));

            // Un seul extra : l'id du logement
            carte.setOnClickListener(v -> {
                Intent intent = new Intent(this, DetailActivity.class);
                intent.putExtra(DetailActivity.CLE_ID, l.id);
                startActivity(intent);
            });
            conteneurLogements.addView(carte);
        }
    }

    private void afficherErreur(String texte) {
        conteneurLogements.removeAllViews();
        messageErreur.setText(texte);
        messageErreur.setVisibility(View.VISIBLE);
    }
}
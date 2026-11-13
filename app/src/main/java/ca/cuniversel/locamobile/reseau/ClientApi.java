package ca.cuniversel.locamobile.reseau;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Singleton : Retrofit n'est construit qu'une seule fois
public class ClientApi {
    private static LocaMobileApi instance;

    private ClientApi() { }

    public static LocaMobileApi get() {
        if (instance == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            instance = retrofit.create(LocaMobileApi.class);
        }
        return instance;
    }
}
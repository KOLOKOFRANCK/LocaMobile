package ca.cuniversel.locamobile.reseau;

// Moule du JSON : mêmes noms que les clés renvoyées par l'API
public class Logement {
    public int id;
    public String titre;
    public String ville;
    public double prixParNuit;
    public int nbVoyageurs;
    public boolean disponible;
    public String proprietaireNom;
}
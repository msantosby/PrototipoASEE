package es.unex.prototipoasee.model;


import androidx.annotation.NonNull;
import androidx.room.Entity;

// Clase valoración
@Entity(tableName = "Ratings",primaryKeys = {"filmID","username"})
public class Rating {

    // Atributos clase valoración
    private int filmID;
    @NonNull
    private String username;
    private int rating;

    public Rating(int filmID, String username, int rating){
        this.filmID = filmID;
        this.username = username;
        this.rating = rating;
    }

    // Métodos SET and GET de valoración
    public int getFilmID() {
        return filmID;
    }

    public void setFilmID(int filmID) {
        this.filmID = filmID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}

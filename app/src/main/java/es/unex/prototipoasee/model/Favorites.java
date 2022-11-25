package es.unex.prototipoasee.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;

// Clase Favorites
@Entity(tableName = "Favorites", primaryKeys = {"filmID","username"})
public class Favorites {

    // Atributos Favorites
    @NonNull
    private int filmID;
    @NonNull
    private String username;

    public Favorites(int filmID, String username) {
        this.filmID = filmID;
        this.username = username;
    }

    // Métodos SET and GET de la clase Favorites
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

}

package es.unex.prototipoasee.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;

// Clase pendientes
@Entity(tableName = "Pendings",primaryKeys = {"filmID","username"})
public class Pendings {

    public Pendings(){

    }

    public Pendings(int filmID, String username) {
        this.filmID = filmID;
        this.username = username;
    }

    // Atrbutos pendientes
    @NonNull
    private int filmID;
    @NonNull
    private String username;

    // Métodos SET and GET de la clase pendientes
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

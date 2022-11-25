package es.unex.prototipoasee.model;


import androidx.room.Entity;

@Entity(tableName = "FilmsGenresList",primaryKeys = {"filmID","genreID"})
public class FilmsGenresList {

    private int filmID;
    private int genreID;

    public int getFilmID() {
        return filmID;
    }

    public void setFilmID(int filmID) {
        this.filmID = filmID;
    }

    public int getGenreID() {
        return genreID;
    }

    public void setGenreID(int genreID) {
        this.genreID = genreID;
    }
}

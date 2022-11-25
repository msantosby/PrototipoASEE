package es.unex.prototipoasee.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import es.unex.prototipoasee.model.Genre;

@Dao
public interface GenreDAO {

    // Insertar el genero de una pelicula
    @Insert
    void insertGenre(Genre genre);

    @Insert
    void insertAllGenres(List<Genre> list);

    // Elimina el genero de una pelicula
    @Delete
    void deleteGenre(Genre genre);

    // Actualiza el genero de una pelicula
    @Update
    void updateGenre(Genre genre);

    // Devuelve el genero de una pelicula
    @Query("SELECT * FROM Genres WHERE genreID = (:genreid)")
    Genre getGenre (int genreid);

    // Devuelve todos los generos de una pelicula
    @Query("SELECT * FROM Genres")
    List<Genre> getAllGenres();
}

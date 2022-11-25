package es.unex.prototipoasee.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import es.unex.prototipoasee.model.FilmsGenresList;

@Dao
public interface FilmsGenresListDAO {

    @Query("INSERT INTO FilmsGenresList VALUES ((:filmid), (:genreid))")
    void insertFilmGenre(int filmid, int genreid);

    @Query("SELECT name FROM FilmsGenresList fg JOIN Genres f ON (fg.genreID=f.genreID) WHERE filmID = (:filmid)")
    List<String> getAllFilmsGenresNames(int filmid);

    @Query("SELECT * FROM FilmsGenresList")
    List<FilmsGenresList> getAllFilmsGenres();
}

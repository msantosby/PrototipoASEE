package es.unex.prototipoasee.room;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import es.unex.prototipoasee.model.Films;

@Dao
public interface FilmDAO {
    @Insert
    void insertFilm(Films film);

    @Insert
    void insertAllFilms(List<Films> filmsList);

    @Delete
    void deleteFilm(Films film);

    @Update
    void updateFilm(Films film);

    @Query("SELECT * FROM Films WHERE filmID = (:filmid)")
    Films getFilm(int filmid);

    @Query("SELECT * FROM Films")
    List<Films> getAllFilmsDetails();

    @Query("SELECT * FROM Films WHERE filmID IN (:filmids)")
    List<Films> getFilmsByID(List<Integer> filmids);

    @Query("SELECT * FROM Films")
    List<Films> getAllFilms();

    @Query("SELECT (totalRatingMovieCheck/totalVotesMovieCheck) FROM Films WHERE filmID = (:filmid)  ")
    Double getAverageRatingFromMovieCheck(int filmid);

    @Query("UPDATE Films SET totalRatingMovieCheck = totalRatingMovieCheck + (:rating) AND totalVotesMovieCheck = totalVotesMovieCheck + 1")
    void addRating(int rating);

    @Query("UPDATE Films SET poster = (:url)+poster WHERE filmID = (:filmid)")
    void updatePosterPath(String url, int filmid);

    @Query("SELECT f.filmID, f.poster, f.overview, f.releaseDate, f.originalTitle, f.title, f.totalRatingMovieCheck, f.totalVotesMovieCheck, f.voteAverage FROM films f JOIN Pendings p ON f.filmID = p.filmID WHERE p.username = (:username)")
    List<Films> getPendingsFilms(String username);

    @Query("SELECT f.filmID, f.poster, f.overview, f.releaseDate, f.originalTitle, f.title, f.totalRatingMovieCheck, f.totalVotesMovieCheck, f.voteAverage FROM films f JOIN Favorites fav ON f.filmID = fav.filmID WHERE fav.username = (:username)")
    List<Films> getFavoritesFilms(String username);

}

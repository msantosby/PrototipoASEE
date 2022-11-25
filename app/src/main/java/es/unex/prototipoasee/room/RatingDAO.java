package es.unex.prototipoasee.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import es.unex.prototipoasee.model.Rating;

@Dao
public interface RatingDAO {

    // Inserta una valoración de un usuario sobre una película
    @Insert
    void insertRating(Rating rating);

    // Elimina una valoración de un usuario sobre una película
    @Delete
    void deleteRating(Rating rating);

    // Actualiza una valoración de un usuario sobre una película
    @Query("UPDATE Ratings SET rating = (:rating) WHERE filmID = (:filmid) AND username = (:username)")
    void updateRating(int rating, int filmid, String username);

    // Devuelve una valoración de un usuario sobre una película
    @Query("SELECT * FROM Ratings WHERE filmID = (:filmid) AND username = (:username)")
    Rating getRating(int filmid, String username);

    // Devuelve true si un usuario ha valorado una película
    @Query("SELECT CASE WHEN EXISTS (SELECT filmid, username FROM Ratings WHERE filmid = (:filmid) AND username = (:username)) THEN 'true' ELSE 'false' END")
    boolean checkIfExistsRating(int filmid, String username);

    @Query("SELECT filmID FROM Ratings WHERE username = (:username)")
    List<Integer> getRatingIDs(String username);

}

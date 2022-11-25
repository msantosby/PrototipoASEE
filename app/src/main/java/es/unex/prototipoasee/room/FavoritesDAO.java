package es.unex.prototipoasee.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import es.unex.prototipoasee.model.Favorites;

@Dao
public interface FavoritesDAO {

    @Insert
    void insertFavorites(Favorites favorites);

    @Delete
    void deleteFavorites(Favorites favorites);

    @Update
    void updateFavorites(Favorites favorites);

    @Query("SELECT * FROM Favorites WHERE filmID = (:filmid) AND username = (:username)")
    Favorites getExactFavorite(int filmid, String username);

    @Query("SELECT filmID FROM Favorites WHERE username = (:username)")
    List<Integer> getUserFavorites(String username);

    @Query("SELECT filmID FROM Pendings WHERE username = (:username)")
    List<Integer> getFilmsIDFavorites(String username);
}

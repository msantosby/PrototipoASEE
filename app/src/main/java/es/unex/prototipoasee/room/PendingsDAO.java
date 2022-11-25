package es.unex.prototipoasee.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import es.unex.prototipoasee.model.Pendings;

@Dao
public interface PendingsDAO {

    @Insert
    void insertPendings(Pendings pendings);

    @Delete
    void deletePendings(Pendings pendings);

    @Update
    void updatePendings(Pendings pendings);

    @Query("SELECT * FROM Pendings WHERE filmID = (:filmid) AND username = (:username)")
    Pendings getExactPending(int filmid, String username);

    @Query("SELECT filmID FROM Pendings WHERE username = (:username)")
    List<Integer> getUserPendings(String username);

    @Query("SELECT filmID FROM Pendings WHERE username = (:username)")
    List<Integer> getFilmsIDPendings(String username);
}

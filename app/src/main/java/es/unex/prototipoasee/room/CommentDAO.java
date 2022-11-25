package es.unex.prototipoasee.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import es.unex.prototipoasee.model.Comment;
import es.unex.prototipoasee.model.Comments;

@Dao
public interface CommentDAO {

    @Insert
    void insertComment(Comments comment);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllComment(List<Comments> commentsList);

    @Delete
    void deleteComment(Comments comment);

    @Query("DELETE FROM Comments WHERE commentID = (:commentID)")
    void deleteCommentByID(int commentID);

    @Query("DELETE FROM Comments WHERE filmID = (:filmID)")
    void deleteCommentsByFilmID(int filmID);

    @Query("DELETE FROM Comments WHERE filmID = (:filmID) AND username = (:username)")
    void deleteCommentsUserFilm(int filmID, String username);

    @Query("UPDATE Comments SET username = (:username), text = (:text) WHERE commentID = (:commentid)")
    void updateComment(String username, String text, int commentid);

    @Query("SELECT * FROM Comments WHERE commentID = (:commentid)")
    Comments getComment(int commentid);

    @Query("SELECT * FROM Comments WHERE filmID = (:filmID)")
    List<Comments> getFilmComments(int filmID);

}

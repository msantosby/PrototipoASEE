package es.unex.prototipoasee.interfaces;

import java.util.List;

import es.unex.prototipoasee.model.Comments;
import es.unex.prototipoasee.model.Films;

// Interfaz para comunicar este Fragment con su actividad (ItemDetailActivity)
public interface ItemDetailInterface{
    Films getFilmSelected();
    List<Comments> getCommentList();
}

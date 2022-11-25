package es.unex.prototipoasee.interfaces;

import java.util.List;

import es.unex.prototipoasee.model.Films;

public interface UserDataInterface {
    List<Films> getUserFavoriteFilms();
    List<Films> getUserPendingsFilms();
    List<Integer> getUserRatedFilms();
}

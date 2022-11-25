package es.unex.prototipoasee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.unex.prototipoasee.interfaces.UserDataInterface;
import es.unex.prototipoasee.model.Films;

public class UserFilmsData {
    public HashMap<Integer, Films> userPendingFilms = new HashMap<>();
    public HashMap<Integer, Films> userFavoriteFilms = new HashMap<>();
    public Set<Integer> userRatedFilms = new HashSet<>();
    private static UserFilmsData instance = null;

    public static UserFilmsData getInstance() {
        if (instance==null) {
            instance = new UserFilmsData();
        }
        return instance;
    }
}

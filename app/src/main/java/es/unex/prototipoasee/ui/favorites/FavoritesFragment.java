package es.unex.prototipoasee.ui.favorites;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import es.unex.prototipoasee.AppExecutors;
import es.unex.prototipoasee.UserFilmsData;
import es.unex.prototipoasee.adapters.FilmAdapter;
import es.unex.prototipoasee.R;
import es.unex.prototipoasee.adapters.FilmListAdapter;
import es.unex.prototipoasee.interfaces.ItemDetailInterface;
import es.unex.prototipoasee.interfaces.UserDataInterface;
import es.unex.prototipoasee.model.Films;
import es.unex.prototipoasee.room.FilmsDatabase;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private FilmListAdapter filmListAdapter;
    private List<Integer>  filmsID = new ArrayList<>();

    private SharedPreferences preferences;

    private UserDataInterface userDataInterface;

    //List<Films> userFavoriteFilms;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        UserFilmsData userFilmsData = UserFilmsData.getInstance();
        List<Films> userFavoriteFilms  = new ArrayList<Films>(userFilmsData.userFavoriteFilms.values());

        View v = inflater.inflate(R.layout.fragment_favorites, container, false);
        filmListAdapter = new FilmListAdapter(userFavoriteFilms,R.layout.favorites_item_list_content, getContext());

        View recyclerView = v.findViewById(R.id.fragment_favorites);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        return v;
    }

    // Se asigna a la lista los datos del dummy
    public void setupRecyclerView(@NonNull RecyclerView recyclerView){
        recyclerView.setAdapter(filmListAdapter);
    }

    @Override
    public void onResume() {
        UserFilmsData userFilmsData = UserFilmsData.getInstance();
        List<Films> userFavoriteFilms  = new ArrayList<Films>(userFilmsData.userFavoriteFilms.values());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                filmListAdapter.swap(userFavoriteFilms);
            }
        });
        super.onResume();
    }

//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        try {
//            userDataInterface = (UserDataInterface) context;
//        } catch (ClassCastException e){
//            throw new ClassCastException(context.toString() + " must implement UserDataInterface");
//        }
//    }
}
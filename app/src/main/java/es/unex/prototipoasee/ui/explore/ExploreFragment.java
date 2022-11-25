package es.unex.prototipoasee.ui.explore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.unex.prototipoasee.AppExecutors;
import es.unex.prototipoasee.R;
import es.unex.prototipoasee.adapters.FilmAdapter;
import es.unex.prototipoasee.model.Films;
import es.unex.prototipoasee.room.FilmsDatabase;

public class ExploreFragment extends Fragment {

    private List<Films> filmList = new ArrayList<>();

    private FilmAdapter filmAdapter;

    public ExploreFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        filmAdapter = new FilmAdapter(filmList, R.layout.explore_item_grid_content, getContext());
        loadExploreFilms();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_explore);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(filmAdapter);

        return view;
    }

    private void loadExploreFilms() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                FilmsDatabase db = FilmsDatabase.getInstance(getContext());
                filmList = db.filmDAO().getAllFilms();
            }
        });
    }

    @Override
    public void onResume() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                filmAdapter.swap(filmList);
            }
        });
        super.onResume();
    }
}
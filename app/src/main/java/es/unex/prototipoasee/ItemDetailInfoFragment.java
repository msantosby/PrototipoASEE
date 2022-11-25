package es.unex.prototipoasee;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import es.unex.prototipoasee.interfaces.ItemDetailInterface;
import es.unex.prototipoasee.interfaces.UserDataInterface;
import es.unex.prototipoasee.model.Favorites;
import es.unex.prototipoasee.model.Films;
import es.unex.prototipoasee.model.Pendings;
import es.unex.prototipoasee.room.FilmsDatabase;

public class ItemDetailInfoFragment extends Fragment {

    // Referencias a vistas de la UI
    private ImageView ivMoviePosterDetail;
    private TextView tvMovieTitleDetail;
    private TextView tvReleaseDateValueDetail;
    private TextView tvRatingAPIDetail;
    private TextView tvRatingValueDetail;
    private TextView tvMovieGenresValue;
    private TextView tvSynopsisValueDetail;
    private Button bToggleFavoriteDetail;
    private Button bTogglePendingDetail;

    private List<String> listGenres = new ArrayList<>();
    private ItemDetailInterface itemDetailInterface;
    private UserDataInterface userDataInterface;

    List<Films> userFavoritesFilms;
    Films film;

    SharedPreferences loginPreferences;


    // Objeto para la sincronización de hilos
    public static final Object lock = new Object();

    public ItemDetailInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_detail_info, container, false);
        loginPreferences = getActivity().getSharedPreferences(getActivity().getPackageName()+"_preferences", Context.MODE_PRIVATE);
        getViewsReferences(view);

        film = itemDetailInterface.getFilmSelected();

        updateUI();

        bToggleFavoriteDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filmInFavorites()){
                    removeFilmFromFavorites();
                    setFavButtonAdd();
                    Toast.makeText(getActivity(), R.string.toggle_favorites_remove, Toast.LENGTH_SHORT).show();
                } else {
                    addFilmToFavorites();
                    setFavButtonRemove();
                    Toast.makeText(getActivity(), R.string.toggle_favorites_add, Toast.LENGTH_SHORT).show();
                }
            }
        });

        bTogglePendingDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filmInPendings()){
                    removeFilmFromPendings();
                    setPendingButtonAdd();
                    Toast.makeText(getActivity(), R.string.toggle_pending_remove, Toast.LENGTH_SHORT).show();
                } else {
                    addFilmToPendings();
                    setPendingButtonRemove();
                    Toast.makeText(getActivity(), R.string.toggle_pending_add, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void getViewsReferences(View view) {
        ivMoviePosterDetail = view.findViewById(R.id.ivMoviePosterDetail);
        tvMovieTitleDetail = view.findViewById(R.id.tvMovieTitleDetail);
        tvReleaseDateValueDetail = view.findViewById(R.id.tvReleaseDateValueDetail);
        tvRatingAPIDetail = view.findViewById(R.id.tvRatingAPIDetail);
        tvRatingValueDetail = view.findViewById(R.id.tvRatingValueDetail);
        tvMovieGenresValue = view.findViewById(R.id.tvMovieGenresValue);
        tvSynopsisValueDetail = view.findViewById(R.id.tvSynopsisValueDetail);
        bToggleFavoriteDetail = view.findViewById(R.id.bToggleFavoriteDetail);
        bTogglePendingDetail = view.findViewById(R.id.bTogglePendingDetail);
    }

    private void updateUI(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(filmInFavorites()){
                    setFavButtonRemove();
                } else {
                    setFavButtonAdd();
                }
                if(filmInPendings()){
                    setPendingButtonRemove();
                } else {
                    setPendingButtonAdd();
                }
                Glide.with(getContext()).load("https://image.tmdb.org/t/p/original/"+film.getPosterPath()).into(ivMoviePosterDetail);
                tvMovieTitleDetail.setText(film.getTitle());
                tvReleaseDateValueDetail.setText(film.getReleaseDate());
                tvRatingAPIDetail.setText(String.valueOf(film.getVoteAverage()));
                tvSynopsisValueDetail.setText(film.getOverview());
                if(film.getTotalVotesMovieCheck()!=0){
                    tvRatingValueDetail.setText(String.valueOf(film.getTotalRatingMovieCheck()/film.getTotalVotesMovieCheck()));
                }else{
                    tvRatingValueDetail.setText(String.valueOf(0));
                }
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (lock){
                            FilmsDatabase db = FilmsDatabase.getInstance(getContext());
                            listGenres = db.filmsGenresListDAO().getAllFilmsGenresNames(film.getId());
                            lock.notify();
                        }
                    }
                });
                synchronized (lock){
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String genresAsString = String.join(" - ", listGenres);
                    tvMovieGenresValue.setText(genresAsString);
                }
            }
        });
    }

    private void setFavButtonAdd(){
        bToggleFavoriteDetail.setText(R.string.detail_add_favorites);
        bToggleFavoriteDetail.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red_widgets));
    }

    private void setFavButtonRemove() {
        bToggleFavoriteDetail.setText(R.string.remove_favorite);
        bToggleFavoriteDetail.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red_widgets_darker));
    }

    private boolean filmInFavorites(){
        UserFilmsData userFilmsData = UserFilmsData.getInstance();
        return userFilmsData.userFavoriteFilms.get(film.getId()) != null;
    }

    private void addFilmToFavorites() {
        UserFilmsData userFilmsData = UserFilmsData.getInstance();
        userFilmsData.userFavoriteFilms.put(film.getId(), film);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                FilmsDatabase db = FilmsDatabase.getInstance(getActivity());
                db.favoritesDAO().insertFavorites(new Favorites(film.getId(), loginPreferences.getString("USERNAME", "")));
            }
        });
    }

    private void removeFilmFromFavorites() {
        UserFilmsData userFilmsData = UserFilmsData.getInstance();
        userFilmsData.userFavoriteFilms.remove(film.getId());
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                FilmsDatabase db = FilmsDatabase.getInstance(getActivity());
                db.favoritesDAO().deleteFavorites(new Favorites(film.getId(), loginPreferences.getString("USERNAME", "")));
            }
        });
    }

    private boolean filmInPendings(){
        UserFilmsData userFilmsData = UserFilmsData.getInstance();
        return userFilmsData.userPendingFilms.get(film.getId()) != null;
    }

    private void addFilmToPendings() {
        UserFilmsData userFilmsData = UserFilmsData.getInstance();
        userFilmsData.userPendingFilms.put(film.getId(), film);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                FilmsDatabase db = FilmsDatabase.getInstance(getActivity());
                db.pendingsDAO().insertPendings(new Pendings(film.getId(), loginPreferences.getString("USERNAME", "")));
            }
        });
    }

    private void removeFilmFromPendings() {
        UserFilmsData userFilmsData = UserFilmsData.getInstance();
        userFilmsData.userPendingFilms.remove(film.getId());
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                FilmsDatabase db = FilmsDatabase.getInstance(getActivity());
                db.pendingsDAO().deletePendings(new Pendings(film.getId(), loginPreferences.getString("USERNAME", "")));
            }
        });
    }

    private void setPendingButtonRemove() {
        bTogglePendingDetail.setText(R.string.remove_pending);
        bTogglePendingDetail.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red_widgets_darker));
    }

    private void setPendingButtonAdd() {
        bTogglePendingDetail.setText(R.string.detail_add_pendant);
        bTogglePendingDetail.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red_widgets));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            itemDetailInterface = (ItemDetailInterface) context;
            //userDataInterface = (UserDataInterface)
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement ItemDetailInterface and UserDataInterface");
        }
    }
}
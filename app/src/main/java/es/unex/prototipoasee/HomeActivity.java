package es.unex.prototipoasee;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import es.unex.prototipoasee.API.FilmAPI;
import es.unex.prototipoasee.adapters.FilmAdapter;
import es.unex.prototipoasee.adapters.FilmListAdapter;
import es.unex.prototipoasee.interfaces.UserDataInterface;
import es.unex.prototipoasee.model.Favorites;
import es.unex.prototipoasee.model.Film;
import es.unex.prototipoasee.model.Films;
import es.unex.prototipoasee.model.FilmsGenresList;
import es.unex.prototipoasee.model.FilmsPages;
import es.unex.prototipoasee.model.Genre;
import es.unex.prototipoasee.model.GenresList;
import es.unex.prototipoasee.databinding.ActivityMainBinding;
import es.unex.prototipoasee.model.Pendings;
import es.unex.prototipoasee.model.Rating;
import es.unex.prototipoasee.model.User;
import es.unex.prototipoasee.room.FilmsDatabase;
import es.unex.prototipoasee.ui.profile.ProfileFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity implements ProfileFragment.ProfileListener, FilmAdapter.FilmListener, FilmListAdapter.ActionButtonListener {

    private ActivityMainBinding binding;

    // Campos necesarios para trabajar con la API
    private static final String URLBASE = "https://api.themoviedb.org";
    private static final String APIKEY = "a99f635c038e47e61a70f3ab2b526e3e";
    private static final String LANGUAGE = "es-ES";
    private static final String TAG = "APP";

    private SharedPreferences loginPreferences;

    //FilmsDatabase db = FilmsDatabase.getInstance(HomeActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FilmsDatabase db = FilmsDatabase.getInstance(HomeActivity.this);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<Films> fList = db.filmDAO().getAllFilms();
                if (fList.size() == 0){
                    getData();
                }
            }
        });

        loginPreferences = getSharedPreferences(getPackageName()+"_preferences", Context.MODE_PRIVATE);

        obtainUserData();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView navView = findViewById(R.id.nav_view);

        //Passing each menu ID as a set of Ids because each
        //menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_explore, R.id.navigation_favorites, R.id.navigation_pendings, R.id.navigation_profile)
                .build();

        // Se centra la etiqueta (título) de la App Bar para cada pantalla
        centerAppBarTitle();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    public void obtainUserData(){
        UserFilmsData userFilmsData = UserFilmsData.getInstance();
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                FilmsDatabase db = FilmsDatabase.getInstance(HomeActivity.this);
                for (Films film : db.filmDAO().getFavoritesFilms(loginPreferences.getString("USERNAME", ""))) {
                    userFilmsData.userFavoriteFilms.put(film.getId(), film);
                }
                for (Films film : db.filmDAO().getPendingsFilms(loginPreferences.getString("USERNAME", ""))) {
                    userFilmsData.userPendingFilms.put(film.getId(), film);
                }
                userFilmsData.userRatedFilms.addAll(db.ratingDAO().getRatingIDs(loginPreferences.getString("USERNAME", "")));
            }
        });
    }

    public void getData(){
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                getFilms();
                getGenres();
                getFilmsGenres();
            }
        });
    }

    public void getFilms(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLBASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FilmAPI api = retrofit.create(FilmAPI.class);
        Call<FilmsPages> call = api.getFilms(APIKEY,LANGUAGE);

        call.enqueue(new Callback<FilmsPages>() {
            @Override
            public void onResponse(Call<FilmsPages> call, Response<FilmsPages> response) {
                if(!response.isSuccessful()){
                    Log.i(TAG,"CODE: "+response.code());
                }

                assert response.body() != null;
                List<Films> fLists = response.body().getResults();
                putFilmsOnDatabase(fLists);
            }

            @Override
            public void onFailure(Call<FilmsPages> call, Throwable t) {
                Log.i(TAG,"ERROR: LA APLICACIÓN FALLÓ AL REALIZAR LA CONSULTA");
                t.printStackTrace();
            }
        });
    }

    public void getGenres(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLBASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FilmAPI api = retrofit.create(FilmAPI.class);
        Call<GenresList> call = api.getGenres(APIKEY,LANGUAGE);

        call.enqueue(new Callback<GenresList>() {
            @Override
            public void onResponse(Call<GenresList> call, Response<GenresList> response) {
                if(!response.isSuccessful()){
                    Log.i(TAG,"CODE: "+response.code());
                }

                assert response.body() != null;
                List<Genre> gLists = response.body().getGenres();
                putGenresOnDatabase(gLists);
            }

            @Override
            public void onFailure(Call<GenresList> call, Throwable t) {

            }
        });
    }

    public void getFilmsGenres(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLBASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FilmAPI api = retrofit.create(FilmAPI.class);
        Call<FilmsPages> call = api.getFilms(APIKEY,LANGUAGE);

        call.enqueue(new Callback<FilmsPages>() {
            @Override
            public void onResponse(Call<FilmsPages> call, Response<FilmsPages> response) {
                if(!response.isSuccessful()){
                    Log.i(TAG,"CODE: "+response.code());
                }

                assert response.body() != null;
                List<Films> fLists = response.body().getResults();
                putFilmsGenresListOnDatabase(fLists);
            }

            @Override
            public void onFailure(Call<FilmsPages> call, Throwable t) {
                Log.i(TAG,"ERROR: LA APLICACIÓN FALLÓ AL REALIZAR LA CONSULTA");
                t.printStackTrace();
            }
        });
    }

    public void putFilmsOnDatabase(List<Films> list) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                FilmsDatabase db = FilmsDatabase.getInstance(HomeActivity.this);
                db.filmDAO().insertAllFilms(list);
            }
        });
    }

    public void putGenresOnDatabase(List<Genre> list) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                FilmsDatabase db = FilmsDatabase.getInstance(HomeActivity.this);
                db.genreDAO().insertAllGenres(list);
            }
        });
    }

    public void putFilmsGenresListOnDatabase(List<Films> list) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                FilmsDatabase db = FilmsDatabase.getInstance(HomeActivity.this);
                for(Films films: list) {
                    List<Integer> ids = films.getGenreIds();
                    for (Integer id : ids) {
                        db.filmsGenresListDAO().insertFilmGenre(films.getId(), id);
                    }
                }
            }
        });
    }

    /**
     * Centra el título de la AppBar de cada una de las secciones principales de la App.
     */
    private void centerAppBarTitle() {
        ArrayList<View> textViews = new ArrayList<>();

        getWindow().getDecorView().findViewsWithText(textViews, getTitle(), View.FIND_VIEWS_WITH_TEXT);

        if(textViews.size() > 0) {
            AppCompatTextView appCompatTextView = null;
            if(textViews.size() == 1) {
                appCompatTextView = (AppCompatTextView) textViews.get(0);
            } else {
                for(View v : textViews) {
                    if(v.getParent() instanceof Toolbar) {
                        appCompatTextView = (AppCompatTextView) v;
                        break;
                    }
                }
            }

            if(appCompatTextView != null) {
                ViewGroup.LayoutParams params = appCompatTextView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                appCompatTextView.setLayoutParams(params);
                appCompatTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                appCompatTextView.setTextColor(ContextCompat.getColor(this, R.color.white));
            }
        }
    }

    /**
     * Implementación del método onInfoButtonPressed() de la interfaz ProfileListener definido para controlar el comportamiento del botón
     * de 'Información de la App' localizado en ProfileFragment.
     * Se lanza la actividad AppInfoActivity para mostrar información de interés de la App.
     */
    @Override
    public void onInfoButtonPressed() {
        Intent intent = new Intent(this, AppInfoActivity.class);
        startActivity(intent);
    }

    /**
     * Implementación del método onLogoutButtonPressed() de la interfaz ProfileListener definido para controlar el comportamiento del botón
     * para cerrar sesión localizado en ProfileFragment.
     * Se lanza la actividad LoginActivity para volver a iniciar sesión y se finaliza la actividad actual para 'limpiar' la pila de Back.
     */
    @Override
    public void onLogoutButtonPressed() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Implementación del método onDeleteAccountButtonPressed() de la interfaz ProfileListener definido para controlar el comportamiento del botón
     * para eliminar la cuenta de usuario en ProfileFragment.
     * Se lanza la actividad DeleteAccountActivity a la espera de si el usuario confirma la eliminación de la cuenta o no.
     * La respuesta es recogida en deleteAccountLauncher.
     */
    @Override
    public void onDeleteAccountButtonPressed() {
        Intent intent = new Intent(this, DeleteAccountActivity.class);
        deleteAccountLauncher.launch(intent);
    }

    /**
     * Recupera la respuesta devuelta por DeleteAccountActivity. Si el usuario no ha eliminado la cuenta, no se hace nada. En caso contrario
     * (RESULT_OK), se inicia la actividad de LoginActivity para acceder con una nueva cuenta y se finaliza la actividad actual para vaciar
     * la pila de Back.
     */
    ActivityResultLauncher<Intent> deleteAccountLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });

    /**
     *
     * @param film Objeto Films que contiene la información de la película de la que se quiere mostrar la información
     */
    @Override
    public void onFilmSelected(Films film){
        Intent intent = new Intent(this, ItemDetailActivity.class);
        intent.putExtra("FILM", film);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        FilmsDatabase.getInstance(this).close();
        super.onDestroy();
    }

    @Override
    public void onFavButtonPressed(Films film, FilmListAdapter filmListAdapter) {
        UserFilmsData userFilmsData = UserFilmsData.getInstance();
        userFilmsData.userFavoriteFilms.remove(film.getId());
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                FilmsDatabase db = FilmsDatabase.getInstance(HomeActivity.this);
                db.favoritesDAO().deleteFavorites(new Favorites(film.getId(), loginPreferences.getString("USERNAME", "")));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        filmListAdapter.swap(new ArrayList<Films>(userFilmsData.userFavoriteFilms.values()));
                    }
                });
            }
        });
    }

    @Override
    public void onPendingButtonPressed(Films film, FilmListAdapter filmListAdapter) {
        UserFilmsData userFilmsData = UserFilmsData.getInstance();
        userFilmsData.userPendingFilms.remove(film.getId());
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                FilmsDatabase db = FilmsDatabase.getInstance(HomeActivity.this);
                db.pendingsDAO().deletePendings(new Pendings(film.getId(), loginPreferences.getString("USERNAME", "")));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        filmListAdapter.swap(new ArrayList<Films>(userFilmsData.userPendingFilms.values()));
                    }
                });
            }
        });
    }
}
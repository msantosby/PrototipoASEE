package es.unex.prototipoasee;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import es.unex.prototipoasee.adapters.CommentAdapter;
import es.unex.prototipoasee.adapters.TabsViewPagerAdapter;
import es.unex.prototipoasee.interfaces.ItemDetailInterface;
import es.unex.prototipoasee.model.Comment;
import es.unex.prototipoasee.model.Comments;
import es.unex.prototipoasee.model.Films;
import es.unex.prototipoasee.model.Rating;
import es.unex.prototipoasee.room.FilmsDatabase;

public class ItemDetailActivity extends AppCompatActivity implements ItemDetailInterface, CommentAdapter.DeleteCommentInterface {

    TabLayout tabLayout;
    ViewPager2 viewPager2;

    List<Comments> commentList = new ArrayList<>();

    Films film;

    SharedPreferences loginPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        setTitle(R.string.detail_title);

        tabLayout = findViewById(R.id.tlDetail);
        viewPager2 = findViewById(R.id.vpDetail);

        loginPreferences = getSharedPreferences(getPackageName()+"_preferences", Context.MODE_PRIVATE);

        viewPager2.setAdapter(new TabsViewPagerAdapter(this.getSupportFragmentManager(), getLifecycle()));
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Se obtiene la película de la que se quiere mostrar información
        film = (Films) getIntent().getSerializableExtra("FILM");

        obtainFilmData();
    }

    private void obtainFilmData() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                FilmsDatabase db = FilmsDatabase.getInstance(ItemDetailActivity.this);
                commentList = db.commentDAO().getFilmComments(film.getId());
                film = db.filmDAO().getFilm(film.getId());
            }
        });
    }

    @Override
    public Films getFilmSelected() {
        return film;
    }


    @Override
    public void deleteComment(Comments comment, CommentAdapter commentAdapter, List<Comments> commentsList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                commentsList.remove(comment);
                commentAdapter.swap(commentsList);
            }
        });
        Toast.makeText(this, R.string.delete_comment, Toast.LENGTH_SHORT).show();
    }

    @Override
    public List<Comments> getCommentList() {
        return commentList;
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                FilmsDatabase db = FilmsDatabase.getInstance(ItemDetailActivity.this);
                db.commentDAO().deleteCommentsUserFilm(film.getId(),loginPreferences.getString("USERNAME", ""));
                db.commentDAO().insertAllComment(commentList);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
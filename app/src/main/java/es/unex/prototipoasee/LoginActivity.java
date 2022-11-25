package es.unex.prototipoasee;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.List;

import es.unex.prototipoasee.model.User;
import es.unex.prototipoasee.room.FilmsDatabase;

public class LoginActivity extends AppCompatActivity {

    // Referencias a vistas de la UI
    private EditText etUserLogin;
    private EditText etPasswordLogin;
    private TextView tvRegisterLogin;
    private Button bLogin;

    // Objeto de preferencias para almacenar el nombre de usuario y permitir un inicio de sesión automático
    private SharedPreferences loginPreferences;

    // Mapas para guardar los usuarios almacenados en la BD para el chequeo de credenciales
    private HashMap<String, User> usersInDBByUsername;
    private HashMap<String, User> usersInDBByEmail;

    // Objeto para la sincronización de hilos
    public static final Object lock = new Object();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UserFilmsData userFilmsData = UserFilmsData.getInstance();
        userFilmsData.userPendingFilms.clear();
        userFilmsData.userFavoriteFilms.clear();
        userFilmsData.userRatedFilms.clear();

        // Se accede a las preferencias almacenadas en la App
        loginPreferences = getSharedPreferences(getPackageName()+"_preferences", Context.MODE_PRIVATE);

        // Se intenta iniciar sesión con las preferencias recuperadas
        autoLogin();

        // Si no se ha podido iniciar sesión automáticamente, se obtienen todos los usuarios almacenados en la BD y se vuelcan en dos mapas para la consulta de credenciales.
        usersInDBByUsername = new HashMap<>();
        usersInDBByEmail = new HashMap<>();
        getAllUsers();

        getViewsReferences();

        // Se presiona sobre la cadena de Registro
        tvRegisterLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Se inicia la actividad de RegisterActivity esperando que devuelva como resultado el nombre de usuario que se haya registrado
                startRegisterActivityForResult();
            }
        });

        // Se hace click al botón de Inicio de sesión
        bLogin.setOnClickListener(this::logIn);
    }

    /**
     * Obtiene todos los usuarios almacenados en la BD.
     */
    public void getAllUsers() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                FilmsDatabase db = FilmsDatabase.getInstance(LoginActivity.this);
                List<User> users = db.userDAO().getAllUsers();
                for (User user: users) {
                    usersInDBByUsername.put(user.getUsername(), user);
                    usersInDBByEmail.put(user.getEmail(), user);
                }
            }
        });
    }

    /**
     * Comprueba si existe un valor para la preferencia que almacena el usuario al que corresponde la sesión.
     * En caso afirmativo, se inicia la actividad HomeActivity y se mantiene la sesión del usuario referenciado.
     */
    public void autoLogin(){
        if(!loginPreferences.getString("USERNAME", "").equals("")){
            // El usuario ya se ha loggeado o registrado anteriormente en el dispositivo y aún no ha cerrado sesión
            // Inicio de sesión automático, es decir, el usuario no introduce sus credenciales y se usa el valor de la preferencia como referencia al usuario loggeado
            Toast.makeText(this, getString(R.string.auto_login) + " " + loginPreferences.getString("USERNAME", ""), Toast.LENGTH_SHORT).show();
            startHomeActivity();
        }
    }

    /**
     * Método para obtener las referencias a cada una de las vistas que forman parte de la UI de la actividad.
     */
    private void getViewsReferences() {
        etUserLogin = findViewById(R.id.etUserLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        tvRegisterLogin = findViewById(R.id.tvRegisterLogin);
        bLogin = findViewById(R.id.bLogin);
    }

    /**
     * Inicia la actividad HomeActivity y finaliza la actividad actual para eliminarla de la pila de Back, de forma que no se vuelva al login
     * si el usuario presiona el botón Back desde la actividad HomeActivity.
     */
    private void startHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Inicia la actividad RegisterActivity a la espera de que esta devuelva como resultado el nombre de usuario que se registre.
     * La respuesta recibida es gestionada a través de registerLaucher.
     */
    private void startRegisterActivityForResult(){
        Intent intent = new Intent(this, RegisterActivity.class);
        // Se pasan los dos mapas obtenidos para la consulta de credenciales en RegisterActivity
        intent.putExtra("MAP_BY_USERNAME", usersInDBByUsername);
        intent.putExtra("MAP_BY_EMAIL", usersInDBByEmail);
        registerLauncher.launch(intent);
    }

    /**
     * Recupera la respuesta devuelta por RegisterActivity. Si el resultado ha sido satisfactorio (se ha devuelto el nombre de usuario - RESULT_OK),
     * se obtiene el valor de dicho nombre para guardarlo en la preferencia correspondiente e iniciar la actividad HomeActivity con la nueva sesión.
     * En caso contrario no se hace nada, ya que el usuario puede volver a RegisterActivity o presionar Back para salir de la App.
     */
    ActivityResultLauncher<Intent> registerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        assert result.getData() != null;
                        String username = result.getData().getExtras().get("USERNAME").toString();
                        saveUserPreferences(username);
                        Toast.makeText(LoginActivity.this, getString(R.string.auto_login) + " " + username, Toast.LENGTH_SHORT).show();
                        startHomeActivity();
                    }
                }
            });

    /**
     * Edita las preferencias compartidas para actualizar el nombre de usuario al que se asocia la sesión.
     */
    private void saveUserPreferences(String username){
        SharedPreferences.Editor editPreferences = loginPreferences.edit();
        editPreferences.putString("USERNAME", username);
        editPreferences.apply();
    }

    /**
     * Si existe la cuenta asociada a las credenciales introducidas, almacena el nombre de usuario en las preferencias e inicia la actividad
     * HomeActivity con la nueva sesión.
     */
    private void logIn(View view){
        if (accountExists()) {
            saveUserPreferences(etUserLogin.getText().toString());
            startHomeActivity();
        } else {
            Snackbar.make(view, R.string.wrong_credentials, 3500)
                    .setTextMaxLines(10).show();
        }
    }

    /**
     * Comprueba si las credenciales introducidas corresponden a algún usuario dado de alta en la App (es decir, si su información está en la
     * BD). En caso afirmativo se devueve true. En caso contrario se devuelve false. Para ello, se consultan las credenciales a través
     * de los mapas obtenidos, ahorrándose transacciones a la BD cada vez que se intenta iniciar sesión.
     */
    private boolean accountExists() {
        String username = etUserLogin.getText().toString();
        String password = etPasswordLogin.getText().toString();

        User user = usersInDBByUsername.get(username);

        return user != null && user.getPassword().equals(password);
    }

    @Override
    protected void onDestroy() {
        FilmsDatabase.getInstance(this).close();
        super.onDestroy();
    }
}
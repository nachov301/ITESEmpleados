package app.iggy.empleados;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Login extends AppCompatActivity{
    private static final String TAG = "Login";

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    public static final String SWITCH1 = "switch1";

    private String text;


    Button btnLogin;
    Button btnSignUp;
    EditText user;
    EditText password;
    Switch mSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSwitch = (Switch) findViewById(R.id.switch1);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnSignUp = (Button)findViewById(R.id.log_signup);
        user = (EditText)findViewById(R.id.user);
        password = (EditText)findViewById(R.id.password);


        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String text = sharedPreferences.getString(TEXT, "");
        Log.d(TAG, "onCreate: text is " + text);
        user.setText(text);

        Boolean switchOnOff = sharedPreferences.getBoolean(SWITCH1, false);
        mSwitch.setChecked(switchOnOff);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean switchState = mSwitch.isChecked();

                String u_name = user.getText().toString();
                String u_pwd = password.getText().toString();
                ContentResolver contentResolver = getContentResolver();
                ContentValues values = new ContentValues();

                AppProvider appProvider = new AppProvider();
                appProvider.onCreate();

                appProvider.check_login(u_name, u_pwd);
//                check_login(u_name, u_pwd);
                if(appProvider.check_login(u_name,u_pwd)){
                    
                    if (switchState){

                        saveData();

                    }else{
                        Log.d(TAG, "onClick: switch is false");
                    }
                    
                    
                    Toast.makeText(Login.this, "Logeado exitosamente", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(Login.this, "Usuario o Password incorrecto", Toast.LENGTH_LONG).show();
                }

            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

    }

    public void saveData(){
        Log.d(TAG, "saveData: starts");
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TEXT, user.getText().toString());
        editor.putBoolean(SWITCH1, mSwitch.isChecked());

        editor.commit();
    }
}

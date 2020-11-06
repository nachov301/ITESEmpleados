package app.iggy.empleados;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SignUp extends AppCompatActivity {
    private static final String TAG = "SignUp";
    EditText user;
    EditText password;
    EditText password2;
    Button signUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        signUp = (Button) findViewById(R.id.btn_signUp);
        user = (EditText) findViewById(R.id.su_user);
        password = (EditText) findViewById(R.id.su_password);
        password2 = (EditText) findViewById(R.id.su_password2);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppProvider appProvider = new AppProvider();
                appProvider.onCreate();

                if (!(appProvider.check_user(user.getText().toString()))){

                    if (password.getText().toString().equals(password2.getText().toString())){

                        ContentResolver contentResolver = getContentResolver();
                        ContentValues values = new ContentValues();

                        values.put(UserContract.Columns.USERS_NAME, user.getText().toString());
                        values.put(UserContract.Columns.USERS_PASSWORD, password.getText().toString());
                        contentResolver.insert(UserContract.CONTENT_URI, values);
                        Toast.makeText(SignUp.this, "Usuario creado exitosamente", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SignUp.this, MainActivity.class);
                        startActivity(intent);

                    }else{
                        Toast.makeText(SignUp.this, "Las contraseñas no coincided, intente nuevamente.", Toast.LENGTH_LONG).show();
                    }

                }else{

                    Toast.makeText(SignUp.this, "Usuario ya existe, cambielo e intente nuevamente", Toast.LENGTH_LONG).show();

                }




            }
        });



    }

}

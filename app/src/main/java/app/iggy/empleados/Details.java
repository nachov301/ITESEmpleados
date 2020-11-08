package app.iggy.empleados;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Details extends AppCompatActivity {
    private static final String TAG = "Details";
    TextView name;
    TextView lastName;
    TextView so;
    TextView mail;
    TextView address;
    TextView phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle arguments = getIntent().getExtras();

        name = (TextView) findViewById(R.id.name);
        lastName = (TextView) findViewById(R.id.lastName);
        so = (TextView) findViewById(R.id.so);
        mail = (TextView) findViewById(R.id.mail);
        address = (TextView) findViewById(R.id.address);
        phoneNumber = (TextView) findViewById(R.id.phoneNumber);

        final app.iggy.empleados.Employee employee;

        if (arguments != null) {
            Log.d(TAG, "onCreateView: retrieving task Details.");

            employee = (app.iggy.empleados.Employee) arguments.getSerializable(app.iggy.empleados.Employee.class.getSimpleName());

            if (employee != null){
                Log.d(TAG, "onCreateView: task Details found, editing");
                name.setText("Nombre: " + employee.getName());
                lastName.setText("Apellido: " + employee.getDescription());
                so.setText("Orden: " + Integer.toString(employee.getSortOrder()));
                mail.setText(employee.getMail());
                address.setText(employee.getAddress());
                phoneNumber.setText(Long.toString(employee.getPhoneNumber()));
            }else{
//
            }

        }
    }

}

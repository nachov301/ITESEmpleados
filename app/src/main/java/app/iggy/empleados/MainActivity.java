package app.iggy.empleados;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity implements CursorRecyclerViewAdapter.OnTaskClickListener,
AddEditActivityFragment.OnSaveClicked, AppDialog.DialogEvents{
    private static final String TAG = "MainActivity";
    static Context MainActivityContext;


//    whether or not the activity is in two-pane mode
//    i.e. running in landscape on a tablet
    private boolean mTwoPane = false;
    
    public static final int DIALOG_ID_DELETE = 1;
    public static final int DIALOG_ID_CANCEL_EDIT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivityContext = this;
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(findViewById(R.id.employee_details_container)!=null){
//           the detail container view will be present only in the large screen layouts (res/values-land and res/values-sw600dp).
//           if this view is present, then the activity should be in two-pane mode
            mTwoPane = true;
        }



    }

    @Override
    public void onSaveClicked() {
        Log.d(TAG, "onSaveClicked: starts");
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.employee_details_container);
        if (fragment!=null){
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.menumain_addEmployee:
                employeeEditRequest(null);
                break;
            case R.id.menumain_about:
                Log.d(TAG, "onOptionsItemSelected: about");
                Intent intent = new Intent(this, Pop.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                showConfirmationDialog();
                return true; //indicates we are handling this
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEditClick(Employee employee) {
        employeeEditRequest(employee);
    }

    @Override
    public void onDetailsClick(Employee employee) {
        Intent detailIntent = new Intent(this, Details.class);
        Log.d(TAG, "onDetailsClick: here");
            detailIntent.putExtra(Employee.class.getSimpleName(), employee);
            startActivity(detailIntent);

    }

    @Override
    public void onDeleteClick(Employee employee) {
        Log.d(TAG, "onDeleteClick: starts");

        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_DELETE);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.deldiag_message, employee.getId(), employee.getName()));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.deldiag_positive_caption);

        args.putLong("EmployeeId", employee.getId());

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(),null);
    }

    private void employeeEditRequest(Employee employee){
        Log.d(TAG, "employeeEditRequest: starts");
        if (mTwoPane){
            Log.d(TAG, "employeeEditRequest: in two pane mode (tablet)");
            AddEditActivityFragment fragment = new AddEditActivityFragment();

            Bundle arguments = new Bundle();
            arguments.putSerializable(Employee.class.getSimpleName(), employee);
            fragment.setArguments(arguments);

//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.task_details_container, fragment);
//            fragmentTransaction.commit();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.employee_details_container, fragment)
                    .commit();

        }else{
            Log.d(TAG, "employeeEditRequest: in single-pane mode (phone)");
//          in single-pane mode, start the detail activity for the selected item id.
            Intent detailIntent = new Intent(this, AddEditActivity.class);
            if (employee != null){ //editing a task
                detailIntent.putExtra(Employee.class.getSimpleName(), employee);
                startActivity(detailIntent);
            }else{ //adding a new task
                startActivity(detailIntent);
            }
        }
    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onPositiveDialogResult: called");
        switch (dialogId){
            case DIALOG_ID_DELETE:
                Long taskId = args.getLong("EmployeeId");
                if (BuildConfig.DEBUG && taskId == 0) throw new AssertionError("Employee ID is zero");
                getContentResolver().delete(EmployeeContract.buildTaskUri(taskId), null, null);
                break;
            case DIALOG_ID_CANCEL_EDIT:
//              no action required
                break;
        }
        }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onNegativeDialogResult: called");
        switch (dialogId){
            case DIALOG_ID_DELETE:
//              no action required
                break;
            case DIALOG_ID_CANCEL_EDIT:
                finish();
                break;
        }
    }

    @Override
    public void onDialogCancelled(int dialogId) {
        Log.d(TAG, "onDialogCancelled: called");
    }

    private void showConfirmationDialog(){
        //            show dialogue to get confirmation to quit editing
        Log.d(TAG, "onBackPressed: starts");
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddEditActivityFragment fragment = (AddEditActivityFragment) fragmentManager.findFragmentById(R.id.employee_details_container);
//        if ((fragment==null) || fragment.canClose()){
//            super.onBackPressed();
//        }else{
//            show dialogue to get confirmation to quit editing
        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_CANCEL_EDIT);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.cancelMainDiag_message));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.cancelMainDiag_positive_caption);
        args.putInt(AppDialog.DIALOG_NEGATIVE_RID, R.string.cancelMainDiag_negative_caption);

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
//        }
    }

    @Override
    public void onBackPressed() {
        showConfirmationDialog();
    }
}

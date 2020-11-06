package app.iggy.empleados;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddEditActivityFragment extends Fragment {
    private static final String TAG = "AddEditActivityFragment";

    public enum FragmentEditMode {EDIT, ADD};
    private FragmentEditMode mMode;

    private EditText mNameTextView;
    private EditText mDescriptionTextView;
    private EditText mSortOrderTextView;
    private EditText mMailTextView;
    private EditText mAddress;
    private EditText mPhoneNumber;
    private Button mSaveButton;
    private OnSaveClicked mSaveListener = null;

    interface OnSaveClicked{
        void onSaveClicked();
    }

    public AddEditActivityFragment() {
        Log.d(TAG, "AddEditActivityFragment: constructor called");
    }

    public boolean canClose(){
        return false;
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach: starts");
        super.onAttach(context);

//        activities containing this fragment must implements it's callbacks
        Activity activity = getActivity();
        if (!(activity instanceof OnSaveClicked)){
            throw new ClassCastException(activity.getClass().getSimpleName()
                    + " must implement AddEditActivityFragment.OnSaveClicked interface");
        }
        mSaveListener = (OnSaveClicked) activity;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: starts");
        super.onDetach();
        mSaveListener = (OnSaveClicked) null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");

        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);

        mNameTextView = (EditText) view.findViewById(R.id.addedit_name);
        mDescriptionTextView = (EditText) view.findViewById(R.id.addedit_description);
        mSortOrderTextView = (EditText) view.findViewById(R.id.addedit_sortorder);
        mMailTextView = (EditText) view.findViewById(R.id.addedit_mail);
        mAddress = (EditText) view.findViewById(R.id.addedit_address);
        mPhoneNumber = (EditText) view.findViewById(R.id.addedit_phonenumber);

        mSaveButton = (Button) view.findViewById(R.id.addedit_save);


//        Bundle arguments = getActivity().getIntent().getExtras();
        Bundle arguments = getArguments();
        final app.iggy.empleados.Employee employee;

        if (arguments != null){
            Log.d(TAG, "onCreateView: retrieving task Details.");

            employee = (app.iggy.empleados.Employee) arguments.getSerializable(app.iggy.empleados.Employee.class.getSimpleName());

            if (employee != null){
                Log.d(TAG, "onCreateView: task Details found, editing");
                mNameTextView.setText(employee.getName());
                mDescriptionTextView.setText(employee.getDescription());
                mSortOrderTextView.setText(Integer.toString(employee.getSortOrder()));
                mMailTextView.setText(employee.getMail());
                mAddress.setText(employee.getAddress());
                mPhoneNumber.setText(Long.toString(employee.getPhoneNumber()));
                mMode = FragmentEditMode.EDIT;
            }else{
//                no employee, so must be adding a new employee, not editing an existing one.
                mMode = FragmentEditMode.ADD;
            }
        }else {
            employee = null;
            Log.d(TAG, "onCreateView: no arguments, adding a new record");
            mMode = FragmentEditMode.ADD;
        }

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update the database if at least one field has changed
                //there's not need to hit the database unless this had happened
                int so;
                long pn;
                if (mSortOrderTextView.length()>0){
                    so = Integer.parseInt(mSortOrderTextView.getText().toString());
                }else{
                    so = 0;
                }

                if (mPhoneNumber.length()>0){
                    pn = Long.parseLong(mPhoneNumber.getText().toString());
                }else{
                    pn = 0;
                }

                ContentResolver contentResolver = getActivity().getContentResolver();
                ContentValues values = new ContentValues();

                switch (mMode){
                    case EDIT:
                        if (!mNameTextView.getText().toString().equals(employee.getName())){
                            values.put(app.iggy.empleados.EmployeeContract.Columns.EMPLOYEES_NAME, mNameTextView.getText().toString());
                        }
                        if (!mDescriptionTextView.getText().toString().equals(employee.getDescription())){
                            values.put(app.iggy.empleados.EmployeeContract.Columns.EMPLOYEES_DESCRIPTION, mDescriptionTextView.getText().toString());
                        }
                        if (!mMailTextView.getText().toString().equals(employee.getMail())){
                            values.put(EmployeeContract.Columns.EMPLOYEES_MAIL, mDescriptionTextView.getText().toString());
                        }
                        if (!mAddress.getText().toString().equals(employee.getAddress())){
                            values.put(EmployeeContract.Columns.EMPLOYEES_ADDRESS, mDescriptionTextView.getText().toString());
                        }
                        if (pn != (employee.getPhoneNumber())){
                            values.put(EmployeeContract.Columns.EMPLOYEES_PHONENUMBER, pn);
                        }
                        if (so != employee.getSortOrder()){
                            values.put(app.iggy.empleados.EmployeeContract.Columns.EMPLOYEES_SORTORDER, so);
                        }
                        if (values.size()!=0){
                            Log.d(TAG, "onClick: updating employee information");
                            contentResolver.update(app.iggy.empleados.EmployeeContract.buildTaskUri(employee.getId()), values, null, null);
                        }
                        break;
                    case ADD:
                        if (mNameTextView.length()>0){
                            Log.d(TAG, "onClick: adding a new employee");
                            values.put(app.iggy.empleados.EmployeeContract.Columns.EMPLOYEES_NAME, mNameTextView.getText().toString());
                            values.put(app.iggy.empleados.EmployeeContract.Columns.EMPLOYEES_DESCRIPTION, mDescriptionTextView.getText().toString());
                            values.put(EmployeeContract.Columns.EMPLOYEES_MAIL, mMailTextView.getText().toString());
                            values.put(EmployeeContract.Columns.EMPLOYEES_ADDRESS, mAddress.getText().toString());
                            values.put(EmployeeContract.Columns.EMPLOYEES_PHONENUMBER, pn);
                            values.put(app.iggy.empleados.EmployeeContract.Columns.EMPLOYEES_SORTORDER, so);
                            contentResolver.insert(app.iggy.empleados.EmployeeContract.CONTENT_URI, values);
                        }
                        break;
                }
                Log.d(TAG, "onClick: done editing");

                if (mSaveListener!=null){
                    mSaveListener.onSaveClicked();
                }
            }
        });
        Log.d(TAG, "onCreateView: exiting");

        return view;
    }

}

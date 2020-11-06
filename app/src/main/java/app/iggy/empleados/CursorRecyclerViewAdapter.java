package app.iggy.empleados;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class CursorRecyclerViewAdapter extends RecyclerView.Adapter<CursorRecyclerViewAdapter.EmployeesViewHolder>{
    private static final String TAG = "CursorRecyclerViewAdapt";
    private Cursor mCursor;
    private OnTaskClickListener mListener;
    private int FADE_DURATION = 200;

    interface OnTaskClickListener{
        void onEditClick(Employee task);
        void onDeleteClick(Employee task);
        void onDetailsClick(Employee task);
    }

    public CursorRecyclerViewAdapter(Cursor cursor, OnTaskClickListener listener) {
        Log.d(TAG, "CursorRecyclerViewAdapter: constructor called " + cursor);
        mCursor = cursor;
        mListener = listener;
    }


    @NonNull
    @Override
    public EmployeesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_list_items, parent, false);
        return new EmployeesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeesViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: starts");

        if ((mCursor == null) || (mCursor.getCount() == 0)){
            Log.d(TAG, "onBindViewHolder: providing instructions");
            holder.name.setText(R.string.instructions_heading);
            holder.description.setText(R.string.instructions);
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }else{
            if (!mCursor.moveToPosition(position)){
                throw new IllegalStateException("Couldn't move cursor to position " + position);
            }

            final Employee employee = new Employee(mCursor.getLong(mCursor.getColumnIndex(EmployeeContract.Columns._ID)),
                    mCursor.getString(mCursor.getColumnIndex(EmployeeContract.Columns.EMPLOYEES_NAME)),
                    mCursor.getString(mCursor.getColumnIndex(EmployeeContract.Columns.EMPLOYEES_DESCRIPTION)),
                    mCursor.getInt(mCursor.getColumnIndex(EmployeeContract.Columns.EMPLOYEES_SORTORDER)),
                    mCursor.getString(mCursor.getColumnIndex(EmployeeContract.Columns.EMPLOYEES_MAIL)),
                    mCursor.getString(mCursor.getColumnIndex(EmployeeContract.Columns.EMPLOYEES_ADDRESS)),
                    mCursor.getLong(mCursor.getColumnIndex(EmployeeContract.Columns.EMPLOYEES_PHONENUMBER)));


            holder.name.setText(employee.getName());
            holder.description.setText(employee.getDescription());
            holder.editButton.setVisibility(View.VISIBLE); // TODO add onClick listener
            holder.deleteButton.setVisibility(View.VISIBLE); // TODO add onClick listener

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onDetailsClick(employee);
                }
            });

            View.OnClickListener buttonListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: starts");
                    switch (v.getId()){
                        case R.id.tli_edit:
                            if (mListener!=null){
                                mListener.onEditClick(employee);
                            }
                            break;
                        case R.id.tli_delete:
                            if (mListener!=null){
                                mListener.onDeleteClick(employee);
                            }
                            break;
                        case R.id.tli_name:
                            Log.d(TAG, "onClick: starts");
                            break;
                            default:
                                Log.d(TAG, "onClick: found unexpected button id");
                    }
                    Log.d(TAG, "onClick: button with id " + v.getId() + " clicked");
                    Log.d(TAG, "onClick: task name is " + employee.getName());
                }
            };

            holder.deleteButton.setOnClickListener(buttonListener);
            holder.editButton.setOnClickListener(buttonListener);


            setScaleAnimation(holder.itemView);
        }
    }

    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: starts");
        if ((mCursor == null) || (mCursor.getCount() == 0)){
            return 1; //fib, because we populate a single view with instructions
        }else{
            Log.d(TAG, "getItemCount: count is " + mCursor.getCount());
            return mCursor.getCount();
        }
    }

    /**
     * swap in a new cursor, returning the old Cursor
     * the returned old cursor is <em>not</em> closed
     *
     * @param newCursor The new cursor to be used
     * @return  Returns the previously set Cursor, or null if there wasn't one.
     * if the given new Cursor is the same instance as the previously set
     * Cursor, null is also returned
     */
    Cursor swapCursor(Cursor newCursor){
        Log.d(TAG, "swapCursor: cursor is " + newCursor);
        if (newCursor == mCursor){
            Log.d(TAG, "swapCursor: they're the same");
            return null;
        }

        final Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if (newCursor != null){
            Log.d(TAG, "swapCursor: receives a new cursor");
            //notify the observers about the new cursor
            notifyDataSetChanged();
        }else{
            //notify the observers about the lack of data set
            notifyItemRangeRemoved(0, getItemCount());
        }
        Log.d(TAG, "swapCursor: returns " + oldCursor);
        return oldCursor;
    }



    static class EmployeesViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "EmployeesViewHolder";

        TextView name = null;
        TextView description = null;
        ImageButton editButton = null;
        ImageButton deleteButton = null;

        public EmployeesViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "EmployeesViewHolder: starts");

            this.name = (TextView) itemView.findViewById(R.id.tli_name);
            this.description = (TextView) itemView.findViewById(R.id.tli_description);
            this.editButton = (ImageButton) itemView.findViewById(R.id.tli_edit);
            this.deleteButton = (ImageButton) itemView.findViewById(R.id.tli_delete);
        }
    }
}

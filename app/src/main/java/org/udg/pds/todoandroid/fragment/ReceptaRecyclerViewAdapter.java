package org.udg.pds.todoandroid.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.R_recepta;
import org.udg.pds.todoandroid.entity.Recepta;
import org.udg.pds.todoandroid.fragment.placeholder.PlaceholderContent.PlaceholderItem;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ReceptaRecyclerViewAdapter extends RecyclerView.Adapter<ReceptaRecyclerViewAdapter.ReceptaViewHolder> {

    private List<Recepta> mValues= new ArrayList<Recepta>();

    TodoApi mTodoService;

    public ReceptaRecyclerViewAdapter(TodoApi todoService) {
        mTodoService = todoService;
    }

    @Override
    public ReceptaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.fragment_recepta, parent, false);
        return new ReceptaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReceptaViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mName.setText(mValues.get(position).nom);
        holder.mDescription.setText(mValues.get(position).descripcio);
        if (!mValues.get(position).imageUrl.isEmpty()) {
            Picasso.get().load(mValues.get(position).imageUrl).into(holder.mImage);
        }
        holder.mCheckBox.setOnCheckedChangeListener(null); // Clear any previous listeners to avoid issues with recycled views
        holder.mCheckBox.setChecked(holder.mItem.isChecked());
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                R_recepta r = new R_recepta();
                r.id = holder.mItem.id;
                if (isChecked) {
                    r.posar = true;
                    Call<String> call = mTodoService.addRemoveReceptaPreferida(r);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()) {
                                holder.mItem.isChecked = true;
                                Toast.makeText(buttonView.getContext(), "Mira les receptes preferides!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(buttonView.getContext(), "Error llegint els preferits", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(buttonView.getContext(), "Error fent la crida a preferits", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                else {
                    r.posar = false;
                    Call<String> call = mTodoService.addRemoveReceptaPreferida(r);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()) {
                                holder.mItem.isChecked = false;
                                Toast.makeText(buttonView.getContext(), "Mira les receptes preferides!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(buttonView.getContext(), "Error llegint els preferits 2", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(buttonView.getContext(), "Error fent la crida a preferits", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setReceptes(List<Recepta> receptes) {
        mValues = receptes;
        notifyDataSetChanged();
    }

    public class ReceptaViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mName;
        public final TextView mDescription;
        public final ImageView mImage;
        public final CheckBox mCheckBox;

        public Recepta mItem;

        public ReceptaViewHolder(View view) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.name);
            mDescription = (TextView) view.findViewById(R.id.description);
            mImage = view.findViewById(R.id.idImage);
            mCheckBox= view.findViewById(R.id.checkbox);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDescription.getText() + "'";
        }
    }
}

package org.udg.pds.todoandroid.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Recepta;
import org.udg.pds.todoandroid.fragment.placeholder.PlaceholderContent.PlaceholderItem;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ReceptaRecyclerViewAdapter extends RecyclerView.Adapter<ReceptaRecyclerViewAdapter.ReceptaViewHolder> {

    private List<Recepta> mValues= new ArrayList<Recepta>();


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

        public Recepta mItem;

        public ReceptaViewHolder(View view) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.name);
            mDescription = (TextView) view.findViewById(R.id.description);
            mImage = view.findViewById(R.id.idImage);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDescription.getText() + "'";
        }
    }
}

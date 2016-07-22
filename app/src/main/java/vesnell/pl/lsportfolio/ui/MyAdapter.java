package vesnell.pl.lsportfolio.ui;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import vesnell.pl.lsportfolio.R;
import vesnell.pl.lsportfolio.model.Project;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private Context mContext;
    private List<Project> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public LinearLayout mLinearLayout;
        public ImageView mImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.name);
            mImageView = (ImageView) itemView.findViewById(R.id.icon);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.item_row);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context context, List<Project> myDataset) {
        mDataset = myDataset;
        mContext = context;
    }

    public MyAdapter(Context context) {
        mContext = context;
        mDataset = new ArrayList<Project>();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.project_list_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(position).name);

        //set height in proportion to screen size
        int proportionalHeight = mContext.getResources().getInteger(R.integer.project_item_row_height);
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, proportionalHeight); // (width, height)
        holder.mLinearLayout.setLayoutParams(params);
        if (position % 2 == 0) {
            holder.mLinearLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.itemRowGrey));
        } else {
            holder.mLinearLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        }
        Picasso.with(mContext).load(mDataset.get(position).icon)
                .resizeDimen(R.dimen.list_item_width, R.dimen.list_item_height).centerCrop().into(holder.mImageView);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void add(Project item, int position) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Project item) {
        int position = mDataset.indexOf(item);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    public void swap(List<Project> items){
        mDataset.clear();
        mDataset.addAll(items);
        notifyDataSetChanged();
    }
}

package vesnell.pl.lsportfolio.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import vesnell.pl.lsportfolio.R;
import vesnell.pl.lsportfolio.database.model.Project;

public class ListViewAdapter extends BaseAdapter {

    private Context context;
    private List<Project> projects = new ArrayList<Project>();

    static class ViewHolder {
        public TextView tvName;
        public ImageView ivIcon;
    }

    public ListViewAdapter(Context context) {
        this.context = context;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return projects.size();
    }

    @Override
    public Object getItem(int position) {
        return projects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.project_list_row, null);

            //configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) rowView.findViewById(R.id.name);
            viewHolder.ivIcon = (ImageView) rowView.findViewById(R.id.icon);
            rowView.setTag(viewHolder);
        }

        //fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();

        Project project = projects.get(position);
        String name = project.getName();
        holder.tvName.setText(name);

        Picasso.with(context).load(projects.get(position).getIcon())
                .resizeDimen(R.dimen.list_item_width, R.dimen.list_item_height).centerCrop().into(holder.ivIcon);

        return rowView;
    }
}

package adapter;

import android.content.Context;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import excelsoft.com.cl_launcher.R;
import model.AppInfoModel;

/**
 * Created by IMFCORP\alok.acharya on 24/2/17.
 */

public class AppInfoAdapter extends RecyclerView.Adapter<AppInfoAdapter.CustomViewHolder>{

    private ArrayList<AppInfoModel> dataList;
    private Context context;

    public AppInfoAdapter(Context context,ArrayList<AppInfoModel> dataList) {
        this.dataList=dataList;
        this.context=context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_icon_text, parent, false);
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        AppInfoModel model = dataList.get(position);
        holder.title.setText(model.getTitle());
        holder.icon.setImageDrawable(model.getIcon());
        if(model.isInstalled()&& model.isDownloaded()) {
            holder.loader.setVisibility(View.GONE);
        }else{
            holder.loader.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        public ContentLoadingProgressBar loader;
        public TextView title;
        public ImageView icon;
        public CustomViewHolder(View itemView) {
            super(itemView);
            loader = (ContentLoadingProgressBar) itemView.findViewById(R.id.progressBarContentLoading);
            title = (TextView) itemView.findViewById(R.id.text);
            icon = (ImageView) itemView.findViewById(R.id.icon);

        }
    }
}

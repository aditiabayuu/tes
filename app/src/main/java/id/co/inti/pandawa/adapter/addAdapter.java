package id.co.inti.pandawa.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import id.co.inti.pandawa.R;
import id.co.inti.pandawa.item.RecyclerViewClickListener;
import id.co.inti.pandawa.item.add_dev_holder;
import id.co.inti.pandawa.item.item;


import java.util.List;


/**
 * Created by Bangprod on 6/21/2017.
 */

public class addAdapter extends RecyclerView.Adapter<add_dev_holder> {
    private Activity activity;
    List<item> list;
    private Context mcontext;
    private RecyclerViewClickListener mListener;

    public addAdapter(List<item> list, Context mcontext, RecyclerViewClickListener recyclerViewClickListener) {
        this.list = list;
        this.mcontext = mcontext;
        this.mListener = recyclerViewClickListener;
    }

    @Override
    public add_dev_holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.add_dev_rec, parent, false);
        final add_dev_holder mViewHolder = new add_dev_holder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClick(view, mViewHolder.getAdapterPosition());
            }
        });
        return new add_dev_holder(view);
    }

    @Override
    public void onBindViewHolder(final add_dev_holder holder, final int position) {
        holder.id.setText(list.get(position).getName());
        holder.img.setImageBitmap(list.get(position).getImage());

        holder.cvMain.setTag(position);
       }

    @Override
    public int getItemCount() {
        return list.size();
    }

}

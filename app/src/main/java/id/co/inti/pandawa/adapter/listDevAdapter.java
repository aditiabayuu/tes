package id.co.inti.pandawa.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import id.co.inti.pandawa.R;
import id.co.inti.pandawa.item.RecyclerViewClickListener;
import id.co.inti.pandawa.item.add_dev_holder;
import id.co.inti.pandawa.item.item;
import id.co.inti.pandawa.item.list_dev_holder;

import java.util.List;


/**
 * Created by Bangprod on 6/21/2017.
 */

public class listDevAdapter extends RecyclerView.Adapter<list_dev_holder> {
    private Activity activity;
    List<item> list;
    private Context mcontext;
    private RecyclerViewClickListener mListener;

    public listDevAdapter(List<item> list, Context mcontext, RecyclerViewClickListener recyclerViewClickListener) {
        this.list = list;
        this.mcontext = mcontext;
        this.mListener = recyclerViewClickListener;
    }

    @Override
    public list_dev_holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.dev_list, parent, false);
        final list_dev_holder mViewHolder = new list_dev_holder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClick(view, mViewHolder.getAdapterPosition());
            }
        });
        return new list_dev_holder(view);
    }

    @Override
    public void onBindViewHolder(final list_dev_holder holder, final int position) {
        holder.id.setText(list.get(position).getName());
        holder.img.setImageBitmap(list.get(position).getImage());
        int i =list.get(position). getCount();
        Log.d("tag","count : "+i);

            //holder.temper.setText(list.get(position).getTemp());
            // 5 is the count how many times you want to add textview
            LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            TextView tv = new TextView(holder.temper.getContext());
            tv.setLayoutParams(lparams);
            tv.setText(list.get(position).getTemp());
            holder.temper.addView(tv);

        holder.cvMain.setTag(position);
       }

    @Override
    public int getItemCount() {
        return list.size();
    }

}

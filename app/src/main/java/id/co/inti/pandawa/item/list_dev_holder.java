package id.co.inti.pandawa.item;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import id.co.inti.pandawa.R;

public class list_dev_holder extends RecyclerView.ViewHolder {
    public TextView id,humid,baterai;
    public LinearLayout temper;
    public ImageView img;
    public CardView cvMain;
    RecyclerViewClickListener listener;
    public list_dev_holder(View itemView) {
        super(itemView);
        id= itemView.findViewById(R.id.txt_nama_dev);
        img= itemView.findViewById(R.id.img_v);
        cvMain=(CardView) itemView.findViewById(R.id.maincv);
        temper= itemView.findViewById(R.id.txt_temperature);

    }
}

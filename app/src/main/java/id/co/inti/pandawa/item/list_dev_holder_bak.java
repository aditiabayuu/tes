package id.co.inti.pandawa.item;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import id.co.inti.pandawa.R;

public class list_dev_holder_bak extends RecyclerView.ViewHolder {
    public TextView id,temper,humid,baterai;
    public ImageView img;
    public CardView cvMain;
    RecyclerViewClickListener listener;
    public list_dev_holder_bak(View itemView) {
        super(itemView);
        id= itemView.findViewById(R.id.txt_nama_dev);
        img= itemView.findViewById(R.id.img_v);
        cvMain=(CardView) itemView.findViewById(R.id.maincv);
        temper= itemView.findViewById(R.id.txt_temperature);
        humid= itemView.findViewById(R.id.txt_humid);
        baterai= itemView.findViewById(R.id.txt_bat);

    }
}

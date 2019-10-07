package id.co.inti.pandawa.item;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import id.co.inti.pandawa.R;

public class add_dev_holder extends RecyclerView.ViewHolder {
    public TextView id;
    public ImageView img;
    public CardView cvMain;
    RecyclerViewClickListener listener;
    public add_dev_holder(View itemView) {
        super(itemView);
        id= itemView.findViewById(R.id.txt_nama_dev);
        img= itemView.findViewById(R.id.img_v);
        cvMain=(CardView) itemView.findViewById(R.id.maincv);

    }
}

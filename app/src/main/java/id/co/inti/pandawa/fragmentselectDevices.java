package id.co.inti.pandawa;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import id.co.inti.pandawa.adapter.addAdapter;
import id.co.inti.pandawa.dbhelper.DBHelper;
import id.co.inti.pandawa.item.RecyclerViewClickListener;
import id.co.inti.pandawa.item.item;

import java.util.ArrayList;

public class fragmentselectDevices extends Fragment {
    private View rootView;
    DBHelper dbHelper;
    RecyclerView rv;
    EditText tx;
    Context context;
    LinearLayoutManager layoutManager;
    addAdapter adapter;
    private ArrayList<item> items = new ArrayList<item>();
    private String data, name;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.add_device, container, false);
        rv = (RecyclerView) rootView.findViewById(R.id.rcv);
        context = getActivity();
        items.clear();
        Bundle args = getArguments();
        if(args!=null)
        {
            name = getArguments().getString("name");
        }
        try {
            dbHelper = new DBHelper(context);
            dbHelper.openDB();
            Cursor cursor = dbHelper.getCategory();
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    item it = new item();
                    String name = cursor.getString(cursor.getColumnIndex("type"));
                    it.setName(name);
                    byte[] byteArray = cursor.getBlob(cursor.getColumnIndex("image"));
                    Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    it.setImage(bm);
                    items.add(it);

                }
            }
            adapter = new addAdapter(items, context,new RecyclerViewClickListener(){
                @Override
                public void onClick(View view, int position) {
                    int i= (int) view.getTag();
                    Log.d("tag","POINTER"+i);
                    String title = ((TextView) rv.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.txt_nama_dev)).getText().toString();
                    Log.d("tag",title);
                    Bundle bundle = new Bundle();
                    bundle.putString("dev",title);
                    bundle.putString("name",name);
                    Fragment frg = new fragmentRegisterDev();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    frg.setArguments(bundle);
                    ft.replace(R.id.main_container, frg, "get");
                    ft.addToBackStack(null);
                    dbHelper.closeDB();
                    ft.commit();
                    //getFragmentManager().popBackStackImmediate();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setAdapter(adapter);
        rv.setLayoutManager(layoutManager);
        rv.setHasFixedSize(true);
        return rootView;
    }
}

package id.co.inti.pandawa

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView

class fragmentNodevice : Fragment() {
    private var dialog: Dialog? = null
    private var rootView: View? = null
    private lateinit var  img_add : ImageButton
    private lateinit var  add : Button
    private lateinit var data : String
    private lateinit var name : TextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.nodevice, container, false)
        img_add=rootView!!.findViewById(R.id.add_dev)
        (activity as AppCompatActivity).supportActionBar!!.hide()
        add= rootView!!.findViewById(R.id.add_device)
        name= rootView!!.findViewById(R.id.txt_name)
        val bundle = this.arguments
        data= Preferences.getRegisteredUser(activity!!)
        name.setText(data+"'s Home")
        if (bundle != null) {
            data = bundle.getString("name")
            Log.d("tag", data)
            //name.setText(data+"'s Home")
        }
        img_add.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("name",data)
            val ft = fragmentManager!!.beginTransaction()
            val frg = fragmentRegisterDev()
            frg.arguments = bundle
            ft.replace(R.id.main_container, frg, "get")
            ft.addToBackStack(null)
            ft.commit()

            //fragmentManager!!.popBackStack();
        }

        add.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("name",data)
            val ft = fragmentManager!!.beginTransaction()
            val frg = fragmentRegisterDev()
            frg.arguments = bundle
            ft.replace(R.id.main_container, frg, "get")
            ft.addToBackStack(null)
            ft.commit()

           //fragmentmanager.popBackStack();
            //fragmentManager!!.popBackStack();
        }
        return rootView
    }
}
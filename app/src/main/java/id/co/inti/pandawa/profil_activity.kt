package id.co.inti.pandawa

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.mikhaellopez.circularimageview.CircularImageView
import android.Manifest
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import id.co.inti.pandawa.dbhelper.DBHelper
import java.io.*
import java.lang.Exception
import java.util.*
import android.util.Base64;

class profil_activity : Fragment()
{
    private var dialog: Dialog? = null
    private var rootView: View? = null
    private lateinit var names : TextView
    private lateinit var data : String
    private lateinit var  pwd : Button
    private lateinit var about : Button
    private lateinit var logout : Button
    internal var context: Context? = null
    private lateinit var prof : CircularImageView
    private lateinit var gmbar : CircularImageView
    private lateinit var dbHelper: DBHelper
    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
        private val GALLERY = 1
        private val CAMERA = 2
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.profile_activity_bak, container, false)
        (activity as AppCompatActivity).supportActionBar!!.hide()
        names= rootView!!.findViewById(R.id.user_profile_name)
        data= Preferences.getLoggedInUser(activity!!)
        context=activity
        names.setText(data)
        dbHelper = DBHelper(context)
        dbHelper.openDB()
        gmbar=rootView!!.findViewById(R.id.img_profile)
        prof=rootView!!.findViewById(R.id.img_plus)
        pwd=rootView!!.findViewById(R.id.btn_pwd)
        about=rootView!!.findViewById(R.id.btn_about)
        logout=rootView!!.findViewById(R.id.btn_logout)
        logout.setOnClickListener {
            val mainIntent = Intent(activity, LoginActivity::class.java)
            Preferences.clearLoggedInUser(activity);
            //mainIntent.putExtra("name",usrname.text.toString())
            this.startActivity(mainIntent)
        }
        pwd.setOnClickListener {
            val frg =change_password()
            val ft = fragmentManager!!.beginTransaction()
            ft.replace(R.id.main_container, frg, "get")
            ft.addToBackStack(null)
            ft.commit()
        }

        try {
            val namess = Preferences.getRegisteredUser(context)
            val byteArray = dbHelper.getPhoto(namess)

            val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            gmbar!!.setImageBitmap(bmp)
        }
        catch (ex: Exception)
        {
            ex.printStackTrace()
        }



        prof.setOnClickListener {
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (ContextCompat.checkSelfPermission(activity!!,Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE);
                }
                else{
                    //permission already granted
                    pickImageFromGallery();
                }
            }
            else{
                //system OS is < Marshmallow
                pickImageFromGallery();
            }*/

            val pictureDialog = AlertDialog.Builder(context)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItems = arrayOf("Select image from gallery", "Capture photo from camera")
            pictureDialog.setItems(pictureDialogItems
            ) { dialog, which ->
                when (which) {
                    0 -> chooseImageFromGallery()
                    1 -> takePhotoFromCamera()
                }
            }
            pictureDialog.show()
        }
        return rootView;
    }
    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }



    private fun takePhotoFromCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(activity!!,Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_DENIED){
                //permission denied
                val permissions = arrayOf(Manifest.permission.CAMERA);
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE);
            }
            if (ContextCompat.checkSelfPermission(activity!!,Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED){
                //permission denied
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE);
            }
            else{
                //permission already granted
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA)
            }
        }
        else{
            //system OS is < Marshmallow
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA)
        }


    }

    fun chooseImageFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(activity!!,Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED){
                //permission denied
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE);
            }
            else{
                //permission already granted
                pickImageFromGallery();
            }
        }
        else{
            //system OS is < Marshmallow
            pickImageFromGallery();
        }
        /*val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY)*/
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size >0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    if (requestCode==IMAGE_PICK_CODE) {
                        pickImageFromGallery()
                    }
                    else  if (requestCode==CAMERA) {
                        takePhotoFromCamera()
                    }
                }
                else{
                    //permission from popup denied
                    Toast.makeText(activity, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode==IMAGE_PICK_CODE) {
            if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
                gmbar.setImageURI(data?.data)
                try{
                    val drawable = gmbar.getDrawable() as BitmapDrawable
                    val bm = drawable.getBitmap();
                    val stream = ByteArrayOutputStream()
                    bm.compress(Bitmap.CompressFormat.JPEG, 50, stream)
                    val ids= Preferences.getRegisteredUser(context)
                    val byteArray = stream.toByteArray()
                    val sr = Base64.encode(byteArray, Base64.DEFAULT)
                    Log.d("tag", "AWWW: " + String(sr))
                    dbHelper.insertPhoto(ids,byteArray)

                }
                catch(ex : java.lang.Exception)
                {
                    ex.printStackTrace()
                }

            }
        }
        else if (requestCode == CAMERA)
        {
            try {
                val thumbnail = data!!.extras!!.get("data") as Bitmap
                gmbar!!.setImageBitmap(thumbnail)
                val ids = Preferences.getRegisteredUser(context)
                val stream = ByteArrayOutputStream()
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val byteArray = stream.toByteArray()
                dbHelper.insertPhoto(ids, byteArray)
                saveImage(thumbnail)
                Toast.makeText(context, "Photo Show!", Toast.LENGTH_SHORT).show()
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }
        }
    }
    fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
        val dir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM
            ), "Camera"
        )
        val wallpaperDirectory = File (
            (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)).toString())
        Log.d("fee", wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists())
        {
            wallpaperDirectory.mkdirs()
        }
        try
        {
            Log.d("heel", wallpaperDirectory.toString())
            val f = File(wallpaperDirectory, ((Calendar.getInstance()
                .getTimeInMillis()).toString() + ".png"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(context, arrayOf(f.getPath()), arrayOf("image/png"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())

            return f.getAbsolutePath()
        }
        catch (e1: IOException){
            e1.printStackTrace()
        }
        return ""
    }

    fun getBytes(inputStream:InputStream):ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 32000
        val buffer = ByteArray(bufferSize)
        val len = inputStream.read(buffer)
        while (len  != -1)
        {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }




}
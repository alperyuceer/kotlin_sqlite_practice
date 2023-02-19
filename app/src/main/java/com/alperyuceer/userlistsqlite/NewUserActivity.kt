package com.alperyuceer.userlistsqlite

import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alperyuceer.userlistsqlite.databinding.ActivityNewUserBinding
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.sql.Statement

class NewUserActivity : AppCompatActivity() {

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedBitmap: Bitmap?=null
    private lateinit var dataBase: SQLiteDatabase


    private lateinit var binding:ActivityNewUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        dataBase = this.openOrCreateDatabase("Users", MODE_PRIVATE,null)

        registerLauncher()
    }

    fun saveButton(view: View){
        val userName = binding.newUserNameText.text.toString()
        val userYear = binding.newUserYearText.text.toString()
        if (selectedBitmap!=null){
            val smallBitmap = makeSmallerBitmap(selectedBitmap!!,300)
            val outputStream=ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteArray =outputStream.toByteArray()
            try {
                dataBase.execSQL("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY,username VARCHAR, useryear VARCHAR, userimage BLOB)")
                val sqlString = "INSERT INTO users (username, useryear, userimage) VALUES (?, ?, ?)"
                val statement = dataBase.compileStatement(sqlString)
                statement.bindString(1,userName)
                statement.bindString(2,userYear)
                statement.bindBlob(3,byteArray)
                statement.execute()


            }catch (e:Exception){
                e.printStackTrace()
            }
            val intent =Intent(this@NewUserActivity,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

        }


    }
    fun makeSmallerBitmap(image:Bitmap,maxSize:Int):Bitmap{
        var width = image.width
        var height = image.height

        var bitmapRatio: Double = width.toDouble()/height.toDouble()

        if (bitmapRatio>1){
            //yatay
            width = maxSize
            val scaledHeight = width/bitmapRatio
            height = scaledHeight.toInt()
        }else{
            //dikey
            height =maxSize
            val scaledWidth = height*bitmapRatio
            width = scaledWidth.toInt()

        }
        return Bitmap.createScaledBitmap(image,width,height,true)


    }


    fun selectImage(view: View){
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_MEDIA_IMAGES)!= PackageManager.PERMISSION_GRANTED){
            //rationale yap
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_MEDIA_IMAGES)){
                Snackbar.make(view,"Permission needed!",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",View.OnClickListener {
                    //izin iste
                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)

                }).show()

            }else{
                //izin iste
                permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            }
        }else{
            //izin verildi
            val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }


    }
    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
            if (result.resultCode == RESULT_OK){
                val intentFromResult = result.data
                if(intentFromResult!=null){
                    val imageData=intentFromResult.data
                    if(imageData!=null){
                        try {
                            if (Build.VERSION.SDK_INT>=28){
                                val source = ImageDecoder.createSource(this@NewUserActivity.contentResolver,imageData)
                                selectedBitmap = ImageDecoder.decodeBitmap(source)
                                binding.imageView.setImageBitmap(selectedBitmap)

                            }else{
                                selectedBitmap =MediaStore.Images.Media.getBitmap(contentResolver,imageData)
                                binding.imageView.setImageBitmap(selectedBitmap)

                            }


                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){result->
            if (result){
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }else{
                Toast.makeText(this@NewUserActivity,"Permission denied'",Toast.LENGTH_LONG).show()
            }
        }

    }
}
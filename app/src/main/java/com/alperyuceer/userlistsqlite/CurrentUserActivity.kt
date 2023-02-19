package com.alperyuceer.userlistsqlite

import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alperyuceer.userlistsqlite.databinding.ActivityCurrentUserBinding

class CurrentUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCurrentUserBinding
    private lateinit var database: SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrentUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        database = this.openOrCreateDatabase("Users", MODE_PRIVATE,null)
        val intent = intent
        val selectedId = intent.getIntExtra("id",1)
        val cursor = database.rawQuery("SELECT * FROM users WHERE id= ?", arrayOf(selectedId.toString()))
        val userNameIx = cursor.getColumnIndex("username")
        val userYearIx = cursor.getColumnIndex("useryear")
        val userImageIx = cursor.getColumnIndex("userimage")

        while (cursor.moveToNext()){
            binding.currentUserName.setText(cursor.getString(userNameIx))
            binding.currentUserYear.setText(cursor.getString(userYearIx))


            val byteArray =cursor.getBlob(userImageIx)

            val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
            binding.imageView2.setImageBitmap(bitmap)
        }
        cursor.close()


    }
}
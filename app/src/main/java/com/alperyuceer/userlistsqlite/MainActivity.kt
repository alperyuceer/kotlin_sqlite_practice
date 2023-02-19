package com.alperyuceer.userlistsqlite

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.alperyuceer.userlistsqlite.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userList: ArrayList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        userList = ArrayList<User>()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = UserAdapter(userList)
        binding.recyclerView.adapter = adapter


        try {
            val database = this.openOrCreateDatabase("Users", MODE_PRIVATE,null)
            val cursor =database.rawQuery("SELECT * FROM users",null)
            val userNameIx = cursor.getColumnIndex("username")
            val idIx = cursor.getColumnIndex("id")

            while (cursor.moveToNext()){
                val name = cursor.getString(userNameIx)
                val id =cursor.getInt(idIx)
                val user = User(name,id)
                userList.add(user)
            }
            cursor.close()



        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_user_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.yeniKullaniciEkle){
            val intent = Intent(this@MainActivity,NewUserActivity::class.java)
            startActivity(intent)

        }
        return super.onOptionsItemSelected(item)
    }
}
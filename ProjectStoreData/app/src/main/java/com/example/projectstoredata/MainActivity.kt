package com.example.projectstoredata

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectstoredata.databinding.ActivityMainBinding
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var ProductList:ArrayList<Product>
    private lateinit var ProductAdapter:ProductAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        ProductList = ArrayList<Product>()

        ProductAdapter = ProductAdapter(ProductList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = ProductAdapter

        binding.buttonlevel.setOnClickListener{
            val intent = Intent(this,ProductAdd::class.java)
            intent.putExtra("info","new")
            startActivity(intent)
        }
        try {

            val database = this.openOrCreateDatabase("products", Context.MODE_PRIVATE,null)

            val cursor = database.rawQuery("SELECT * FROM products",null)
            val productNameIx = cursor.getColumnIndex("productname")
            val idIx = cursor.getColumnIndex("id")

            while (cursor.moveToNext()) {
                val name = cursor.getString(productNameIx)
                val id = cursor.getInt(idIx)
                val procut = Product(name,id)
                ProductList.add(procut)
            }

            ProductAdapter.notifyDataSetChanged()

            cursor.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}
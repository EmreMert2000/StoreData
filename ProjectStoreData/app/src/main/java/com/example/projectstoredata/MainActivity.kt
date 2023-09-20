package com.example.projectstoredata

import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlin.text.contains
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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






      //  val adapterSearch = ProductAdapter(ProductList)


        binding.searchbox.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
          override fun onQueryTextSubmit(query: String?): Boolean {

              if (query != null) {
                  ProductAdapter.filter(query)
              }
              return true

          }

          override fun onQueryTextChange(newText: String?): Boolean {

              newText?.let {
                  ProductAdapter.filter(it)
              }
                 return true
          }


      })

        binding.buttonlevel.setOnClickListener{
            val intent = Intent(this,ProductAdd::class.java)
            intent.putExtra("info","new")
            startActivity(intent)
        }
        try {

            val database = this.openOrCreateDatabase("Prod", Context.MODE_PRIVATE,null)


            val cursor = database.rawQuery("SELECT * FROM prod",null)
            val productNameIx = cursor.getColumnIndex("productname")
            val idIx = cursor.getColumnIndex("id")

            while (cursor.moveToNext()) {
                val name = cursor.getString(productNameIx)
                val id = cursor.getInt(idIx)
                val product = Product(name,id)
                ProductList.add(product)
            }

            ProductAdapter.notifyDataSetChanged()

            cursor.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun onSearchResult(filteredResults: List<Product>) {

    }


}



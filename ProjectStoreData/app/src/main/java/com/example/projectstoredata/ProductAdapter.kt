package com.example.projectstoredata

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectstoredata.databinding.RecRowBinding

class ProductAdapter(val Productlist : ArrayList<Product>) : RecyclerView.Adapter<ProductAdapter.ProductHolder>() {
    class ProductHolder(val binding:RecRowBinding) : RecyclerView.ViewHolder(binding.root) {
    }
    private var filteredList: ArrayList<Product> = ArrayList(Productlist)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val binding = RecRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductHolder(binding)
    }

    override  fun onBindViewHolder(holder: ProductHolder, position: Int) {
        try {
            holder.binding.recyclerViewTextView.text = Productlist[position].name



            holder.itemView.setOnClickListener {

                val intent = Intent(holder.itemView.context, ProductAdd::class.java)

                intent.putExtra("info", "old")
                intent.putExtra("id",Productlist[position].id)


                holder.itemView.context.startActivity(intent)
            }

        } catch (e: IndexOutOfBoundsException) {
            Log.e("ProductAdapter", "IndexOutOfBoundsException: ${e.message}")
        } catch (e: Exception) {
            Log.e("ProductAdapter", "Error in onBindViewHolder: ${e.message}")
        }
    }







    override fun getItemCount(): Int {
        return Productlist.size
    }
    fun filter(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(Productlist)
        } else {
            val lowercaseQuery = query.toLowerCase()
            Productlist.forEach { product ->
                if (product.name.toLowerCase().contains(lowercaseQuery)) {
                    filteredList.add(product)
                }
            }
        }
        notifyDataSetChanged()

    }
}

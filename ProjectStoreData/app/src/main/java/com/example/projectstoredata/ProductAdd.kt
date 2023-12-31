package com.example.projectstoredata

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.example.projectstoredata.databinding.ActivityMainBinding
import com.example.projectstoredata.databinding.ActivityProductAddBinding
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.io.IOException

class ProductAdd : AppCompatActivity() {
    private  var selectedBitmap : Bitmap? = null
    private lateinit var binding: ActivityProductAddBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var database: SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductAddBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        // setContentView(R.layout.activity_product_add)

        database=this.openOrCreateDatabase("Prod",Context.MODE_PRIVATE,null)
        registerLauncher()
        val intent = intent

        val info = intent.getStringExtra("info")


        binding.DeleteButton.visibility = View.INVISIBLE
        binding.UpdateButton.visibility=View.INVISIBLE

        if (info.equals("new")) {
            binding.ProductName.setText("")
            binding.ProductAdet.setText("")
            binding.ProductPrice.setText("")
            binding.button.visibility = View.VISIBLE

            val selectedImageBackground = BitmapFactory.decodeResource(applicationContext.resources,R.drawable.selectem)
            binding.imageView.setImageBitmap(selectedImageBackground)

        } else {
            binding.button.visibility = View.INVISIBLE
            binding.DeleteButton.visibility=View.VISIBLE
            binding.UpdateButton.visibility=View.VISIBLE

            val selectedId = intent.getIntExtra("id",1)

            val cursor = database.rawQuery("SELECT * FROM Prod WHERE id = ?", arrayOf(selectedId.toString()))

            val productnameIx = cursor.getColumnIndex("productname")
            val productAdetIx = cursor.getColumnIndex("productadet")
            val productPriceIx = cursor.getColumnIndex("productprice")
            val imageIx = cursor.getColumnIndex("image")

            while (cursor.moveToNext()) {
                binding.ProductName.setText(cursor.getString(productnameIx))
                binding.ProductAdet.setText(cursor.getString(productAdetIx))
                binding.ProductPrice.setText(cursor.getString(productPriceIx))

                val byteArray = cursor.getBlob(imageIx)
                val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                binding.imageView.setImageBitmap(bitmap)

            }

            cursor.close()

        }



    }

    fun save(view: View) {

        val productName = binding.ProductName.text.toString()
        val productadet = binding.ProductAdet.text.toString()
        val productPrice = binding.ProductPrice.text.toString()

        if (selectedBitmap != null) {
            val smallBitmap = makeSmallerBitmap(selectedBitmap!!, 300)

            val outputStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            val byteArray = outputStream.toByteArray()

            try {

                database.execSQL("CREATE TABLE IF NOT EXISTS prod (id INTEGER PRIMARY KEY, productname VARCHAR, productadet VARCHAR,productprice VARCHAR, image BLOB)")

                val sqlString =
                    "INSERT INTO prod (productname, productadet, productprice, image) VALUES (?, ?, ?, ?)"
                val statement = database.compileStatement(sqlString)
                statement.bindString(1, productName)
                statement.bindString(2, productadet)
                statement.bindString(3, productPrice)
                statement.bindBlob(4, byteArray)

                statement.execute()
                Toast.makeText(this@ProductAdd, "Ürün Başarıyla Eklendi!", Toast.LENGTH_LONG).show()

            } catch (e: Exception) {
                e.printStackTrace()
            }


            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            startActivity(intent)

            //finish()
        }
    }

    fun deleteFun(view: View)
    {
        try {

            val selectedId = intent.getIntExtra("id",1)

             val cursor=database.rawQuery("DELETE  FROM Prod WHERE id = ?", arrayOf(selectedId.toString()))
            val productnameIx = cursor.getColumnIndex("productname")
            val productAdetIx = cursor.getColumnIndex("productadet")
            val productPriceIx = cursor.getColumnIndex("productprice")
            val imageIx = cursor.getColumnIndex("image")

            while (cursor.moveToNext()) {
                binding.ProductName.setText(cursor.getString(productnameIx))
                binding.ProductAdet.setText(cursor.getString(productAdetIx))
                binding.ProductPrice.setText(cursor.getString(productPriceIx))

                val byteArray = cursor.getBlob(imageIx)
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                binding.imageView.setImageBitmap(bitmap)
            }
            cursor.close()

            Toast.makeText(this, "Ürün Başarıyla Silindi.", Toast.LENGTH_SHORT).show()



        } catch (e: Exception) {
            e.printStackTrace()

        }


        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        startActivity(intent)
    }

    fun UpdateFun(view: View) {
        try {
            val selectedId = intent.getIntExtra("id", 1)

            // Kullanıcının girdiği yeni verileri alın
            val yeniProductIsmi = binding.ProductName.text.toString()
            val yeniProductAdeti = binding.ProductAdet.text.toString()
            val yeniProductFiyati = binding.ProductPrice.text.toString()


            // Güncel verileri ContentValues nesnesine ekleyin
            val contentValues = ContentValues()
            contentValues.put("productname", yeniProductIsmi)
            contentValues.put("productadet", yeniProductAdeti)
            contentValues.put("productprice", yeniProductFiyati)


            // UPDATE sorgusu ile veritabanını güncelleyin
            val affectedRows =
                database.update("Prod", contentValues, "id = ?", arrayOf(selectedId.toString()))

            if (affectedRows > 0) {
                Toast.makeText(this, "Ürün Başarıyla Güncellendi.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Ürün Güncellenemedi.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
















    private fun makeSmallerBitmap(image: Bitmap, maximumSize : Int) : Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio : Double = width.toDouble() / height.toDouble()
        if (bitmapRatio > 1) {
            width = maximumSize
            val scaledHeight = width / bitmapRatio
            height = scaledHeight.toInt()
        } else {
            height = maximumSize
            val scaledWidth = height * bitmapRatio
            width = scaledWidth.toInt()
        }
        return Bitmap.createScaledBitmap(image,width,height,true)
    }

    fun selectImage(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                ) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give Permission",
                            View.OnClickListener {
                                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                            }).show()
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give Permission",
                            View.OnClickListener {
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }).show()
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }

    }
    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    val imageData = intentFromResult.data
                    try {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(this@ProductAdd.contentResolver, imageData!!)
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            binding.imageView.setImageBitmap(selectedBitmap)
                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(this@ProductAdd.contentResolver, imageData)
                            binding.imageView.setImageBitmap(selectedBitmap)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                //permission denied
                Toast.makeText(this@ProductAdd, "Permisson needed!", Toast.LENGTH_LONG).show()
            }
        }
    }
}
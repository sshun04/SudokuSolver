package com.shojishunsuke.myapplication

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    val REQUEST_CODE_CAEMRA = 1
    lateinit var imageView: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.captureButton)
        imageView = findViewById(R.id.takenImage)
        button.setOnClickListener {
            intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_CODE_CAEMRA)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAEMRA) {

                val bitmap = data?.extras?.get("data") as Bitmap
                    imageView.setImageBitmap(bitmap)




            }
        }
    }


    override fun onResume() {
        super.onResume()

    }


}






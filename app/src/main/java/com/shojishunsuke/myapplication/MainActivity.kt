package com.shojishunsuke.myapplication

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage

class MainActivity : AppCompatActivity() {

    val REQUEST_CODE_CAEMRA = 1
    lateinit var imageView: ImageView
    lateinit var sudokuRespondent: SudokuRespondent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val captureButton = findViewById<Button>(R.id.captureButton)
        val viewAnswerButton = findViewById<Button>(R.id.viewAnswer)
        imageView = findViewById(R.id.takenImage)


        captureButton.setOnClickListener {
            intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_CODE_CAEMRA)
        }

        viewAnswerButton.setOnClickListener {
            val answer = sudokuRespondent.solve()

         System.out.println(answer[8][8])
            sudokuRespondent.view()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

//        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAEMRA) {

                val bitmap = data?.extras?.get("data") as Bitmap
                val image = FirebaseVisionImage.fromBitmap(bitmap)

                imageView.setImageBitmap(bitmap)

                val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

                val result = detector.processImage(image)
                    .addOnSuccessListener {
                        sudokuRespondent = SudokuRespondent(it)
                    }
                    .addOnFailureListener {
                        Log.d("MainActivity","認識できませんでした")
                    }


//            }
        }
    }


    override fun onResume() {
        super.onResume()

    }


}






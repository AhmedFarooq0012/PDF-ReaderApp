package com.example.permission_pdf_name

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.barteksc.pdfviewer.PDFView
import java.io.File

class pdfview : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdfview)

        findViewById<PDFView>(R.id.pdfView).fromFile(File(intent.getStringExtra("name"))).onError {
            Log.e("TAG", "onCreate: ${intent.getStringExtra("name")}" )
        }.load()
    }
}
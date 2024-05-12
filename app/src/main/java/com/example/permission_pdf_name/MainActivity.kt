package com.example.permission_pdf_name

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class MainActivity : AppCompatActivity(), Pdffileinterface {

    private lateinit var button: Button
    private lateinit var searchView: SearchView
    private lateinit var rcview: RecyclerView
    private lateinit var pdffilenamelist: MutableList<PDFfile>
    private lateinit var datalistAdapter: pdfAdapter
    private lateinit var checkall: CheckBox
    private lateinit var gridshow: Button
    private lateinit var btnsend: Button
    private var isGridLayout = false

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!hasStoragePermission()) {
            requestStoragePermission()
        } else {
            initializeUI()
        }
    }

    private fun initializeUI() {
        checkall = findViewById(R.id.checkall)
        searchView = findViewById(R.id.itemsearch)
        button = findViewById(R.id.topviewid)
        rcview = findViewById(R.id.recyclerview)
        btnsend = findViewById(R.id.sendall)
        gridshow = findViewById(R.id.searchid)
        searchView.queryHint = "Search Pdf file"
        val layoutManager = LinearLayoutManager(this)
        pdffilenamelist = getpdfname()
        datalistAdapter = pdfAdapter(pdffilenamelist, this)
        rcview.layoutManager = layoutManager
        rcview.adapter = datalistAdapter

        checkall.setOnClickListener {

            datalistAdapter.newlist.size
            datalistAdapter.updateSelectAll(checkall.isChecked)
            datalistAdapter.dataList
        }
        gridshow.setOnClickListener {
            isGridLayout = !isGridLayout
            val layoutManager = if (isGridLayout) {
                GridLayoutManager(this, 2)  // Set the number of columns as needed
            } else {
                // Use LinearLayoutManager for list layout
                LinearLayoutManager(this)
            }
            rcview.layoutManager = layoutManager
            datalistAdapter.notifyDataSetChanged()
        }

        button.setOnClickListener {
            Log.e(TAG, "onCreate: my selected data is ${datalistAdapter.dataList}")
            Log.e(TAG, "onCreate: ${datalistAdapter.dataList.size}")
            val urilist = ArrayList<Uri>()
            for (file in datalistAdapter.dataList) {
                val uri = uriFromFile(file)
                urilist.add(uri)
            }
            val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, urilist)
            shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            shareIntent.type = "*/*"
            startActivity(Intent.createChooser(shareIntent, "Share ..."))
        }

        btnsend.setOnClickListener {
            Log.e(TAG, "onCreate: list created and working")
            val list = ArrayList<Uri>()
            for (file in datalistAdapter.newlist) {
                val uri = uriFromFile(file)
                list.add(uri)
            }
            Log.e(TAG, "onCreate: data added in list $list ")
            val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, list)
            shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            shareIntent.type = "*/*"
            startActivity(Intent.createChooser(shareIntent, "All file Share ..."))
            Log.e(TAG, "onCreate: sharefile is $list")
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                datalistAdapter.filter(p0.orEmpty())
                return true
            }
        })
    }

    private fun hasStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeUI()
            } else {
                finish()
            }
        }
    }

    private fun getpdfname(): MutableList<PDFfile> {
        val pdfFiles = mutableListOf<PDFfile>()
        val projection = arrayOf(
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.MIME_TYPE,)
        val selection = "${MediaStore.Files.FileColumns.MIME_TYPE}=?"
        val selectionArgs = arrayOf("application/pdf")
        val sortOrder = "${MediaStore.Files.FileColumns.DATA} ASC"
        contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val filename = cursor.getString(0)
                pdfFiles.add(PDFfile(filename, false))
            }
        }
        return pdfFiles
    }
    override fun oncellclick(pdf: PDFfile) {
        startActivity(Intent(this, pdfview::class.java).apply {
            putExtra("name", pdf.pdfname)
        })
    }

    private fun uriFromFile(file: String): Uri {
        return FileProvider.getUriForFile(
            this,
            BuildConfig.APPLICATION_ID + ".fileProvider",
            File(file)
        )
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
}

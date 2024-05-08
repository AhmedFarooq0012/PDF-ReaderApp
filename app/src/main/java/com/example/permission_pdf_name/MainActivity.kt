package com.example.permission_pdf_name

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class MainActivity : AppCompatActivity(), Pdffileinterface {
    lateinit var button: Button
    lateinit var searchView: SearchView
    lateinit var rcview: RecyclerView
    lateinit var pdffilenamelist: MutableList<PDFfile>
    lateinit var datalistAdapter: pdfAdapter
    lateinit var checkall: CheckBox
    lateinit var gridshow: Button
    lateinit var btnsend: Button
    private var isGridLayout = false
    private var checkgrid: Boolean = false

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
        checkall = findViewById(R.id.checkall)
        searchView = findViewById(R.id.itemsearch)
        button = findViewById(R.id.topviewid)
        rcview = findViewById(R.id.recyclerview)
        btnsend = findViewById(R.id.sendall)
        gridshow = findViewById(R.id.searchid)

        val layoutManager = LinearLayoutManager(this)
//        rcview.layoutManager = layoutManager
        pdffilenamelist = getpdfname()
        datalistAdapter = pdfAdapter(pdffilenamelist, this)
//        rcview.layoutManager = LinearLayoutManager(this)
        rcview.layoutManager = layoutManager
        rcview.adapter = datalistAdapter
        checkall.setOnClickListener() {

            var a = datalistAdapter.newlist.size
            datalistAdapter.updateSelectAll(checkall.isChecked)
            datalistAdapter.dataList
        }
        // move one activity to another
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
        //activity move end
        button.setOnClickListener() {
            Log.e(TAG, "onCreate: my selected data is ${datalistAdapter.dataList}")
            Log.e(TAG, "onCreate: ${datalistAdapter.dataList.size}")
            var urilist = ArrayList<Uri>()
            for (file in datalistAdapter.dataList) {
                val uri = uriFromFile(file)
                urilist.add(uri)
            }
            if (urilist != null) {
                val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, urilist)
                shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                shareIntent.type = "*/*"
                startActivity(Intent.createChooser(shareIntent, "Share ..."))
            } else {
                Toast.makeText(this, "no data selected", Toast.LENGTH_SHORT).show()
            }
        }
        btnsend.setOnClickListener() {
            Log.e(TAG, "onCreate: list created and working")
            var list = ArrayList<Uri>()
            for (file in datalistAdapter.newlist) {
                val uri = uriFromFile(file)
                list.add(uri)
            }
            Log.e(TAG, "onCreate: data added in list ")
            if (list != null) {
                val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, list)
                shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                shareIntent.type = "*/*"
                startActivity(Intent.createChooser(shareIntent, "All file Share ..."))
                Log.e(TAG, "onCreate: sharefile is $list")
            } else {
                Toast.makeText(this, "no data selected", Toast.LENGTH_SHORT).show()
            }
        }

// searching

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                datalistAdapter.filter("${p0.orEmpty()}")
                return true
            }
        })
        // searching end
    }

    private fun getpdfname(): MutableList<PDFfile> {
        val pdfFiles = mutableListOf<PDFfile>()
        val projection = arrayOf(
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.MIME_TYPE,
        )
        val selection = "${MediaStore.Files.FileColumns.MIME_TYPE}=?"
        val selectionArgs = arrayOf("application/pdf")
        val sortoder = "${MediaStore.Files.FileColumns.DATA} ASC"
        contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            projection,
            selection,
            selectionArgs,
            sortoder
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

    fun uriFromFile(file: String): Uri {
        return FileProvider.getUriForFile(
            this,
            BuildConfig.APPLICATION_ID + ".fileProvider",
            File(file)
        )
    }
}
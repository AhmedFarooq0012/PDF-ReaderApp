package com.example.permission_pdf_name

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class pdfAdapter(
    private var names: MutableList<PDFfile>, private val oncellclick: Pdffileinterface
) :
    RecyclerView.Adapter<pdfAdapter.ViewHolder>() {
    private val originalList: MutableList<PDFfile> = mutableListOf()

    init {
        originalList.addAll(names)
    }

    fun filter(query: String) {
        names.clear()
        if (query.isEmpty()) {
            names.addAll(originalList)
        } else {
            for (item in originalList) {
                if (item.pdfname.contains(query, ignoreCase = true)) {
                    names.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflateview =
            LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(inflateview)
    }

    val dataList: MutableList<String> = mutableListOf()
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentpostion = names[position]
        val obj = File(currentpostion.pdfname)
        holder.textview.text = obj.name
        //check start
        currentpostion.check
        holder.checkbox.isChecked = currentpostion.check
        holder.checkbox.setOnClickListener() {
            names[position].check = holder.checkbox.isChecked
            if (currentpostion.check) {
                Log.e(TAG, "onBindViewHolder:your data is  add in list ")
                dataList.add(obj.path)
                Log.e(TAG, "onBindViewHolder: your data inthe list is ${dataList}")
            } else {
                names[position].check = holder.checkbox.isChecked
                dataList.remove(obj.path)
                Log.e(TAG, "onBindViewHolder: your remove dataitem form list:${dataList}")
                notifyDataSetChanged()
            }
            notifyDataSetChanged()
        }
        //check end
        holder.textview.setOnClickListener {
            oncellclick.oncellclick(names[position])
        }
    }

    var newlist: MutableList<String> = mutableListOf()
    fun updateSelectAll(checked: Boolean = true) {
        for (pdf in names) {
            val obj = File(pdf.pdfname)
            if (checked) {
                pdf.check = checked
                newlist.add(obj.name)
                Log.e(TAG, "updateSelectAll: data added in newlist ${pdf.pdfname}")
            } else {
                pdf.check = false
                newlist.clear()
                Log.e(TAG, "updateSelectAll: dataremove $newlist.size")
            }
        }
        notifyDataSetChanged()
        Log.e(TAG, "updateSelectAll: remaining data and size is ${newlist.size}")
    }

    override fun getItemCount(): Int {
        return names.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textview: TextView = itemView.findViewById(R.id.textview)
        var image: ImageView = itemView.findViewById(R.id.imageid)
        var checkbox: CheckBox = itemView.findViewById(R.id.checkboxid)
    }
}
package com.ppb2.kalfian.covidtracker.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ppb2.kalfian.covidtracker.R
import com.ppb2.kalfian.covidtracker.databinding.ListTestCovidBinding
import com.ppb2.kalfian.covidtracker.models.TestCovid
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TestCovidAdapter(onClick: AdapterTestCovidOnClickListener): RecyclerView.Adapter<TestCovidAdapter.ViewHolder>() {
    private var list = ArrayList<TestCovid>()
    private var onClickAdapter = onClick

    interface AdapterTestCovidOnClickListener {
        fun onItemClickListener(data: TestCovid)
    }

    inner class ViewHolder(itemView: View, onClickListener: AdapterTestCovidOnClickListener): RecyclerView.ViewHolder(itemView) {
        private val b = ListTestCovidBinding.bind(itemView)
        private var clickListener: AdapterTestCovidOnClickListener = onClickListener

        fun bind(v: TestCovid) {
            b.titleTestCovid.text = v.title

            val date = Date(v.valid_date.toLong() * 1000)
            Log.d("TIMESTAMP ADAPTER", v.valid_date.toLong().toString())
            val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.getDefault())
            val formattedDate = simpleDateFormat.format(date)
            b.validUntilTestCovid.text = "Berlaku hingga $formattedDate"

            itemView.setOnClickListener {
                clickListener.onItemClickListener(list[adapterPosition])
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_test_covid, parent, false)
        return ViewHolder(v, onClickAdapter)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addList(items: List<TestCovid>) {
        list.addAll(items)
        notifyDataSetChanged()
    }

    fun add(item: TestCovid) {
        list.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }
}
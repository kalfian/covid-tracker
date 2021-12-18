package com.ppb2.kalfian.covidtracker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ppb2.kalfian.covidtracker.R
import com.ppb2.kalfian.covidtracker.databinding.ListCheckinHistoryBinding
import com.ppb2.kalfian.covidtracker.models.CheckInHistory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CheckInHistoryAdapter(onClick: AdapterCheckInHistoryOnClickListener): RecyclerView.Adapter<CheckInHistoryAdapter.ViewHolder>() {
    private var list = ArrayList<CheckInHistory>()
    private var onClickAdapter = onClick

    interface AdapterCheckInHistoryOnClickListener {
        fun onBtnClickListener(data: CheckInHistory)
        fun onItemClickListener(data: CheckInHistory)
    }

    inner class ViewHolder(itemView: View, onClickListener: AdapterCheckInHistoryOnClickListener): RecyclerView.ViewHolder(itemView) {
        private val b = ListCheckinHistoryBinding.bind(itemView)
        private var clickListener: AdapterCheckInHistoryOnClickListener = onClickListener

        fun bind(v: CheckInHistory) {
            b.title.text = v.place_name
            var checkout = ""

            if(v.checkout_timestamp != 0.0) {
                b.button.visibility = View.GONE
                val checkOutData = Date(v.checkout_timestamp.toLong() * 1000)
                val checkOutFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                checkout = "- " + checkOutFormat.format(checkOutData)
            } else {
                b.button.visibility = View.VISIBLE
            }

            val date = Date(v.checkin_timestamp.toLong() * 1000)
            val simpleDateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            val checkIn = simpleDateFormat.format(date)
            b.masuk.text = "Masuk $checkIn $checkout"

            itemView.setOnClickListener {
                clickListener.onItemClickListener(list[adapterPosition])
            }

            b.button.setOnClickListener{
                clickListener.onBtnClickListener(list[adapterPosition])
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_checkin_history, parent, false)
        return ViewHolder(v, onClickAdapter)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addList(items: List<CheckInHistory>) {
        list.addAll(items)
        notifyDataSetChanged()
    }

    fun add(item: CheckInHistory) {
        list.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }
}
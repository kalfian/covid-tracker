package com.ppb2.kalfian.covidtracker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ppb2.kalfian.covidtracker.R
import com.ppb2.kalfian.covidtracker.databinding.ListVaccineCertBinding
import com.ppb2.kalfian.covidtracker.models.VaccineCert
import com.squareup.picasso.Picasso
import kotlin.collections.ArrayList

class VaccineCertAdapter(onClick: AdapterVaccineCertOnClickListener): RecyclerView.Adapter<VaccineCertAdapter.ViewHolder>() {
    private var list = ArrayList<VaccineCert>()
    private var onClickAdapter = onClick

    interface AdapterVaccineCertOnClickListener {
        fun onItemClickListener(data: VaccineCert)
    }

    inner class ViewHolder(itemView: View, onClickListener: AdapterVaccineCertOnClickListener): RecyclerView.ViewHolder(itemView) {
        private val b = ListVaccineCertBinding.bind(itemView)
        private var clickListener: AdapterVaccineCertOnClickListener = onClickListener

        fun bind(v: VaccineCert) {
            b.vaccineTitle.text = v.title
            val image = v.image

            Picasso.get()
                .load(image)
                .placeholder(R.drawable.logo)
                .into(b.vaccineImage)

            itemView.setOnClickListener {
                clickListener.onItemClickListener(list[adapterPosition])
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_vaccine_cert, parent, false)
        return ViewHolder(v, onClickAdapter)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addList(items: List<VaccineCert>) {
        list.addAll(items)
        notifyDataSetChanged()
    }

    fun add(item: VaccineCert) {
        list.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }
}
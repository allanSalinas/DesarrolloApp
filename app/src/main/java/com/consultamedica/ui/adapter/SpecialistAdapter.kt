package com.consultamedica.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.consultamedica.R
import com.consultamedica.model.Specialist

class SpecialistAdapter(private val specialists: List<Specialist>) : RecyclerView.Adapter<SpecialistAdapter.SpecialistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_specialist, parent, false)
        return SpecialistViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpecialistViewHolder, position: Int) {
        val specialist = specialists[position]
        holder.bind(specialist)
    }

    override fun getItemCount(): Int = specialists.size

    class SpecialistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tvSpecialistName)
        private val specialtyTextView: TextView = itemView.findViewById(R.id.tvSpecialty)

        fun bind(specialist: Specialist) {
            nameTextView.text = specialist.name
            specialtyTextView.text = specialist.specialty
        }
    }
}
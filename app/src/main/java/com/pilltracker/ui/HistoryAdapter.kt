package com.pilltracker.ui

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pilltracker.R
import com.pilltracker.data.MedicationRecord
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    private var records: List<MedicationRecord> = emptyList()

    fun submitList(list: List<MedicationRecord>) {
        records = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(records[position])

    override fun getItemCount() = records.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val dot: ImageView = view.findViewById(R.id.status_dot)
        private val dateText: TextView = view.findViewById(R.id.date_text)
        private val statusText: TextView = view.findViewById(R.id.status_text)
        private val formatter = DateTimeFormatter.ofPattern("EEE, MMM d")

        fun bind(record: MedicationRecord) {
            dateText.text = LocalDate.parse(record.date).format(formatter)
            if (record.taken) {
                dot.setColorFilter(0xFF4CAF50.toInt(), PorterDuff.Mode.SRC_IN)
                statusText.text = itemView.context.getString(R.string.status_taken)
            } else {
                dot.setColorFilter(0xFF9E9E9E.toInt(), PorterDuff.Mode.SRC_IN)
                statusText.text = itemView.context.getString(R.string.status_not_taken)
            }
        }
    }
}

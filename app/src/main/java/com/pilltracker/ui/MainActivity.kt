package com.pilltracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pilltracker.R
import com.pilltracker.data.MedicationDatabase
import com.pilltracker.data.MedicationRecord
import com.pilltracker.data.MedicationRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = HistoryAdapter()
        val list = findViewById<RecyclerView>(R.id.history_list)
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

        val repo = MedicationRepository(
            MedicationDatabase.getInstance(this).medicationDao()
        )
        lifecycleScope.launch {
            repo.getAll().collectLatest { records ->
                adapter.submitList(records)
            }
        }
    }
}

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    private var records: List<MedicationRecord> = emptyList()

    fun submitList(list: List<MedicationRecord>) {
        records = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(records[position])
    }

    override fun getItemCount() = records.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val dot: View = view.findViewById(R.id.status_dot)
        private val dateText: TextView = view.findViewById(R.id.date_text)
        private val statusText: TextView = view.findViewById(R.id.status_text)

        private val formatter = DateTimeFormatter.ofPattern("EEE, MMM d")

        fun bind(record: MedicationRecord) {
            val date = LocalDate.parse(record.date)
            dateText.text = date.format(formatter)
            if (record.taken) {
                dot.setBackgroundColor(0xFF4CAF50.toInt())
                statusText.text = itemView.context.getString(R.string.status_taken)
            } else {
                dot.setBackgroundColor(0xFF9E9E9E.toInt())
                statusText.text = itemView.context.getString(R.string.status_not_taken)
            }
        }
    }
}

package com.pilltracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pilltracker.R
import com.pilltracker.data.MedicationDatabase
import com.pilltracker.data.MedicationRepository
import com.pilltracker.util.HistoryStats
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {
    private lateinit var adapter: HistoryAdapter

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            title = getString(R.string.app_name)
        }
        activity?.invalidateOptionsMenu()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_history, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = HistoryAdapter()
        view.findViewById<RecyclerView>(R.id.history_list).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HistoryFragment.adapter
        }

        val streakView = view.findViewById<TextView>(R.id.streak_count)
        val weekView = view.findViewById<TextView>(R.id.week_count)

        val repo = MedicationRepository(
            MedicationDatabase.getInstance(requireContext()).medicationDao()
        )
        viewLifecycleOwner.lifecycleScope.launch {
            repo.getAll().collectLatest { records ->
                adapter.submitList(records)
                streakView.text = HistoryStats.streak(records).toString()
                val (taken, total) = HistoryStats.weekSummary(records)
                weekView.text = "$taken/$total"
            }
        }
    }
}

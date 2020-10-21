package com.example.pm_22689.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pm_22689.R
import com.example.pm_22689.entities.Notes

class NotesAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var notes = emptyList<Notes>() // Cached copy of words

    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notesItemView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return NotesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val current = notes[position]
        holder.notesItemView.text = current.text
    }

    internal fun setWords(notes: List<Notes>) {
        this.notes = notes
        notifyDataSetChanged()
    }

    override fun getItemCount() = notes.size
}

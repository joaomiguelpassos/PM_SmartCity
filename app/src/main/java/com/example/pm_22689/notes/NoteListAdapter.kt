package com.example.pm_22689.notes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pm_22689.R
import com.example.pm_22689.database.Notes

class NoteListAdapter internal constructor(context: Context, var clickListener: OnNoteItemClickListener) : RecyclerView.Adapter<NoteListAdapter.NotesViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var notes = emptyList<Notes>() // Cached copy of words

    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notesDate: TextView = itemView.findViewById(R.id.textView)
        val notesMessage: TextView = itemView.findViewById(R.id.textView1)

        fun initialize(item: Notes, action: OnNoteItemClickListener) {
            notesDate.text = item.noteDate
            notesMessage.text = item.noteMessage

            itemView.setOnClickListener {
                action.onItemClick(item, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return NotesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        //val current = notes[position]
        //holder.notesDate.text = current.noteDate
        //holder.notesMessage.text = current.noteMessage
        holder.initialize(notes.get(position), clickListener)
    }

    internal fun setNotes(notes: List<Notes>) {
        this.notes = notes
        notifyDataSetChanged()
    }

    override fun getItemCount() = notes.size

    // ADICIONEI ISTO AQUI
    fun getNoteAtPosition(position: Int): Notes {
        return notes[position]
    }
}

interface OnNoteItemClickListener{
    fun onItemClick(item: Notes, position: Int)
}
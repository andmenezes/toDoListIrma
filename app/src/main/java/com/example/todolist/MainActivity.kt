package com.example.todolist

import android.app.Application
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Database

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class NotesApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    /* Precisa criar esse aplication aqui para passar ele para o VIewModel na linha 41 por meio do ViewModelFactory
       PRECISA IR NO MANIFEST e adicionar a TAG android:name=".NotesApplication" com o mesmo nome dessa classe pra funfar! Não me pergunte o pq!
    */
    private val database by lazy { DataBase.getDatabase(this) }

    val userRepository by lazy { Repository(database.dao()) }
}

class MainActivity : AppCompatActivity() {

    private val noteViewModel: NoteViewModel by viewModels {

        /** NoteViewModelFactory foi criado na classe da ViewModel! copiado da internet
         * essa aqui é uma Val que é auto instanciada em swift chamamos isso de computed var
         */
        NoteViewModelFactory((application as NotesApplication).userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = NoteListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        /** Adicionado um observer no LiveData faz com que a magia aconteça ele fica dando o fetch no banco de dados quando o metodo onChange() é disparado!
         * quem dispara o metodo onChanged? não sei, não fica claro, indiano nenhum consegue explicar isso mas ele é disparado quando se faz alteracao no banco de dados
         */
        noteViewModel.allNotes.observe(this) { words ->
            // Update the cached copy of the words in the adapter.
            words.let { adapter.submitList(it) }
        }

        floatingActionButton.setOnClickListener {
            showAddNoteDialog()
        }
    }

    private fun showAddNoteDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_add_new_note)
        dialog.setCancelable(true)

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT

        val etNoteTitle: EditText = dialog.findViewById(R.id.etNoteTitle)
        val etNoteDescription:EditText = dialog.findViewById(R.id.etNoteDescription)

        dialog.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.btAddNote).setOnClickListener {
            if (inputCheck(etNoteTitle.text.toString(), etNoteDescription.text.toString())) {

                val notes = Notes(0, etNoteTitle.text.toString(), etNoteDescription.text.toString())
                noteViewModel.addData(notes)
                Toast.makeText(this, "Data added", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter data", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
        dialog.window!!.attributes = layoutParams
    }

    private fun inputCheck(noteTitle: String, noteDescription: String): Boolean {
        return !(TextUtils.isEmpty(noteTitle) && TextUtils.isEmpty(noteDescription))
    }
}

/** Essa classe aqui é a responsavel por ligar os dados dfo banco de dados com a tela não precisa ficar na mainactive recomendo criar um arquivo chanado
 * NoteListAdapter para deixar ele mais facil! é nele que vamos mexer quando quisermos adicionar mais campos a tela! Mexer com cautela
 * Sendo bem sincero precisaria de mais uns dois dias para entender o que rola entre esse adapter e a lista da main active. 
 */

class NoteListAdapter : ListAdapter<Notes, NoteListAdapter.NoteViewHolder>(NoteComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.noteTitle + " - " + current.noteDescription )
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val wordItemView: TextView = itemView.findViewById(R.id.textView)

        fun bind(text: String?) {
            wordItemView.text = text
        }

        companion object {
            fun create(parent: ViewGroup): NoteViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return NoteViewHolder(view)
            }
        }
    }

    class NoteComparator : DiffUtil.ItemCallback<Notes>() {
        override fun areItemsTheSame(oldItem: Notes, newItem: Notes): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Notes, newItem: Notes): Boolean {
            return oldItem.noteTitle == newItem.noteTitle
        }
    }
}
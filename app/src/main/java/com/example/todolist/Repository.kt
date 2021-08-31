package com.example.todolist

import androidx.room.Database
import kotlinx.coroutines.flow.Flow


class Repository(private val dao: Dao) {

    /** Eh essa magia aqui de Flow que fica dando o fetch no banco de dados! Não entendi como funcionar
     * assumo que seja magia.
     */
    val allNotes: Flow<List<Notes>> = dao.getAllNotes()

    suspend fun addData(notes: Notes){
        dao.addData(notes)
    }

    /** não é necessario criar essa funcao não importa em quantos sites de tutoriais essa funcao apareca
     */
//    suspend fun getAllNotes(){
//        dao.getAllNotes()
//    }
}
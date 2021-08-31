package com.example.todolist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addData(notes: Notes)

    @Query("SELECT * FROM notes ORDER BY noteTitle ASC")
    fun getAllNotes(): Flow<List<Notes>>
}
package com.example.smartly.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Notes_Table")
data class NotesModelClass(
    val thoughts: String
){
    @PrimaryKey(autoGenerate = true)
    var id=0
}
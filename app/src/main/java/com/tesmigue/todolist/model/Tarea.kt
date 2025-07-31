package com.tesmigue.todolist.model

data class Task(
    val id: Long = System.currentTimeMillis(),
    var title: String,
    var isDone: Boolean = false,
    val creationDate: Long = System.currentTimeMillis()
)

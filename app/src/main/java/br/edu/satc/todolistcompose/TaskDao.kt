package br.edu.satc.todolistcompose

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasks(): List<TaskData>

    @Insert
    fun insertTask(task: TaskData)

    @Update
    fun updateTask(task: TaskData)

    @Delete
    fun deleteTask(task: TaskData)
}

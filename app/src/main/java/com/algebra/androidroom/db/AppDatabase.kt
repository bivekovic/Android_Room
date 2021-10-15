package com.algebra.androidroom.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.algebra.androidroom.dao.ToDoDao
import com.algebra.androidroom.model.ToDo

@Database( entities = arrayOf( ToDo::class ), version=1 )
abstract class AppDatabase : RoomDatabase( ) {

    abstract fun toDoDao( ): ToDoDao

    companion object {

    }
}
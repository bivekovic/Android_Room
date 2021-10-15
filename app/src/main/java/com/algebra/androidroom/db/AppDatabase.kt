package com.algebra.androidroom.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.algebra.androidroom.dao.ToDoDao
import com.algebra.androidroom.model.ToDo

@Database( entities = arrayOf( ToDo::class ), version=1 )
abstract class AppDatabase : RoomDatabase( ) {

    abstract fun toDoDao( ): ToDoDao


    companion object {
        @Volatile private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke( context : Context )= instance ?: synchronized( LOCK ){
            instance ?: buildDatabase( context ).also { instance = it}
        }

        private fun buildDatabase( context : Context) =
                            Room
                                .databaseBuilder( context, AppDatabase::class.java, "todo-list.db" )
//                              .allowMainThreadQueries( )
                                .build( )
    }
}
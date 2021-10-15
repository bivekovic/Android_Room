package com.algebra.androidroom.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.algebra.androidroom.model.ToDo

@Dao
interface ToDoDao {
/*
    @Query( "SELECT * FROM todo_items" )
    fun getAll( ) : List< ToDo >
*/

    @Query( "SELECT * FROM todo_items" )
    fun getAll( ) : LiveData< List< ToDo > >

    @Query( "SELECT * FROM todo_items WHERE id=:toDoId" )
    fun loadById( toDoId : Int ) : ToDo

    @Query("SELECT * FROM todo_items WHERE id IN(:toDoIds)" )
    fun loadAllByIds( toDoIds : IntArray ) : List< ToDo >


    @Insert
    fun insertAll( vararg todos: ToDo )

    @Delete
    fun delete( todos : ToDo )

    @Query( "DELETE FROM todo_items" )
    fun deleteAll( )

}
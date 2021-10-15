package com.algebra.androidroom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.algebra.androidroom.db.AppDatabase
import com.algebra.androidroom.model.ToDo
import com.algebra.androidroom.ui.ToDosAdapter
import com.algebra.androidroom.ui.Wiper
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

enum class ToDoEditTexts {
    TITLE,
    DESCRIPTION
}

class MainActivity : AppCompatActivity( ) {

    private val TAG = "MainActivity"
    private lateinit var db      : AppDatabase
    private lateinit var adapter : ToDosAdapter

    override fun onCreate( savedInstanceState : Bundle? ) {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.activity_main )

        db = Room
                .databaseBuilder( applicationContext, AppDatabase::class.java, "todos.db" )
                .allowMainThreadQueries( )
                .build( )

        Log.i( TAG, this.toString( ) )

        setupRecyclerView( )
        setupListeners( )
    }

    private fun setupListeners( ) {
        bInsert.setOnClickListener {
            val title = etTitle.text.toString( )
            val desc  = etDescription.text.toString( )

            val validationResult = validateFields( title, desc )
            if( validationResult.isNotEmpty( ) ) {
                markErrorFields( validationResult )
            } else {
                insertTodo( title, desc )
                etTitle.text.clear( )
                etDescription.text.clear( )
                etTitle.requestFocus( )
                Toast
                    .makeText( this, "Success", Toast.LENGTH_SHORT )
                    .show( )
            }
        }
    }

    private fun setupRecyclerView( ) {
        rvTodos.layoutManager = LinearLayoutManager( this )
        adapter = ToDosAdapter( object : Wiper {
            override fun delete( todo: ToDo ) {
                this@MainActivity.delete( todo.id!! )
            }
        } )
        rvTodos.adapter = adapter
        adapter.todos = db.toDoDao( ).getAll( )
    }

    fun delete( id : Int ) {
        db.toDoDao( ).delete( ToDo( id, null, null ) )
        adapter.todos = db.toDoDao( ).getAll( )
    }

    private fun insertTodo( title : String, desc : String ) {
        db.toDoDao( ).insertAll( ToDo( null, title, desc ) )
        adapter.todos = db.toDoDao( ).getAll( )
    }


    fun validateFields( title : String, description : String ) : Map< ToDoEditTexts, Boolean > {

        val fieldsMap = EnumMap< ToDoEditTexts, Boolean >( ToDoEditTexts::class.java )

        if( title.isEmpty( ) )
            fieldsMap[ ToDoEditTexts.TITLE ] = false
        if( description.isEmpty( ) )
            fieldsMap[ ToDoEditTexts.DESCRIPTION ] = false

        return fieldsMap
    }

    private fun markErrorFields( validationResult: Map< ToDoEditTexts, Boolean > ) {
        if( validationResult.containsKey( ToDoEditTexts.TITLE ) )
            etTitle.error = "Title missing"
        if( validationResult.containsKey( ToDoEditTexts.DESCRIPTION ) )
            etDescription.error = "Description missing"
    }

    override fun onCreateOptionsMenu( menu: Menu? ) : Boolean {
        menuInflater.inflate( R.menu.main_menu, menu )
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) : Boolean {
        if( item.itemId==R.id.brisiSve ) {
            db.toDoDao( ).deleteAll( )
            adapter.todos = db.toDoDao( ).getAll( )
        }
        return true
    }
}
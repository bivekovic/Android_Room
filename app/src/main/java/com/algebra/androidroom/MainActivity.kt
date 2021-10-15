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

        Log.i( TAG, "onCreate: Prije kreiranja baze" )

        db = AppDatabase( this )

        Log.i( TAG, "onCreate: Nakon kreiranja baze" )

        setupRecyclerView( )

        Log.i( TAG, "onCreate: Postavljen RecyclerView" )

        setupListeners( )

        Log.i( TAG, "onCreate: Postavljeni Listeneri" )
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
        Log.i( TAG, "setupRecyclerView: Postavljen Layout Manager" )
        adapter = ToDosAdapter( object : Wiper {
            override fun delete( todo: ToDo ) {
                this@MainActivity.delete( todo.id!! )
            }
        } )
        Log.i( TAG, "setupRecyclerView: Kreiran Adapter" )
        rvTodos.adapter = adapter
        Log.i( TAG, "setupRecyclerView: Postavljen Adapter" )
        val t = Thread {
            adapter.todos = db.toDoDao( ).getAll( )
            Log.i( TAG, "setupRecyclerView.NewThread: Dohvat podataka uspješno završio" )
        }
        t.start( )
        Log.i( TAG, "setupRecyclerView: RecyclerView postavljen" )
    }

    fun delete( id : Int ) {
        Thread {
            val dao = db.toDoDao( )
            dao.delete( ToDo( id, null, null ) )
            val novaLista = db.toDoDao( ).getAll( )
            runOnUiThread {
                adapter.todos = novaLista
            }
        }.start( )
    }

    private fun insertTodo( title : String, desc : String ) {
        Thread {
            val dao = db.toDoDao( )
            dao.insertAll( ToDo( null, title, desc ) )
            val novaLista = db.toDoDao( ).getAll( )
            runOnUiThread {
                adapter.todos = novaLista
            }
        }.start( )
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
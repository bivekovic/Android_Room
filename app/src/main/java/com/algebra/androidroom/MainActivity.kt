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
import com.algebra.androidroom.db.AppExecutors
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

        db = AppDatabase( this )
        setupRecyclerView( )
        setupListeners( )

        observeDatabaseChanges( )
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

        /*
        AppExecutors.instance?.diskIO( )?.execute {
            adapter.todos = db.toDoDao( ).getAll( )
        }
         */
    }

    fun delete( id : Int ) {
        AppExecutors.instance?.diskIO( )?.execute {
            db.toDoDao( ).delete( ToDo( id, null, null ) )
        }
        /*
        //Thread {
        AppExecutors.instance?.diskIO( )?.execute {
            Log.i( TAG, "delete: Brisem jedan ToDo" )
            val dao = db.toDoDao( )
            dao.delete( ToDo( id, null, null ) )
            val novaLista = db.toDoDao( ).getAll( )
            runOnUiThread {
                adapter.todos = novaLista
            }
            Log.i( TAG, "delete: ToDo jedan obrisan" )
        }
        //}.start( )
         */
    }

    private fun insertTodo( title : String, desc : String ) {
        AppExecutors.instance?.diskIO( )?.execute {
            db.toDoDao( ).insertAll( ToDo( null, title, desc ) )
        }
        /*
        AppExecutors.instance?.diskIO( )?.execute {
        //Thread {
            Log.i( TAG, "insertTodo: Upisujem novi ToDo" )
            val dao = db.toDoDao( )
            dao.insertAll( ToDo( null, title, desc ) )
            val novaLista = db.toDoDao( ).getAll( )
            runOnUiThread {
                adapter.todos = novaLista
            }
            Log.i( TAG, "insertTodo: Novi ToDo upisan" )
        }
        //}.start( )
        */
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

    override fun onOptionsItemSelected( item: MenuItem ) : Boolean {
        if( item.itemId==R.id.brisiSve ) {
            AppExecutors.instance?.diskIO( )?.execute {
                db.toDoDao( ).deleteAll( )
            }
            /*
            AppExecutors.instance?.diskIO( )?.execute {
            //Thread {
                Log.i( TAG, "onOptionsItemSelected: Brišem sve ToDo-ove" )
                val dao = db.toDoDao( )
                dao.deleteAll( )
                val novaLista = db.toDoDao( ).getAll( )
                runOnUiThread {
                    adapter.todos = novaLista
                }
                Log.i( TAG, "onOptionsItemSelected: Svi ToDo-ovi obrisani" )
            //}.start( )
            }
            */
        }
        return true
    }

    private fun observeDatabaseChanges( ) {
        db.toDoDao( ).getAll( ).observe( this, { adapter.todos = it } )
    }
}
package com.algebra.androidroom.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.algebra.androidroom.R
import com.algebra.androidroom.model.ToDo
import kotlinx.android.synthetic.main.todo.view.*

interface Wiper {
    fun delete( todo : ToDo )
}

class ToDosAdapter( val wiper : Wiper ) : RecyclerView.Adapter< ToDosViewHolder >( ) {

    var todos : List< ToDo > = mutableListOf( )
        set( value ) {
            field = value
            notifyDataSetChanged( )
        }

    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int ): ToDosViewHolder {

        return ToDosViewHolder(
                    LayoutInflater
                        .from( parent.context )
                        .inflate( R.layout.todo, parent, false )
        )
    }

    override fun onBindViewHolder( holder: ToDosViewHolder, position: Int ) {
        holder.bind( todos[position], wiper )
        holder.itemView.setBackgroundColor( Color.parseColor( if( position%2==0 ) "#F5F1FF" else "#FFF5F1" ) )
    }

    override fun getItemCount( ): Int {
        return todos.size
    }
}

class ToDosViewHolder( val view: View ) : RecyclerView.ViewHolder( view ) {

    val tvTitle       = view.tvTitle
    val tvDescription = view.tvDescription

    fun bind( item : ToDo, wiper : Wiper ) {
        tvTitle.text       = item.title
        tvDescription.text = item.description
        itemView.setOnClickListener {
            wiper.delete( item )
        }
    }
}
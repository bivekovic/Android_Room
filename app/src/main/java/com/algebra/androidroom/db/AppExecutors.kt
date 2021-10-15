package com.algebra.androidroom.db

import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutors private constructor( private val diskIO : Executor, private val networkIO : Executor ) {

    fun diskIO( ) : Executor {
        return diskIO
    }

    fun networkIO( ) : Executor {
        return networkIO
    }

    companion object {
        private val LOCK = Any( )
        private var sInstance : AppExecutors? = null

        val instance : AppExecutors?
            get( ) {
                if( sInstance==null ) {
                    synchronized( LOCK ) {
                        sInstance = AppExecutors( Executors.newSingleThreadExecutor( ), Executors.newFixedThreadPool( 3 ) )
                    }
                }
                return sInstance
            }
    }
}
package com.azerion.bluestack.demo.kotlin

import android.util.Log

/**
 * Simple logger utility for the Bluestack demo app.
 * Provides consistent logging across all components.
 */
object Logger {
    private const val DEFAULT_TAG = "BluestackDemo"
    
    /**
     * Log debug message
     */
    fun d(tag: String = DEFAULT_TAG, message: String) {
        Log.d(tag, message)
    }
    
    /**
     * Log info message
     */
    fun i(tag: String = DEFAULT_TAG, message: String) {
        Log.i(tag, message)
    }
    
    /**
     * Log warning message
     */
    fun w(tag: String = DEFAULT_TAG, message: String) {
        Log.w(tag, message)
    }
    
    /**
     * Log error message
     */
    fun e(tag: String = DEFAULT_TAG, message: String) {
        Log.e(tag, message)
    }
    
    /**
     * Log error message with exception
     */
    fun e(tag: String = DEFAULT_TAG, message: String, throwable: Throwable) {
        Log.e(tag, message, throwable)
    }
}
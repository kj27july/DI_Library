package net.di.lib.controllers

import android.util.Log
import net.di.lib.collector.TasksHelper

object AppController {
    val TAG = "akshay"

    fun getJsonSchema(): String {
        Log.d(TAG, "getJsonSchema: ")
        val task = TasksHelper.createTask(null)
        task.onSchemaRequest()
        return task.onExecute()
    }

    fun subscribe(pkg: String?, config: String?) {
        Log.d(TAG, "subscribe: ")
        SubscriptionController().processSubscription(pkg!!, config!!)
    }
}
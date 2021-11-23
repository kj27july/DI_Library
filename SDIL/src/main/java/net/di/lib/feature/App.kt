package net.di.lib.feature

import android.annotation.SuppressLint
import android.content.Context
import net.generic.ipc.IResponse
import net.di.lib.interfaces.IFeature
import net.di.lib.models.Property
import org.json.JSONObject

class App {
    lateinit var context: Context
    lateinit var iFeature: IFeature
    var version: Int = 0
    lateinit var properties: List<Property>
    var consumers = mutableMapOf<String, IResponse>()
    lateinit var jsonSchema: JSONObject
    var subscribers = mutableMapOf<String, List<String>>()

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var instance: App

        fun getInstance(): App {
            if (!this::instance.isInitialized) {
                instance = App()
            }
            return instance
        }
    }
}
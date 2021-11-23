package net.di.lib.controllers

import android.util.Log
import net.di.lib.feature.BaseSchema
import net.di.lib.feature.App
import net.di.lib.utils.FileUtil

class SchemaBuilder() : BaseSchema() {
    private val TAG = "akshay"

    fun buildJSON(app: App) {
        app.jsonSchema = super.getSchema(app.properties)
        Log.d(TAG, "buildJSON: ${app.jsonSchema}")
        saveSchema(app)
    }

    private fun saveSchema(app: App) {
        val success = FileUtil(app.context).writeFile(
            "schema_V" + app.version + ".json",
            app.jsonSchema.toString(),
            false
        )
        Log.d(TAG, "saveSchema: $success")
    }
}
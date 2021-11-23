package net.di.lib.controllers

import android.content.Context
import net.di.lib.feature.App
import net.di.lib.interfaces.IFeature
import net.di.lib.models.Property

abstract class FeatureDirectory {
    abstract fun version(version: Int): FeatureDirectory
    abstract fun advertiseDataList(dataList: List<Property>): FeatureDirectory
    abstract fun build(iFeature: IFeature): FeatureDirectory

    class Builder(private val context: Context) : FeatureDirectory() {
        private val app = App.getInstance()

        override fun version(version: Int): FeatureDirectory {
            if (version > 0) {
                app.version = version
            } else {
                throw Exception("Version should be greater than 0")
            }
            return this
        }

        override fun advertiseDataList(dataList: List<Property>): FeatureDirectory {
            app.properties = dataList.distinctBy { it.name }
            return this
        }

        override fun build(iFeature: IFeature): FeatureDirectory {
            app.context = context
            app.iFeature = iFeature
            SchemaBuilder().buildJSON(app)
            return this
        }
    }
}
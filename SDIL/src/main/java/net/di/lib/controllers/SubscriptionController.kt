package net.di.lib.controllers

import android.util.Log
import net.di.lib.feature.BaseSchema
import net.di.lib.feature.App
import org.json.JSONObject

class SubscriptionController : BaseSchema() {
    private val TAG = "akshay"

    fun processSubscription(pkg: String, config: String) {
        Log.d(TAG, "processSubscription: ")
        val oldFeaturesList = getRequiredFeaturesList()
        val subscribersMap = App.getInstance().subscribers
        subscribersMap[pkg] = JSONObject(config).keys().asSequence().toList()
        val newFeaturesList = getRequiredFeaturesList()

        for (feature in oldFeaturesList.filterNot { newFeaturesList.contains(it) }) {
            initFeature(feature, false)
        }
        for (feature in newFeaturesList.filterNot { oldFeaturesList.contains(it) }) {
            initFeature(feature, true)
        }
    }

    private fun initFeature(feature: String, start: Boolean) {
        val featureDetails = super.getFeatureDetails(App.getInstance().jsonSchema, feature)
        if (featureDetails != null && super.isEventFeature(featureDetails)) {
            if (start)
                App.getInstance().iFeature.startEvent(feature)
            else
                App.getInstance().iFeature.stopEvent(feature)
        }
    }

    private fun getRequiredFeaturesList(): ArrayList<String> {
        val subscribersMap = App.getInstance().subscribers
        val featuresList = ArrayList<String>()
        for (value in subscribersMap.values) {
            featuresList.addAll(value)
        }
        return if (featuresList.isNotEmpty()) featuresList.distinct() as ArrayList else featuresList
    }
}
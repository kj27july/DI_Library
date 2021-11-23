package net.di.lib.models

import net.di.lib.utils.FeatureType
import net.di.lib.utils.ReturnType

data class Property(
    val name: String,
    val returnType: ReturnType,
    val description: String = "",
    val featureType: FeatureType = FeatureType.UNKNOWN
) {
    var args = ArrayList<Property>()
    var arrayItem: ReturnType? = null
    var objectItems = ArrayList<Property>()
}
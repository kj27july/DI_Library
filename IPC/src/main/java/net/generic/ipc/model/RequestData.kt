package net.generic.ipc.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RequestData(var key: String, var input1: String) : Parcelable {}
package com.practical.mydatamachine.ui.post.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.databinding.BaseObservable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Posts(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("userId") val userId: Int? = null,
    @SerializedName("body") val body: String? = null
) : Parcelable, BaseObservable()


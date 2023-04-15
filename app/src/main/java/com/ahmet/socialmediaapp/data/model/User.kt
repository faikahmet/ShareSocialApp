package com.ahmet.socialmediaapp.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.*

@Parcelize
data class User(
    var userId: String,
    var userName:String,
    var userEmail:String,
    var userRegisterDate: @RawValue Any? = null,
   // val userRegisterDate:String,
    var userLogo:String
): Parcelable
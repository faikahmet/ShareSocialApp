package com.ahmet.socialmediaapp.data.model

import android.os.Parcelable
import com.google.firebase.firestore.FieldValue
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

data class Post(
    val postId:String,
    val postContent:String,
    val postPhoto:String,
    val postUserId:String,
    var postDate: Any? = null,
    var user:User?=null,//bunu sonra sorguda sonradan ekle!!
    var postLike:PostLike?=null,//bunu sonra sorguda sonradan ekle!!
    var postLikeSize:Int?=null//bunu sonra sorguda sonradan ekle!!
)
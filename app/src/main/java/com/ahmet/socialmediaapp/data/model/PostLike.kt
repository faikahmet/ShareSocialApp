package com.ahmet.socialmediaapp.data.model

data class PostLike(
    val postId:String,
    val postLikeUserId:String?,
    val postLikeId:String="0"//!
)
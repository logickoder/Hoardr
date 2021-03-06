package com.jeffreyorazulike.hoardr.models

data class ChatHeadModel (
    var name: String? = null,
    var imageUrl: String? = null,
    var numberOfMessages: Int = 1,
    var lastMessage: String = "",
    var timeOfLastMessage: String? = null
){}

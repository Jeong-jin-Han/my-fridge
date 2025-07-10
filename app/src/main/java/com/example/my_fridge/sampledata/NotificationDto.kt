package com.example.my_fridge.sampledata

import com.example.my_fridge.notification.NotificationType

data class NotificationDto (
    val id: Int,
    val type: NotificationType,
    val message: String,
    val targetId: Int, // 이동 대상 ID
    var clicked: Boolean
)
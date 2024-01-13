package com.example.smartgarden.objects

sealed class GardenKeys(val key : String) {
    data object Temperatures : GardenKeys("temperatures")
}
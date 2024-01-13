package com.example.smartgarden.objects

sealed class DatabaseEntry(val key : String) {
    data object Raspberry : DatabaseEntry("raspberry")
    data object Garden : DatabaseEntry("gardens")
    data object User : DatabaseEntry("users")
}
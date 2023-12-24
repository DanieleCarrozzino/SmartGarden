package com.example.smartgarden.firebase.storage

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseRealTimeDatabaseImpl : FirebaseRealTimeDatabase {

    /* Main object */
    val database = FirebaseDatabase.getInstance("https://smartgarden-d7604-default-rtdb.europe-west1.firebasedatabase.app/")

    override fun insertNode(module : String, childs : List<String>, node : String) : String{
        // build the path
        val ref = getReference(module, childs)

        // Create the space
        val newRef = ref.push()
        // get the id of the new node
        val key = newRef.key
        // add value
        newRef.setValue(node)

        // return the key to save or to use
        return key ?: ""
    }

    override fun insertForceNode(module : String, childs : List<String>, node : String){
        // build the path
        val ref = getReference(module, childs)

        //Insert node
        ref.setValue(node)
    }

    override fun getNodeReference(module : String, childs : List<String>) : DatabaseReference {
        return getReference(module, childs)
    }

    private fun getReference(module : String, childs : List<String>) : DatabaseReference{
        var ref = database.getReference(module)
        for(child in childs){
            ref = ref.child(child)
        }
        return ref
    }

}
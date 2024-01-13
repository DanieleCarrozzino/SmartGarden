package com.example.smartgarden.firebase.storage

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseRealTimeDatabaseImpl : FirebaseRealTimeDatabase {

    /* Main object */
    val database = FirebaseDatabase.getInstance("https://smartgarden-d7604-default-rtdb.europe-west1.firebasedatabase.app/")

    override fun insertNode(module : String, children : List<String>, node : HashMap<String, Any>) : String{
        // build the path
        val ref = getReference(module, children)

        // Create the space
        val newRef = ref.push()
        // get the id of the new node
        val key = newRef.key

        // add value
        newRef.setValue(node)

        // return the key to save or to use
        return key ?: ""
    }

    override fun insertForceNode(module : String, children : List<String>, node : HashMap<String, Any>){
        // build the path
        val ref = getReference(module, children)

        //Insert node
        ref.setValue(node)
    }

    override fun updateNode(module : String, children : List<String>, node : HashMap<String, Any>){
        // build the path
        val ref = getReference(module, children)

        //Insert node
        ref.updateChildren(node)
    }

    override fun getNodeReference(module : String, children : List<String>) : DatabaseReference {
        return getReference(module, children)
    }

    private fun getReference(module : String, children : List<String>) : DatabaseReference{
        var ref = database.getReference(module)
        for(child in children){
            ref = ref.child(child)
        }
        return ref
    }

}
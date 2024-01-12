package com.example.smartgarden.firebase.storage

import com.google.firebase.database.DatabaseReference
import java.util.Objects

interface FirebaseRealTimeDatabase {

    /**
     * @param module main module, main topic
     * @param childs list of submodule
     * @param node object to insert
     *
     * @return identifying key of the node
     * */
    fun insertNode(module : String, childs : List<String>, node : HashMap<String, Any>) : String

    /**
     * Force to write the node inside the given path
     * */
    fun insertForceNode(module : String, childs : List<String>, node : HashMap<String, Any>)


    /**
     * @param module main module, main topic
     * @param childs list of submodule
     *
     * @return identifying key of the node
     * */
    fun getNodeReference(module : String, childs : List<String>) : DatabaseReference

}
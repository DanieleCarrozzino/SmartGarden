package com.example.smartgarden.firebase.authentication

import com.google.firebase.database.DatabaseReference

interface FirebaseRealTimeDatabase {

    /**
     * @param module main module, main topic
     * @param childs list of submodule
     * @param node object to insert
     *
     * @return identifying key of the node
     * */
    fun insertNode(module : String, childs : List<String>, node : String) : String


    /**
     * @param module main module, main topic
     * @param childs list of submodule
     *
     * @return identifying key of the node
     * */
    fun getNodeReference(module : String, childs : List<String>) : DatabaseReference

}
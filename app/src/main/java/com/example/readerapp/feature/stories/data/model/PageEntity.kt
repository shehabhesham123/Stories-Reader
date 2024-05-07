package com.example.readerapp.feature.stories.data.model

import com.google.gson.annotations.SerializedName

class ModifierPage(val pageNumber: Int, val modifiers: List<Modifier>)

class ModifierList() {
    @SerializedName("modifiers")
    var list: MutableList<ModifierPage> = mutableListOf()
}
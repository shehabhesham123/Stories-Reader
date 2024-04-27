package com.example.readerapp.feature.stories.data.model

import com.example.readerapp.R

class Query private constructor(
    val indices: List<Int>,
    val wordSize: Int,
    val highlightColor: Int
) {
    companion object {
        fun query(word: String, text: String, color: Int): Query {
            val indices = mutableListOf<Int>()
            val newWord = word.lowercase()
            val body = text.lowercase()
            for ((i, j) in body.withIndex()) {
                if (j == newWord[0]) {
                    var idx = i
                    var isValid = true
                    for (h in newWord) {
                        if (h != body[idx++]) {
                            isValid = false
                        }
                    }
                    if (isValid) indices.add(i)
                }
            }
            return Query(indices, word.length, color)
        }
    }
}
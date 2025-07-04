package com.sanaker.hvaskalvispise.data.model

// Bruker 'data class' sp, automatisk genererer hashCode(), equals(), toString() og copy()
//Dette gjør den ideell for enkle dataklasser
data class Dish(
    val id: String, // Bruker String for ID foreløpig, en UUID kan vøre bra senere
    val name: String // Navnet på retten
)
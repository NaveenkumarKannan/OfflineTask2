package com.naveenkumar.offlinetask2.model

data class Data(

    var title: String,

    var company: String,

    var item_count: Int,

    var date: String,

    var color: Int,

    var images: List<String>

) {
    override fun toString(): String {
        return "Data(title='$title', company='$company', item_count='$item_count', date='$date', color='$color', images='${images.joinToString()}')"
    }
}
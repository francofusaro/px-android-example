package com.example.pxandroidexample.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class Item(
    var title : String,
    @SerializedName("unit_price") var unitPrice : BigDecimal,
    var quantity : Int)

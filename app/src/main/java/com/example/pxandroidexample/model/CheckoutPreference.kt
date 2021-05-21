package com.example.pxandroidexample.model

import com.google.gson.annotations.SerializedName

data class CheckoutPreference(val id : String,
                              val items : List<Item>,
                              val payer : Payer,
                              @SerializedName("payment_methods")
                              val paymentPref : PaymentPreference
)

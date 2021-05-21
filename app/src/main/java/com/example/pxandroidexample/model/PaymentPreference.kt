package com.example.pxandroidexample.model

import com.google.gson.annotations.SerializedName

data class PaymentPreference(@SerializedName("excluded_payment_types")
                             val excludedPaymentTypes : List<PaymentType>,
                             @SerializedName("excluded_payment_methods")
                             val excludedPaymentMethods : List<PaymentMethod>)

data class PaymentType(val id : String)
data class PaymentMethod(val id : String)
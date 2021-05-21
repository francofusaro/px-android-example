package com.example.pxandroidexample.services

import android.os.Parcel
import android.os.Parcelable
import com.example.pxandroidexample.R
import com.mercadopago.android.px.core.SplitPaymentProcessor
import com.mercadopago.android.px.model.*
import com.mercadopago.android.px.model.internal.GenericPaymentDescriptor

interface IPaymentService : Parcelable {
    fun createPayment(checkoutData : SplitPaymentProcessor.CheckoutData) :
            IParcelablePaymentDescriptor
}
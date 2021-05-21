package com.example.pxandroidexample.model

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Parcel
import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.example.pxandroidexample.services.IPaymentService
import com.example.pxandroidexample.services.PaymentServiceMock
import com.example.pxandroidexample.ui.main.CongratsCustomTopFragment
import com.example.pxandroidexample.ui.main.CustomVisualPaymentFragment
import com.mercadopago.android.px.core.SplitPaymentProcessor
import com.mercadopago.android.px.preferences.CheckoutPreference

class CustomSplitPaymentProcessor(private val visualProcessor: Boolean = false,
                                  private val paymentService : IPaymentService)
    : SplitPaymentProcessor {
    private val handler = Handler(Looper.getMainLooper())

    override fun startPayment(context: Context,
                              checkoutData: SplitPaymentProcessor.CheckoutData,
                              onPaymentListener: SplitPaymentProcessor.OnPaymentListener
    ) {
        val payment = paymentService.createPayment(checkoutData)
        handler.postDelayed({ onPaymentListener.onPaymentFinished(payment)}, LOADING_TIME)
    }

    override fun getPaymentTimeout(checkoutPreference: CheckoutPreference): Int = TIMEOUT

    override fun shouldShowFragmentOnPayment(checkoutPreference: CheckoutPreference): Boolean = visualProcessor

    override fun supportsSplitPayment(checkoutPreference: CheckoutPreference?): Boolean = true

    override fun getFragment(checkoutData: SplitPaymentProcessor.CheckoutData,
                             context: Context): Fragment? {
        return CustomVisualPaymentFragment(paymentService, checkoutData)
    }

    constructor(parcel: Parcel) : this(
        parcel.readInt() == 1,
        parcel.readParcelable<IPaymentService>(IPaymentService::class.java.classLoader)!!) {
    }

    override fun describeContents() = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(if (visualProcessor) 1 else 0)
        parcel.writeParcelable(paymentService, 0)
    }

    companion object CREATOR : Parcelable.Creator<CustomSplitPaymentProcessor> {
        private const val TIMEOUT = 20000
        private const val LOADING_TIME = 2000L

        override fun createFromParcel(parcel: Parcel): CustomSplitPaymentProcessor {
            return CustomSplitPaymentProcessor(parcel)
        }

        override fun newArray(size: Int): Array<CustomSplitPaymentProcessor?> {
            return arrayOfNulls(size)
        }
    }
}
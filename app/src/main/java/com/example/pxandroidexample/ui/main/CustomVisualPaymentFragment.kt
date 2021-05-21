package com.example.pxandroidexample.ui.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pxandroidexample.R
import com.example.pxandroidexample.services.IPaymentService
import com.mercadopago.android.px.core.SplitPaymentProcessor

class CustomVisualPaymentFragment(
    private val paymentService : IPaymentService,
    private val checkoutData : SplitPaymentProcessor.CheckoutData) :
    Fragment() {

    private var onPaymentListener : SplitPaymentProcessor.OnPaymentListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_custom_visual_payment, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SplitPaymentProcessor.OnPaymentListener){
            onPaymentListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        onPaymentListener = null
    }

    override fun onResume() {
        super.onResume()
        // Ready to start payment
        val payment = paymentService.createPayment(checkoutData)
        view?.postDelayed({
            onPaymentListener?.onPaymentFinished(payment)
        }, DELAY_TIME_IN_MS)
    }

    companion object{
        const val DELAY_TIME_IN_MS = 2000L
    }
}
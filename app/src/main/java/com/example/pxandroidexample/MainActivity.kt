package com.example.pxandroidexample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.pxandroidexample.ui.main.MainFragment
import com.google.android.material.snackbar.Snackbar
import com.mercadopago.android.px.core.MercadoPagoCheckout
import com.mercadopago.android.px.model.IParcelablePaymentDescriptor
import com.mercadopago.android.px.model.exceptions.MercadoPagoError

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }
    companion object{
        const val REQ_CODE_CHECKOUT = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE_CHECKOUT){
            handlePaymentResult(resultCode, data)
        }
    }

    private fun handlePaymentResult(resultCode: Int, data: Intent?) {
        when (resultCode) {
            MercadoPagoCheckout.PAYMENT_RESULT_CODE -> handlePaymentFinished(data)
            RESULT_CANCELED -> handlePaymentCanceled(data)
            else -> handleOtherPaymentEnd(data, resultCode)
        }
    }

    private fun handlePaymentCanceled(data: Intent?){
        val error = data?.getSerializableExtra(MercadoPagoCheckout.EXTRA_ERROR)?.let {
            it as MercadoPagoError
        }
        Snackbar.make(findViewById(android.R.id.content),
            error?.errorDetail ?: "Payment canceled",
            Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.px_color_red_error))
            .setTextColor(ContextCompat.getColor(this, R.color.white))
            .show()
    }

    private fun handlePaymentFinished(data: Intent?) {
        val payment = data?.getSerializableExtra(MercadoPagoCheckout.EXTRA_PAYMENT_RESULT)?.let {
            it as IParcelablePaymentDescriptor
        }
        val paymentStatus = payment?.paymentStatus ?: "Payment was not returned"
        Snackbar.make(findViewById(android.R.id.content),
            "Payment finished with status: $paymentStatus",
            Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.px_green_status_bar))
            .setTextColor(ContextCompat.getColor(this, R.color.white))
            .show()
    }

    private fun handleOtherPaymentEnd(data: Intent?, code : Int) {
        Snackbar.make(findViewById(android.R.id.content),
            "Payment ended with custom responde code: $code",
            Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.design_blue_mp_dark))
            .setTextColor(ContextCompat.getColor(this, R.color.white))
            .show()
    }
}
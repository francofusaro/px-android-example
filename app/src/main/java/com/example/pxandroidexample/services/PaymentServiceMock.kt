package com.example.pxandroidexample.services

import android.os.Parcel
import android.os.Parcelable
import com.example.pxandroidexample.R
import com.example.pxandroidexample.ui.main.CongratsCustomTopFragment
import com.mercadopago.android.px.core.SplitPaymentProcessor
import com.mercadopago.android.px.model.*
import com.mercadopago.android.px.model.internal.GenericPaymentDescriptor


class PaymentServiceMock() : IPaymentService {

    constructor(parcel: Parcel) : this() {
    }

    override fun createPayment(checkoutData: SplitPaymentProcessor.CheckoutData): IParcelablePaymentDescriptor {
        with(checkoutData.checkoutPreference.items.first()){
            return when {
                title.contains(GENERIC_APPROVED) -> getApprovedGenericPayment()
                title.contains(BUSINESS_APPROVED) -> getApprovedBusinessPayment()
                title.contains(BUSINESS_REJECTED) -> getRejectedBusinessPayment()
                else -> GenericPaymentDescriptor.with(
                    GenericPayment.Builder(
                    Payment.StatusCodes.STATUS_REJECTED,
                    Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_OTHER_REASON)
                    .setPaymentId(1234L).createGenericPayment())
            }
        }
    }

    private fun getRejectedBusinessPayment() = BusinessPayment.Builder(
        BusinessPayment.Decorator.REJECTED,
        Payment.StatusCodes.STATUS_REJECTED,
        Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_CARD_NUMBER,
        R.drawable.px_icon_default,
        "Pago business rechazado :(",
        PaymentMethods.ARGENTINA.VISA,
        PaymentTypes.CREDIT_CARD
    )
        .setPaymentMethodVisibility(true)
        .setTopFragment(CongratsCustomTopFragment::class.java, null)
        .setPrimaryButton(ExitAction("Volver al inicio", 999))
        .setSubtitle("Subtitle?")
        .setReceiptId("9341")
        .build()

    private fun getApprovedBusinessPayment() = BusinessPayment.Builder(
        BusinessPayment.Decorator.APPROVED,
        Payment.StatusCodes.STATUS_APPROVED,
        Payment.StatusDetail.STATUS_DETAIL_ACCREDITED,
        R.drawable.px_badge_check_icon,
        "Pago business exitoso",
        PaymentMethods.ACCOUNT_MONEY,
        PaymentTypes.ACCOUNT_MONEY
    )
        .setPrimaryButton(ExitAction("Finalizar", 99))
        .setPaymentMethodVisibility(true)
        .build()

    private fun getApprovedGenericPayment() = GenericPaymentDescriptor.with(
        GenericPayment.Builder(
            Payment.StatusCodes.STATUS_APPROVED,
            Payment.StatusDetail.STATUS_DETAIL_ACCREDITED
        )
            .setPaymentId(1234L).createGenericPayment()
    )

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
    }

    companion object CREATOR : Parcelable.Creator<PaymentServiceMock> {
        const val GENERIC_APPROVED = "GEN APRO"
        const val BUSINESS_APPROVED = "BUS APRO"
        const val BUSINESS_REJECTED = "BUS REJE"

        override fun createFromParcel(parcel: Parcel): PaymentServiceMock {
            return PaymentServiceMock(parcel)
        }

        override fun newArray(size: Int): Array<PaymentServiceMock?> {
            return arrayOfNulls(size)
        }
    }

}
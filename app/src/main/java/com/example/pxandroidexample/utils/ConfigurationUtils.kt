package com.example.pxandroidexample.utils

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.fragment.app.DialogFragment
import com.example.pxandroidexample.model.CustomSplitPaymentProcessor
import com.example.pxandroidexample.services.PaymentServiceMock
import com.example.pxandroidexample.ui.main.CongratsCustomTopFragment
import com.example.pxandroidexample.ui.main.CustomDialogFragment
import com.mercadopago.android.px.configuration.*
import com.mercadopago.android.px.core.DynamicDialogCreator
import com.mercadopago.android.px.model.PaymentMethods
import com.mercadopago.android.px.model.PaymentTypes
import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule
import java.math.BigDecimal


class ConfigurationUtils {

    companion object{
        fun getPaymentConfiguration(useVisualProcessor: Boolean, addChargeToCreditCard : Boolean) :
                PaymentConfiguration{
            val charges: MutableList<PaymentTypeChargeRule> = getCharges(addChargeToCreditCard)
            return PaymentConfiguration.Builder(
                CustomSplitPaymentProcessor(useVisualProcessor, PaymentServiceMock()))
                .addChargeRules(charges)
                .build()
        }

        fun getAdvancedConfiguration(useCustomStrings : Boolean,
                                     useDialogOnTapHeader : Boolean,
                                     useCustomFragments : Boolean) :
                AdvancedConfiguration{
            return AdvancedConfiguration.Builder().also {
                if (useCustomStrings){
                    it.setCustomStringConfiguration(getCustomStringConfiguration())
                }
                if (useDialogOnTapHeader){
                    it.setDynamicDialogConfiguration(getDynamicDialogConfiguration())
                }
                if (useCustomFragments) {
                    it.setPaymentResultScreenConfiguration(getPaymentResultScreenConfiguration())
                }
            }.setExpressPaymentEnable(true).build()
        }

        private fun getPaymentResultScreenConfiguration() :
                PaymentResultScreenConfiguration{
            return PaymentResultScreenConfiguration.Builder()
                    .setTopFragment(CongratsCustomTopFragment::class.java, null)
                    .setBottomFragment(CongratsCustomTopFragment::class.java, null)
                .build()
        }

        private fun getCharges(addChargeToCreditCard: Boolean): MutableList<PaymentTypeChargeRule> {
            val charges: MutableList<PaymentTypeChargeRule> = ArrayList()
            charges.add(PaymentTypeChargeRule.createChargeFreeRule(
                PaymentMethods.ACCOUNT_MONEY,
                "Este método no tiene cargos!"))
            if (addChargeToCreditCard) {
                charges.add(PaymentTypeChargeRule(
                    PaymentTypes.CREDIT_CARD,
                    BigDecimal.TEN,
                    CustomDynamicDialog())
                )
            }
            return charges
        }

        private fun getDynamicDialogConfiguration() = DynamicDialogConfiguration.Builder()
            .addDynamicCreator(
                DynamicDialogConfiguration.DialogLocation.TAP_ONE_TAP_HEADER,
                CustomDynamicDialog()
            )
            .build()

        private fun getCustomStringConfiguration() = CustomStringConfiguration.Builder()
            .setCustomPayButtonText("Pagar customizado!")
            .setCustomPayButtonProgressText("Pagando customizado... Espera")
            .setTotalDescriptionText("Monto a pagar customizado")
            .build()
    }
}

class CustomDynamicDialog() : DynamicDialogCreator{
    constructor(parcel: Parcel) : this() {
    }

    override fun shouldShowDialog(
        context: Context,
        checkoutData: DynamicDialogCreator.CheckoutData
    ): Boolean = true

    override fun create(
        context: Context,
        checkoutData: DynamicDialogCreator.CheckoutData
    ): DialogFragment {
        return CustomDialogFragment()
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
    }

    companion object CREATOR : Parcelable.Creator<CustomDynamicDialog> {
        override fun createFromParcel(parcel: Parcel): CustomDynamicDialog {
            return CustomDynamicDialog(parcel)
        }

        override fun newArray(size: Int): Array<CustomDynamicDialog?> {
            return arrayOfNulls(size)
        }
    }

}
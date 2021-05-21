package com.example.pxandroidexample.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pxandroidexample.model.*
import com.example.pxandroidexample.services.CheckoutPreferenceServiceImpl
import com.example.pxandroidexample.services.ResultWrapper
import com.example.pxandroidexample.utils.ConfigurationUtils
import com.example.pxandroidexample.utils.Credentials
import com.example.pxandroidexample.utils.SessionManager
import com.mercadopago.android.px.configuration.AdvancedConfiguration
import com.mercadopago.android.px.configuration.PaymentConfiguration
import com.mercadopago.android.px.core.MercadoPagoCheckout
import com.mercadopago.android.px.model.GenericPayment
import com.mercadopago.android.px.model.IParcelablePaymentDescriptor
import com.mercadopago.android.px.model.Payment
import com.mercadopago.android.px.model.PaymentTypes
import com.mercadopago.android.px.model.internal.GenericPaymentDescriptor
import kotlinx.coroutines.launch
import java.math.BigDecimal

class MainViewModel : ViewModel() {
    private lateinit var _sessionManager : SessionManager

    private val _itemTitle = MutableLiveData<String>()
    val itemTitle: MutableLiveData<String>
        get() = _itemTitle

    private val _itemUnitPrice = MutableLiveData<BigDecimal>()
    val itemUnitPrice: MutableLiveData<BigDecimal>
        get() = _itemUnitPrice

    private val _itemQuantity = MutableLiveData<Int>()
    val itemQuantity: MutableLiveData<Int>
        get() = _itemQuantity

    private val _checkoutBuilder = MutableLiveData<MercadoPagoCheckout.Builder>()
    val checkoutBuilder: MutableLiveData<MercadoPagoCheckout.Builder>
        get() = _checkoutBuilder

    var useLastPrefId: Boolean = false
    var excludeCreditCard: Boolean = false
    var excludeDebitCard: Boolean = false
    var useCustomPaymentProcessor: Boolean = false
    var useVisualProcessor: Boolean = false
    var useCustomStrings: Boolean = false
    var useDialogOnHeaderTap: Boolean = false
    var addChargeToCreditCard: Boolean = false
    var useCustomTopAndBottomFragmentsOnResultScreen: Boolean = false

    fun updateProperty(key: Int, value: String) {
        when (key){
            ITEM_TITLE_KEY -> _itemTitle.value = value
            ITEM_PRICE_KEY -> _itemUnitPrice.value =
                if (value.isNotEmpty()) value.toBigDecimal() else null
            ITEM_QUANTITY_KEY -> _itemQuantity.value =
                if (value.isNotEmpty()) value.toInt() else null
        }
    }

    fun isStartCheckoutEnabled(): Boolean {
        return useLastPrefId || !_itemTitle.value.isNullOrEmpty() &&
                _itemQuantity.value != null &&
                _itemUnitPrice.value != null
    }

    fun createCheckoutBuilder(){
        viewModelScope.launch {
            val prefId = getPreferenceId()
            if (prefId.isNotEmpty()) {
                _sessionManager.saveLastPrefId(prefId)
                val builder = if (useCustomPaymentProcessor){
                    MercadoPagoCheckout.Builder(
                        Credentials.DEFAULT_MLA_COLLECTOR_PUBLIC_KEY,
                        prefId,
                        ConfigurationUtils.getPaymentConfiguration(useVisualProcessor,
                            addChargeToCreditCard))
                } else {
                    MercadoPagoCheckout.Builder(
                        Credentials.DEFAULT_MLA_COLLECTOR_PUBLIC_KEY, prefId)
                }.setAdvancedConfiguration(
                        ConfigurationUtils.getAdvancedConfiguration(
                            useCustomStrings,
                            useDialogOnHeaderTap,
                            useCustomTopAndBottomFragmentsOnResultScreen))
                    .setPrivateKey(Credentials.DEFAULT_MLA_PAYER_ACCESS_TOKEN)
                checkoutBuilder.postValue(builder)
            }
        }
    }

    private suspend fun getPreferenceId(): String {
        return if (useLastPrefId)
            _sessionManager.getLastPrefId() ?: createCheckoutPreference()
        else
            createCheckoutPreference()
    }

    private suspend fun createCheckoutPreference(): String {
        val item = Item(_itemTitle.value!!, _itemUnitPrice.value!!, _itemQuantity.value!!)
        val checkoutPrefDTO = CheckoutPreference("", listOf(item),
            Payer(Credentials.DEFAULT_MLA_PAYER_EMAIL),
            PaymentPreference(getExcludedPaymentTypes(), emptyList())
        )
        return when (val createPrefResponse = CheckoutPreferenceServiceImpl()
            .create(checkoutPrefDTO, Credentials.DEFAULT_MLA_COLLECTOR_ACCESS_TOKEN)) {
            is ResultWrapper.Success -> {
                createPrefResponse.value.id
            }
            else -> "" // TODO: Add branches for network error and generic error
        }
    }

    private fun getExcludedPaymentTypes(): List<PaymentType> {
        val excludedPaymentTypes = mutableListOf<PaymentType>()
        if (excludeCreditCard){
            excludedPaymentTypes.add(PaymentType(PaymentTypes.CREDIT_CARD))
        }
        if (excludeDebitCard){
            excludedPaymentTypes.add(PaymentType(PaymentTypes.DEBIT_CARD))
        }
        return excludedPaymentTypes
    }

    fun setSessionManager(sessionManager: SessionManager) {
        _sessionManager = sessionManager
    }

    companion object{
        const val ITEM_TITLE_KEY : Int = 0
        const val ITEM_PRICE_KEY : Int = 1
        const val ITEM_QUANTITY_KEY : Int = 2
    }
}
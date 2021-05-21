package com.example.pxandroidexample.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pxandroidexample.MainActivity
import com.example.pxandroidexample.R
import com.example.pxandroidexample.utils.SessionManager
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputLayout
import com.mercadopago.android.px.core.CheckoutLazyInit
import com.mercadopago.android.px.core.MercadoPagoCheckout

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var useLastPref : MaterialCheckBox
    private lateinit var itemTitleInput : TextInputLayout
    private lateinit var itemUnitPriceInput : TextInputLayout
    private lateinit var itemQuantityInput : TextInputLayout
    private lateinit var excludeCreditCard : MaterialCheckBox
    private lateinit var excludeDebitCard : MaterialCheckBox
    private lateinit var useCustomPaymentProcessor : MaterialCheckBox
    private lateinit var useVisualProcessor : MaterialCheckBox
    private lateinit var addChargeToCreditCard : MaterialCheckBox
    private lateinit var useCustomStrings : MaterialCheckBox
    private lateinit var useDialogOnHeaderTap : MaterialCheckBox
    private lateinit var useCustomResultScreenFragments : MaterialCheckBox
    private lateinit var startCheckoutBtn : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.setSessionManager(SessionManager(requireContext()))

        setViewAccessors(view)
        setUseLastPrefCheckboxListener()
        setExcludeCardCheckboxListener()
        setItemInputsChangeListeners()
        setPaymentConfigurationListeners()
        setAdvancedConfigurationListeners()
        setPaymentResultScreenConfigurationListeners()
        setItemObservers()
        setStartCheckoutObserver()
        setStartCheckoutButtonClickListener()
    }

    private fun setPaymentResultScreenConfigurationListeners() {
        useCustomResultScreenFragments.setOnCheckedChangeListener{ _, isChecked ->
            viewModel.useCustomTopAndBottomFragmentsOnResultScreen = isChecked
        }
    }

    private fun setPaymentConfigurationListeners(){
        useCustomPaymentProcessor.setOnCheckedChangeListener { _, isChecked ->
            viewModel.useCustomPaymentProcessor = isChecked
            useVisualProcessor.isChecked = false
            useVisualProcessor.isEnabled = isChecked
            addChargeToCreditCard.isChecked = false
            addChargeToCreditCard.isEnabled = isChecked
        }
        useVisualProcessor.setOnCheckedChangeListener { _, isChecked ->
            viewModel.useVisualProcessor = isChecked
        }
        addChargeToCreditCard.setOnCheckedChangeListener { _, isChecked ->
            viewModel.addChargeToCreditCard = isChecked
        }
    }

    private fun setAdvancedConfigurationListeners(){
        useCustomStrings.setOnCheckedChangeListener { _, isChecked ->
            viewModel.useCustomStrings = isChecked
        }
        useDialogOnHeaderTap.setOnCheckedChangeListener { _, isChecked ->
            viewModel.useDialogOnHeaderTap = isChecked
        }
    }

    private fun setExcludeCardCheckboxListener(){
        excludeCreditCard.setOnCheckedChangeListener { _, isChecked ->
            viewModel.excludeCreditCard = isChecked
        }
        excludeDebitCard.setOnCheckedChangeListener { _, isChecked ->
            viewModel.excludeDebitCard = isChecked
        }
    }

    private fun setUseLastPrefCheckboxListener() {
        useLastPref.setOnCheckedChangeListener { _, isChecked ->
            viewModel.useLastPrefId = isChecked
            changeStartCheckoutBtnIsEnabled()
        }
    }

    private fun setStartCheckoutButtonClickListener() {
        startCheckoutBtn.setOnClickListener {
            viewModel.createCheckoutBuilder()
        }
    }

    private fun setStartCheckoutObserver() {
        viewModel.checkoutBuilder.observe(viewLifecycleOwner, {
            val lazyInit: CheckoutLazyInit = object : CheckoutLazyInit(it) {
                override fun fail(mercadoPagoCheckout: MercadoPagoCheckout) {
                    mercadoPagoCheckout.startPayment(
                        requireActivity(),
                        MainActivity.REQ_CODE_CHECKOUT
                    )
                }

                override fun success(mercadoPagoCheckout: MercadoPagoCheckout) {
                    mercadoPagoCheckout.startPayment(
                        requireActivity(),
                        MainActivity.REQ_CODE_CHECKOUT
                    )
                }
            }
            lazyInit.fetch(requireContext())
        })
    }

    private fun setViewAccessors(view: View) {
        // this can be avoided with data binding too
        with(view) {
            itemTitleInput = findViewById(R.id.item_title)
            itemUnitPriceInput = findViewById(R.id.item_price)
            itemQuantityInput = findViewById(R.id.item_quantity)
            startCheckoutBtn = findViewById(R.id.start_checkout_btn)
            useLastPref = findViewById(R.id.use_last_pref_id_radio_btn)
            excludeCreditCard = findViewById(R.id.exclude_credit_card)
            excludeDebitCard = findViewById(R.id.exclude_debit_card)
            useCustomPaymentProcessor = findViewById(R.id.use_custom_payment_processor)
            useVisualProcessor = findViewById(R.id.use_visual_processor)
            useCustomStrings = findViewById(R.id.use_custom_strings)
            useDialogOnHeaderTap = findViewById(R.id.use_dialog_on_tap_header)
            addChargeToCreditCard = findViewById(R.id.add_charge_to_credit)
            useCustomResultScreenFragments = findViewById(R.id.use_custom_fragments_on_payment_result)

        }
    }

    private fun setItemObservers() {
        with(viewModel) {
            for (prop in listOf(itemQuantity, itemUnitPrice, itemTitle)) {
                prop.observe(viewLifecycleOwner, {
                    changeStartCheckoutBtnIsEnabled()
                })
            }
        }
    }

    private fun changeStartCheckoutBtnIsEnabled() {
        startCheckoutBtn.isEnabled = viewModel.isStartCheckoutEnabled()
    }

    private fun setItemInputsChangeListeners() {
        // this can be done with data bindings and onTextChanged
        val viewModelPropKeyMap = mapOf(Pair(MainViewModel.ITEM_TITLE_KEY, itemTitleInput),
            Pair(MainViewModel.ITEM_PRICE_KEY, itemUnitPriceInput),
            Pair(MainViewModel.ITEM_QUANTITY_KEY, itemQuantityInput))
        for (textInput in viewModelPropKeyMap) {
            textInput.value.editText?.let { editText ->
                editText.doAfterTextChanged {
                    viewModel.updateProperty(textInput.key, it.toString())
                }
            }
        }
    }
}
package com.kompsia.sketchcolor.billing

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener

class BillingManager(
    private val context: Context,
    private val onPremiumUnlocked: () -> Unit
) : PurchasesUpdatedListener {

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .build()

    fun start() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Ready. Add Play Console products premium_monthly and gems_100 before real purchase flow.
                }
            }

            override fun onBillingServiceDisconnected() {
                // Billing client will reconnect on next purchase attempt.
            }
        })
    }

    fun openPremiumScreen(activity: Activity) {
        Toast.makeText(
            activity,
            "Billing is ready. Create Play Console product premium_monthly, then connect ProductDetails purchase flow here.",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !purchases.isNullOrEmpty()) {
            onPremiumUnlocked()
        }
    }

    companion object {
        const val PREMIUM_PRODUCT_ID = "premium_monthly"
        const val GEMS_PRODUCT_ID = "gems_100"
    }
}

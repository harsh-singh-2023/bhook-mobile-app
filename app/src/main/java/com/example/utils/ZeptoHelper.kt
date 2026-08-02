package com.example.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object ZeptoHelper {
    private const val ZEPTO_BASE_URL = "https://www.zepto.com"
    private const val ZEPTO_PACKAGE = "com.zeptocookbook.zepto"

    fun openZeptoSearch(context: Context, query: String) {
        val cleanQuery = query.trim()
        if (cleanQuery.isBlank()) return

        val encodedQuery = URLEncoder.encode(cleanQuery, StandardCharsets.UTF_8.toString())
        val webUrl = "$ZEPTO_BASE_URL/search?q=$encodedQuery"

        Toast.makeText(
            context,
            "🛒 Opening Zepto for '$cleanQuery' — Tap 'ADD' to put in cart!",
            Toast.LENGTH_LONG
        ).show()

        try {
            // Try launching Zepto native app with search query intent
            val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.zepto.com/search?q=$encodedQuery")).apply {
                setPackage(ZEPTO_PACKAGE)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(appIntent)
        } catch (e: Exception) {
            // Fallback to web browser or default intent resolver
            try {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)
            } catch (ex: Exception) {
                Toast.makeText(context, "Could not open Zepto: ${ex.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun openZeptoStore(context: Context) {
        Toast.makeText(context, "🛒 Opening Zepto Express Grocery Store...", Toast.LENGTH_SHORT).show()
        try {
            val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ZEPTO_BASE_URL)).apply {
                setPackage(ZEPTO_PACKAGE)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(appIntent)
        } catch (e: Exception) {
            try {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ZEPTO_BASE_URL)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)
            } catch (ex: Exception) {
                Toast.makeText(context, "Cannot open Zepto website", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun orderMultipleItemsOnZepto(context: Context, items: List<String>) {
        if (items.isEmpty()) return
        val mainItem = items.first()
        val allItemsStr = items.joinToString(", ")

        // Copy item list to clipboard for easy pasting in Zepto app search/notes
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Hostel Grocery List", allItemsStr)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(
                context,
                "🛒 Opening Zepto for '$mainItem'! List copied: $allItemsStr",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            // Ignore clipboard errors
        }

        openZeptoSearch(context, mainItem)
    }
}


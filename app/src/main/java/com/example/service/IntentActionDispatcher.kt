package com.example.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import java.net.URLEncoder

class IntentActionDispatcher(private val context: Context) {

    fun executeCall(phoneNumber: String): Boolean {
        return try {
            val cleanNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$cleanNumber")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open phone dialer: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun executeWhatsApp(phoneNumber: String, message: String): Boolean {
        return try {
            val cleanNumber = phoneNumber.replace(Regex("[^0-9]"), "")
            val encodedMessage = URLEncoder.encode(message, "UTF-8")
            // Try standard whatsapp uri
            val uri = if (cleanNumber.isNotEmpty()) {
                Uri.parse("https://api.whatsapp.com/send?phone=$cleanNumber&text=$encodedMessage")
            } else {
                Uri.parse("https://api.whatsapp.com/send?text=$encodedMessage")
            }
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            // Check if WhatsApp can handle it, otherwise open via browser
            val packageManager = context.packageManager
            if (intent.resolveActivity(packageManager) != null) {
                context.startActivity(intent)
                true
            } else {
                // Fallback to general intent
                val fallbackIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(fallbackIntent)
                true
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open WhatsApp: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun executeSms(phoneNumber: String, message: String): Boolean {
        return try {
            val cleanNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$cleanNumber")
                putExtra("sms_body", message)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open SMS: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun openAppByName(appName: String, extraQuery: String? = null): Boolean {
        val normalized = appName.lowercase().trim()
        val intent = when {
            normalized.contains("camera") -> {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            }
            normalized.contains("youtube") -> {
                if (!extraQuery.isNullOrBlank()) {
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=${URLEncoder.encode(extraQuery, "UTF-8")}"))
                } else {
                    context.packageManager.getLaunchIntentForPackage("com.google.android.youtube")
                        ?: Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com"))
                }
            }
            normalized.contains("spotify") -> {
                if (!extraQuery.isNullOrBlank()) {
                    Intent(Intent.ACTION_VIEW, Uri.parse("spotify:search:${URLEncoder.encode(extraQuery, "UTF-8")}"))
                } else {
                    context.packageManager.getLaunchIntentForPackage("com.spotify.music")
                        ?: Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com"))
                }
            }
            normalized.contains("map") || normalized.contains("direction") || normalized.contains("traffic") -> {
                val query = extraQuery ?: "current location"
                Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${URLEncoder.encode(query, "UTF-8")}"))
            }
            normalized.contains("whatsapp") -> {
                context.packageManager.getLaunchIntentForPackage("com.whatsapp")
                    ?: Intent(Intent.ACTION_VIEW, Uri.parse("https://web.whatsapp.com"))
            }
            normalized.contains("setting") -> {
                Intent(Settings.ACTION_SETTINGS)
            }
            normalized.contains("chrome") || normalized.contains("browser") -> {
                Intent(Intent.ACTION_VIEW, Uri.parse("https://codeninjavik.in"))
            }
            normalized.contains("codeninjavik") -> {
                Intent(Intent.ACTION_VIEW, Uri.parse("https://codeninjavik.in"))
            }
            normalized.contains("calculator") -> {
                Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_CALCULATOR)
            }
            else -> {
                // Try searching package manager
                context.packageManager.getLaunchIntentForPackage(appName)
            }
        }

        return try {
            if (intent != null) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                true
            } else {
                Toast.makeText(context, "App '$appName' not found on device", Toast.LENGTH_SHORT).show()
                false
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Could not launch $appName: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }
}

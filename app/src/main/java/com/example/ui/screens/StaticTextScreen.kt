package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ui.theme.ResidoBackground
import com.example.ui.theme.ResidoBlue
import com.example.ui.theme.ResidoDarkNavy
import com.example.ui.theme.ResidoTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaticTextScreen(
    navController: NavController,
    title: String,
    type: String // "privacy" | "terms" | "support"
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = ResidoDarkNavy
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ResidoDarkNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                modifier = Modifier.shadow(2.dp)
            )
        },
        containerColor = ResidoBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            when (type) {
                "privacy" -> PrivacyPolicyContent()
                "terms" -> TermsAndConditionsContent()
                "support" -> SupportContent()
            }
        }
    }
}

@Composable
fun PrivacyPolicyContent() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Privacy Policy",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ResidoDarkNavy
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Last updated: July 17, 2026",
                fontSize = 12.sp,
                color = ResidoTextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "At Resido Sri Lanka, accessible from Resido Mobile, one of our main priorities is the privacy of our visitors. This Privacy Policy document contains types of information that is collected and recorded by Resido and how we use it.",
                fontSize = 14.sp,
                color = ResidoDarkNavy.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Information We Collect",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ResidoDarkNavy
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "If you register an account, we collect personal details like your First Name, Last Name, email address, phone number, date of birth, and active role preference (Buyer/Agent) to facilitate listings and property search features.",
                fontSize = 14.sp,
                color = ResidoTextSecondary,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "How We Use Your Information",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ResidoDarkNavy
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "• To operate, maintain, and personalize our mobile application.\n" +
                "• To understand how you browse properties in Sri Lanka.\n" +
                "• To develop new features, services, and search experiences.\n" +
                "• To communicate with you regarding listings or inquiries.\n" +
                "• To send you automated alerts and system notifications.",
                fontSize = 14.sp,
                color = ResidoTextSecondary,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Third-Party Integrations",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ResidoDarkNavy
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "We do not sell or rent your personal information to third parties. All property listings and mock conversations are kept strictly within the local device environment and mock servers during this phase.",
                fontSize = 14.sp,
                color = ResidoTextSecondary,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun TermsAndConditionsContent() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Terms and Conditions",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ResidoDarkNavy
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Last updated: July 17, 2026",
                fontSize = 12.sp,
                color = ResidoTextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Welcome to Resido Sri Lanka! These terms and conditions outline the rules and regulations for the use of the Resido Real Estate Mobile Application.",
                fontSize = 14.sp,
                color = ResidoDarkNavy.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Acceptance of Terms",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ResidoDarkNavy
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "By accessing this mobile app, we assume you accept these terms and conditions. Do not continue to use Resido if you do not agree to take all of the terms and conditions stated on this page.",
                fontSize = 14.sp,
                color = ResidoTextSecondary,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Listing Regulations",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ResidoDarkNavy
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "As an authorized Agent or Property Owner listing on Resido, you agree that:\n" +
                "• All information, photos, and prices submitted are authentic and accurate.\n" +
                "• Prices are declared transparently in Sri Lankan Rupees (LKR).\n" +
                "• You possess the right to list and represent the specified real estate assets.\n" +
                "• You will not upload misleading details, offensive media, or spam listings.",
                fontSize = 14.sp,
                color = ResidoTextSecondary,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Limitation of Liability",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ResidoDarkNavy
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Resido serves as a matchmaking marketplace connecting buyers and agents. We do not inspect real estate properties in person or guarantee financial agreements. Users are requested to perform due diligence before making advance payments or signing deeds.",
                fontSize = 14.sp,
                color = ResidoTextSecondary,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun SupportContent() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Need Help & Support?",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ResidoDarkNavy
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Get in touch with Resido Sri Lanka Help Center.",
                fontSize = 13.sp,
                color = ResidoTextSecondary
            )
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Contact Information",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ResidoDarkNavy
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email",
                    tint = ResidoBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Email Support", fontSize = 12.sp, color = ResidoTextSecondary)
                    Text("support@resido.lk", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ResidoDarkNavy)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Phone",
                    tint = ResidoBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Hotline Support", fontSize = 12.sp, color = ResidoTextSecondary)
                    Text("+94 11 234 5678", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ResidoDarkNavy)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Office",
                    tint = ResidoBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Headquarters", fontSize = 12.sp, color = ResidoTextSecondary)
                    Text("Level 12, Galle Face Tower, Colombo 02, Sri Lanka", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = ResidoDarkNavy)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Our customer success team is available 24/7 to help you publish property listings, configure favorite collections, or resolve account settings inquiries.",
                fontSize = 13.sp,
                color = ResidoTextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}

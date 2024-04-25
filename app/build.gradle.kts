import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import java.util.Properties
import java.io.FileInputStream
import java.util.Date
import java.text.SimpleDateFormat

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinKapt)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.navigation.safeargs)
    id("com.google.firebase.crashlytics")
}

// Load keystore
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
    namespace = "com.practical.mydatamachine"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.practical.mydatamachine"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        android.buildFeatures.buildConfig = true

        buildConfigField("String", "SERVER_URL", "\"https://jsonplaceholder.typicode.com/\"")

    }

    buildTypes {
        create("staging") {

            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
        }
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            manifestPlaceholders["enableCrashReporting"] = false
        }

        applicationVariants.all {
            val formattedDate = SimpleDateFormat("ddMMyyyy").format(Date())
            val variant = this
            variant.outputs.map { it as BaseVariantOutputImpl }.forEach { output ->
                val outputFileName = "Practical-${variant.name}_v${variant.versionName}.$formattedDate.apk"
                output.outputFileName = outputFileName
            }
        }
    }

    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
        }
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.retrofit.scalar)
    implementation(libs.gson.convertor)
    implementation(libs.logging.interceptor)
    implementation(libs.coroutine)
    implementation(libs.coroutine.core)
    implementation(libs.flexbox)

    implementation(libs.activity.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.lifecycle.ext)
    implementation(libs.androidx.paging)

    implementation(libs.shimmer.view)
    implementation(libs.splash.screen)

    implementation(platform(libs.bomb.hasbeen.planted))
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlog.ktx)

    implementation(libs.sdp.dimens)
    implementation(libs.ssp.dimens)
    implementation(libs.glide)
    implementation(libs.imagepicker)
    implementation(libs.lottie)

    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.clock.view)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
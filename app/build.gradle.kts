import java.util.Properties
import com.google.gms.googleservices.GoogleServicesTask

val secrets = Properties().apply {
    load(rootProject.file("secrets.properties").inputStream())
}

@Suppress("DSL_SCOPE_VIOLATION") // Remove when fixed https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.google.services.crashlytics)
}

android {
    signingConfigs {
        create("release") {
            storeFile = file(secrets["keystorePath"] as String)
            storePassword = secrets["keystorePassword"] as String
            keyAlias = secrets["keystoreAlias"] as String
            keyPassword = secrets["keystoreAliasPassword"] as String
        }
    }
    namespace = "xyz.artenes.budget"
    compileSdk = 34

    defaultConfig {
        applicationId = "xyz.artenes.budget"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "xyz.artenes.budget.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Enable room auto-migrations
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    flavorDimensions.add("app")

    productFlavors {

        create("production") {
            dimension = "app"
        }

        create("development") {
            dimension = "app"
        }

    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            applicationIdSuffix = ".release"
            versionNameSuffix = "-release"
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        aidl = false
        buildConfig = true
        renderScript = false
        shaders = false
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    coreLibraryDesugaring(libs.android.tools.desugar)

    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Timber
    implementation(libs.timber)

    // Icons
    implementation(libs.icons.extended)

    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    // Hilt and instrumented tests.
    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.hilt.android.compiler)
    // Hilt and Robolectric tests.
    testImplementation(libs.hilt.android.testing)
    kaptTest(libs.hilt.android.compiler)

    // Arch Components
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Compose
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    implementation(libs.faker)
    implementation(libs.datastore)

    implementation(platform(libs.google.firebase))
    implementation(libs.google.firebase.analytics)
    implementation(libs.google.firebase.crashlytics)

    // Tooling
    debugImplementation(libs.androidx.compose.ui.tooling)
    // Instrumented tests
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Local tests: jUnit, coroutines, Android runner
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

    // Instrumented tests: jUnit rules and runners

    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
}

tasks.register<DefaultTask>("checkGoogleServicesJson") {
    val debugFilePath = project.file("src/debug/google-services.json")
    val releaseFilePath = project.file("src/release/google-services.json")

    // Declare file properties
    inputs.file(debugFilePath)
    inputs.file(releaseFilePath)

    doLast {
        if (!debugFilePath.exists()) {
            throw GradleException("Missing google-services.json file in src/debug")
        }

        if (!releaseFilePath.exists()) {
            throw GradleException("Missing google-services.json file in src/release")
        }
    }
}

tasks.named("preBuild").configure {
    dependsOn(tasks.named("checkGoogleServicesJson"))
}

project.afterEvaluate {
    tasks.withType<GoogleServicesTask> {
        gmpAppId.set(project.buildDir.resolve("$name-gmpAppId.txt"))
    }
}
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.apollo)
}

android {
    namespace = "com.jaymin.newsaggregator.core.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

// Apollo GraphQL schema configuration
apollo {
    service("newsService") {
        packageName.set("com.jaymin.newsaggregator.core.data.graphql")
        // Schema file goes in src/main/graphql/
        schemaFiles.from("src/main/graphql/schema.graphqls")
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain"))

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    ksp(libs.room.compiler)

    // Paging
    implementation(libs.paging.runtime)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // Retrofit (for Weather REST API)
    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Apollo GraphQL (for News)
    implementation(libs.apollo.runtime)
    implementation(libs.apollo.normalized.cache)

    // Serialization
    implementation(libs.serialization.json)

    // Location
    implementation(libs.location)
}

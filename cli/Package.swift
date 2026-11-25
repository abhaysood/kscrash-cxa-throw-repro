// swift-tools-version:5.9
import PackageDescription

// Build the Kotlin framework first: ./gradlew assembleReproKotlinReleaseXCFramework
let package = Package(
    name: "ReproCLI",
    platforms: [.macOS(.v13)],
    dependencies: [
        .package(url: "https://github.com/kstenerud/KSCrash.git", exact: "2.6.1"),
    ],
    targets: [
        .binaryTarget(name: "ReproKotlin", path: "../build/XCFrameworks/release/ReproKotlin.xcframework"),
        // Extra symbols in the same executable that the Kotlin framework links into.
        .target(name: "Padding", publicHeadersPath: "."),
        .executableTarget(
            name: "ReproCLI",
            dependencies: ["ReproKotlin", "Padding", .product(name: "Recording", package: "KSCrash")],
            linkerSettings: [.linkedLibrary("c++")]
        ),
    ]
)

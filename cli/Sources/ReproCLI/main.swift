import Foundation
import KSCrashRecording
import ReproKotlin

// Usage: ReproCLI [--kscrash]
//   (none)      no KSCrash
//   --kscrash   KSCrash with the default KSCrashConfiguration
// Prints milliseconds for: cancel 500 collectors, cancel 500 combine collectors.
if CommandLine.arguments.contains("--kscrash") {
    try KSCrash.shared.install(with: KSCrashConfiguration())
}

let count: Int32 = 500
_ = CasesKt.cancelCoroutines(count: 10, combined: true)

let plain = CasesKt.cancelCoroutines(count: count, combined: false)
let combined = CasesKt.cancelCoroutines(count: count, combined: true)
print(String(format: "%.1f %.1f", plain, combined))

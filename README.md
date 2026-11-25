# Coroutine cancellation time in Kotlin/Native with KSCrash's `__cxa_throw` hook

By default, KSCrash 2.6.1 replaces `__cxa_throw` in the app's binaries with its own function. That function runs on every C++ `throw`, even when the exception is caught. On each throw it calls `dladdr()` to find the binary that threw. `dladdr()` searches that binary's symbol table. A binary with more symbols makes each throw slower.

Kotlin/Native implements every Kotlin exception as a C++ `throw`. Each cancelled coroutine throws a `CancellationException`. When a screen closes, its `viewModelScope` cancels all of its coroutines on the main thread. The main thread is blocked until every one of them has thrown.

The `dladdr()` call is in [KSCxaThrowSwapper.c](https://github.com/kstenerud/KSCrash/blob/c3882b4db5e810a8ed4148b79cab29a173d4c5c5/Sources/KSCrashRecordingCore/KSCxaThrowSwapper.c#L253-L257).

## How to run it

You need an Apple Silicon Mac with Xcode and JDK 17 or newer. From the repository root, run:

```
./run.sh
```

The script cancels 500 coroutines that collect a `StateFlow`, then 500 that collect `combine(a, b)`. It times both without KSCrash and with KSCrash's default settings.

## Results

We ran it on an Apple M2 Pro with macOS 26.5.1 and Xcode 26.5. Times are in milliseconds.

| Binary | Symbols | Coroutines cancelled | Without KSCrash | With KSCrash 2.6.1 |
| --- | ---: | --- | ---: | ---: |
| Original | 8,440 | 500 collecting a `StateFlow` | 11 | 50 |
| | | 500 collecting `combine(a, b)` | 19 | 72 |
| With 100,000 empty functions | 108,440 | 500 collecting a `StateFlow` | 11 | 691 |
| | | 500 collecting `combine(a, b)` | 18 | 1,030 |

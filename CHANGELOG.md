# v2.0.0
- Updated Kotlin to 2.0.10, Compose to 1.6.10 and other dependencies
- Moved StartedOptions from FeatureParams to KeemunComponentConnector
- Redesigned saving the state of the store. Added 3 implementations: 
  - stateless (default), 
  - save Store.state (if state serializable), 
  - save the state transformed into another model

# v1.2.0
- Fixed multiple subscription for updates in Jetpack Compose

# v1.1.0
## Core
- Renamed the `init` parameter to `start` from `StoreParams`
- Removed `PreInitEffect` from `StoreParams`
- Renamed `previous` to `savedState` from `Start`

## Decompose
- Renamed `KeemunComponentFeatureConnector` to `KeemunComponentConnector`

## SwiftUI
- Renamed `KeemunNativeFeatureConnector` to `KeemunNativeConnector`


# v1.0.1
- Optimization of swiftui connector dependencies

# v1.0.0
First public version

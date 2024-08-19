Pod::Spec.new do |spec|
    spec.name                     = 'FeatureCounter'
    spec.version                  = '1.1.0'
    spec.homepage                 = 'https://github.com/pavelannin/Keemun-Kotlin'
    spec.source                   = { :http=> ''}
    spec.authors                  = ''
    spec.license                  = ''
    spec.summary                  = 'Multiplatform sample feature'
    spec.vendored_frameworks      = 'build/cocoapods/framework/FeatureCounter.framework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target    = '13.0'
                
                
    if !Dir.exist?('build/cocoapods/framework/FeatureCounter.framework') || Dir.empty?('build/cocoapods/framework/FeatureCounter.framework')
        raise "

        Kotlin framework 'FeatureCounter' doesn't exist yet, so a proper Xcode project can't be generated.
        'pod install' should be executed after running ':generateDummyFramework' Gradle task:

            ./gradlew :sample:counter:generateDummyFramework

        Alternatively, proper pod installation is performed during Gradle sync in the IDE (if Podfile location is set)"
    end
                
    spec.xcconfig = {
        'ENABLE_USER_SCRIPT_SANDBOXING' => 'NO',
    }
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':sample:counter',
        'PRODUCT_MODULE_NAME' => 'FeatureCounter',
    }
                
    spec.script_phases = [
        {
            :name => 'Build FeatureCounter',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                if [ "YES" = "$OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED" ]; then
                  echo "Skipping Gradle build task invocation due to OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED environment variable set to \"YES\""
                  exit 0
                fi
                set -ev
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                "$REPO_ROOT/../../gradlew" -p "$REPO_ROOT" $KOTLIN_PROJECT_PATH:syncFramework \
                    -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
                    -Pkotlin.native.cocoapods.archs="$ARCHS" \
                    -Pkotlin.native.cocoapods.configuration="$CONFIGURATION"
            SCRIPT
        }
    ]
                
end
node {
   stage 'Checkout'

   git url: 'https://github.com/FabricMC/fabric-language-scala.git'

   stage 'Build'

   sh "rm -rf build/libs/"
   sh "chmod +x gradlew"
   sh "./gradlew setupFabric build uploadArchives --refresh-dependencies --stacktrace"

   stage "Archive artifacts"

   archive 'build/libs/*'
}
# fabric-language-scala
Fabric language module for [Scala](http://www.scala-lang.org/). Adds support for using a Scala `object` as the main mod class and bundles the Scala libraries for you.

## Usage
Add it as a dependency:

```groovy
dependencies {
	compile "net.fabricmc:fabric-language-scala:0.1.0"
}
```

Set the language adapter for your mod to use by setting the `languageAdapter` property in the `mod.json` file:

```json
{
    "languageAdapter": "net.fabricmc.language.scala.ScalaLanguageAdapter"
}
```

Add a dependency entry to your `mod.json` file:

```json
{
    "requires": {
        "fabric-language-scala": "*"
    }
}
```
# fabric-language-scala
Fabric language module for [Scala](http://www.scala-lang.org/). Adds support for using a Scala `object` as the main mod class and bundles the Scala libraries for you.

## Usage
Add it as a dependency:

```groovy
dependencies {
	modImplementation "net.fabricmc:fabric-language-scala:1.1.0+scala.2.13.6"
}
```

Specify your entrypoint in your `fabric.mod.json` like so:

```json
"entrypoints": {
    "main": [
        {
            "adapter": "scala",
            "value": "package.ClassName"
        }
    ]
}
```

Add a dependency entry to your `fabric.mod.json` file:

```json
{
    "requires": {
        "fabric-language-scala": "*"
    }
}
```

![Logo](https://raw.githubusercontent.com/idealista/property-merger/main/logo.gif)

# Property-merger

Property-merger is a Maven project built to compute differences and merge property files.

## What is a Property File?

[Property files](https://en.wikipedia.org/wiki/.properties) are mainly used in Java related technologies to store configurable parameters of and application and [i18n/l10n](https://en.wikipedia.org/wiki/Internationalization_and_localization) strings (known as _'Property Resource Bundles'_). Each parameter is stored as key/value pairs; one storing the name of the parameter (key) and the other storing the value.

Property files normally uses the _.properties_ filename extension.

## How it's used

With Property-merger mainly you can compute differences between two Java Property files and merge this differences.

### How to generate an executable Jar file from sources

1. Clone this repository

```
$ git clone https://github.com/idealista/property-merger.git
```

2. Run Maven Package to package this Maven project into an executable Jar file

```
$ ./mvnw package
```

This command will produce a jar in the target folder -> ./target/property-merger-1.1.0-jar-with-dependencies.jar

3. Execute the Jar generated in the second step

```
$ java -jar ./target/property-merger-1.1.0-jar-with-dependencies.jar -h
usage: java -jar <property_merger_jar_path> --help | java -jar <property_merger_jar_path> --version | java -jar <property_merger_jar_path> -l <left_file_path> -r <right_file_path>
            -o <DIFF | MERGE [--with-addition] [--with-deletion] [--with-modification] [--with-remove-base-escapes]>
GitHub Page: https://www.github.com/idealista/property-merger
 -h,--help                        print this message
 -l,--leftFilePath <FILE PATH>    left file path
 -o,--operation <DIFF|MERGE>      operation to execute
 -r,--rightFilePath <FILE PATH>   right file path
 -v,--version                     print version
    --with-addition               addition configured for merge (default to false)
    --with-deletion               deletion configured for merge (default to false)
    --with-modification           modification configured for merge (default to false)
    --with-remove-base-escapes    remove escape character from base file (default to false)
```

### Operations

#### Diff Operation

Given two property files, Property-merger generates an output describing which parameters are added, modified or deleted (comparing base file to other file). 

| Result | Description |
| --- | --- |
| Added Parameter | New parameter (new key) appears in the second file |
| Modified Parameter | Same parameter (same key) appears with different values in both files |
| Deleted Parameter | A parameter that appears in the first file doesn't appear in the second one |

**Example**

**Base File (base.properties)**

```
#
# other comment
#
foo.bar=baz {0}
qux.quux=waldo {0}, fred {1}
```

**Other File (other.properties)**

```
#
# example comment
#
qux.quux=corge {0}, grault {1}
norf.plugh=wyzzy {0}
```

Will produce:

```
$ java -jar ./target/property-merger-1.1.0-jar-with-dependencies.jar -l ./samples/base.properties -r ./samples/other.properties -o DIFF
------ [1] Added Properties ------
norf.plugh = wyzzy {0}

------ [1] Modified Properties ------
qux.quux = corge {0}, grault {1}

------ [1] Deleted Properties ------
foo.bar = baz {0}
```

**Note:** The diff operation **ignores** comments and does not take into account the order in which the parameters were defined. 

Using the files below the diff command won't display any change:

**Base File (base.properties)**

```
#
# example comment
#
foo.bar=baz {0}
qux.quux=corge {0}, grault {1}
```

**Other File (other.properties)**

```
#
# other comment
#
qux.quux=corge {0}, grault {1}
foo.bar=baz {0}
```

Will produce:

```
$ java -jar ./target/property-merger-1.1.0-jar-with-dependencies.jar -l ./samples/base.properties -r ./samples/other.properties -o DIFF
------ [0] Added Properties ------

------ [0] Modified Properties ------

------ [0] Deleted Properties ------

```

## Requirements

The library has been tested with _Apache Maven 3.3.9_ (Maven Wrapper is included to provide a fully encapsulated build setup) and _JDK 1.8_. Newer versions of _Apache Maven/JDK_ should work but could also present issues. 

## License

![Apache 2.0 Licence](https://img.shields.io/hexpm/l/plug.svg)

This project is licensed under the [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0) license - see the [LICENSE](LICENSE) file for details.

## Contributing

Please read [CONTRIBUTING.md](.github/CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

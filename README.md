# jardep

A jar dependency scanner.

It makes a static analysis of the jars with compiled classes.

Examples:

    java -jar target/jardep-1.0-SNAPSHOT.jar -j test.jar -e nl.qcg.test.Main -unused -used

Reports used and used classnames in test.jar when called from nl.qcg.test.Main

## Options

    -x prefix       exclude package     ex: -x org.
    -e classname    entrypoint          ex: -e nl.qcg.test.Main
    -j filename     jar filename        ex: -j ../file.jar
    -unused         report unused
    -used           report used
    -notfound       report notfound
    @config.txt     use options file

In the options file, every option needs to be on a separate line. Per example:

    -x
    org.
    -x
    com.


# jardep

A jar dependency scanner.

It makes a static analysis of the jars with compiled classes.

Examples:

    java -jar jardep.jar -j test.jar -e nl.qcg.test.Main -unused -used

Reports used and used classnames in test.jar when called from nl.qcg.test.Main

## Options

    -x exclude package ex: java.
    -e entrypoint ex: nl.qcg.test.Main

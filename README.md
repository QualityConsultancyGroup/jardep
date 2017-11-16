# jardep

A jar dependency scanner.

It makes a static analysis of the jars with compiled classes.

Examples:

    java -jar jardep.jar -j test.jar -e nl.qcg.test.Main -unused -used

Reports used and used classnames in test.jar when called from nl.qcg.test.Main

## Options

    -x prefix       exclude package     ex: -x org.
    -e classname    entrypoint          ex: -e nl.qcg.test.Main
    -j filename     jar filename        ex: -j ../file.jar
    -unused         report unused
    -used           report used
    -problems       report problems

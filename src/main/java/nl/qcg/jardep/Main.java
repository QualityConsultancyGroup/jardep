/*
 *  Main.java
 *
 *  Created on Oct 10, 2017 2:42:35 PM by Simon IJskes
 *
 */

package nl.qcg.jardep;

import de.tototec.cmdoption.CmdOption;
import de.tototec.cmdoption.CmdlineParser;
import de.tototec.cmdoption.CmdlineParserException;
import java.util.ArrayList;

/**
 *
 * @author Simon IJskes
 */
public class Main
{

    @CmdOption(names = {"--help", "-h"}, description = "Show this help", isHelp = true)
    @SuppressWarnings("FieldMayBeFinal")
    private boolean help;

    @CmdOption(names = {"-v"}, description = "run verbose")
    @SuppressWarnings("FieldMayBeFinal")
    private boolean verbose;

    @CmdOption(names = {"-unused"}, description = "report unused")
    @SuppressWarnings("FieldMayBeFinal")
    private boolean reportUnused;

    @CmdOption(names = {"-notfound"}, description = "report not found")
    @SuppressWarnings("FieldMayBeFinal")
    private boolean reportNotfound;

    @CmdOption(names = {"-used"}, description = "report used")
    @SuppressWarnings("FieldMayBeFinal")
    private boolean reportUsed;

    @CmdOption(names = {"-x"}, args = {"pkgname"}, maxCount = -1, description = "prefix of pkgname to exclude ex: a.b.c")
    @SuppressWarnings("FieldMayBeFinal")
    private ArrayList<String> excludes = new ArrayList<>();

    @CmdOption(names = {"-e"}, args = {"classname"}, maxCount = -1, description = "entrypoint ex: a.b.Main")
    @SuppressWarnings("FieldMayBeFinal")
    private ArrayList<String> entrypoints = new ArrayList<>();

    @CmdOption(names = {"-j"}, args = {"jarfile"}, maxCount = -1, description = "filename of jar to scan")
    @SuppressWarnings("FieldMayBeFinal")
    private ArrayList<String> jars = new ArrayList<>();

    /**
     * @param args the command line arguments
     */
    public static void main( String[] args )
        throws Exception
    {
        Main m = new Main();
        CmdlineParser cp = new CmdlineParser( m );

        try {
            cp.parse( args );
        } catch (CmdlineParserException e) {
            System.err.println( "Error: " + e.getMessage() );
            cp.usage( System.err );
            System.exit( 1 );
        }

        if( m.help ) {
            cp.usage();
            System.exit( 0 );
        }

        m.run();
    }

    private void run()
        throws Exception
    {
        final Analyzer an = new Analyzer();

        for( String j : jars ) {
            if( verbose ) {
                System.out.println( "jar: " + j );
            }
            an.addJar( j );
        }
        for( String e : entrypoints ) {
            if( verbose ) {
                System.out.println( "entrypoint: " + e );
            }
            an.addEntryPoint( e );
        }
        for( String e : excludes ) {
            an.addPackageFilter( e );
        }
        if( verbose ) {
            for( String f : an.getFilter() ) {
                System.out.println( "exclude: " + f );
            }
        }

        an.setReportUnused( reportUnused );
        an.setReportUsed( reportUsed );
        an.setReportNotFound( reportNotfound );

        an.run();
    }

}

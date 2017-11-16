/*
 *  Analyzer.java
 *
 *  Created on Oct 5, 2017 9:43:13 PM by Simon IJskes
 *
 */

package nl.qcg.analyzer;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import org.objectweb.asm.ClassReader;

/**
 *
 * @author Simon IJskes
 */
public class Analyzer
{

    static boolean trace;

    private static final Logger LOG = Logger.getLogger( Analyzer.class.getName() );

    private final ArrayList<String> jars = new ArrayList<>();

    private final HashSet<String> avail = new HashSet<>();

    private final HashSet<String> notfound = new HashSet<>();

    private final HashSet<String> used = new HashSet<>();

    private final HashSet<String> alreadyScanned = new HashSet<>();

    private final HashSet<String> queue = new HashSet<>();

    private PrintWriter pw;

    private boolean reportUsed = true;

    private boolean reportUnused = true;

    private boolean repotNotFound = true;

    public Analyzer()
    {
    }

    public void setReportUsed( boolean reportUsed )
    {
        this.reportUsed = reportUsed;
    }

    public void setReportUnused( boolean reportUnused )
    {
        this.reportUnused = reportUnused;
    }

    public void setRepotNotFound( boolean repotNotFound )
    {
        this.repotNotFound = repotNotFound;
    }

    public void addJar( String file )
    {
        jars.add( file );
    }

    public void addEntryPoint( String className )
    {
        queueClass( className );
    }

    public void run()
        throws Exception
    {
        scanjars();

        while( !queue.isEmpty() ) {
            processQueue();
        }

        writeReport();
    }

    private void writeReport()
    {
        pw = new PrintWriter( System.out );

        if( reportUsed ) {
            writeReportUsed();
        }
        if( reportUnused ) {
            writeReportUnused();
        }
        if( repotNotFound ) {
            writeReportNotFound();
        }

        pw.flush();
    }

    protected void writeReportNotFound()
    {
        pw.println( "=== notfound ===" );
        ArrayList<String> sortlist = new ArrayList<>( notfound );
        Collections.sort( sortlist );

        for( String s : sortlist ) {
            pw.println( s );
        }
    }

    protected void writeReportUnused()
    {
        HashSet<String> set = new HashSet<>();
        set.addAll( avail );
        set.removeAll( used );

        pw.println( "=== unused ===" );
        ArrayList<String> sortlist = new ArrayList<>( set );
        Collections.sort( sortlist );

        for( String s : sortlist ) {
            pw.println( s );
        }
    }

    protected void writeReportUsed()
    {
        HashSet<String> set = new HashSet<>();
        set.addAll( used );
        set.retainAll( avail );

        ArrayList<String> sortlist = new ArrayList<>( set );
        Collections.sort( sortlist );

        pw.println( "=== used ===" );
        for( String s : sortlist ) {
            pw.println( s );
        }
    }

    private String filter[] = {
        "java.", "sun.", "javax.", "javafx.", "com.sun.",
        //        "com.google.", "org.apache.", "org.python.",
        //        "org.antlr.", "org.stringtemplate.",
        //        "org.jdom2.",
        //        "org.w3c.",
        "org.xml.", //        "org.mortbay.",
    //        "org.jdesktop.",
    //        "freemarker."
    };

    private boolean scannableClass( String name )
    {
        for( String f : filter ) {
            if( name.startsWith( f ) ) {
                return false;
            }
        }

        return true;
    }

    private void queueClass( String className )
    {
        if( className == null ) {
            return;
        }

//        if( className.startsWith( "[" ) ) {
//            getClass();
//        }
        if( alreadyScanned.contains( className ) ) {
            // shortcut
            return;
        }
        alreadyScanned.add( className );

        queue.add( className );
    }

    private void processQueue()
    {
        HashSet<String> tmp = new HashSet<>( queue );
        queue.clear();
        for( String s : tmp ) {
            scanNow( s );
        }
    }

    private void scanNow( String className )
    {
        if( !scannableClass( className ) ) {
            return;
        }

        if( used.contains( className ) ) {
            return;
        }

        used.add( className );

        if( refs.containsKey( className ) ) {
            final Set<String> get = refs.get( className );
            for( String s : get ) {
//                if( scannableClass( s ) ) {
//                    used.addJar( s );
//                }
                queueClass( s );
            }
        }

        if( !avail.contains( className ) ) {
            notfound.add( className );
            return;
        }

    }

    private void scanjars()
        throws IOException
    {
        for( String jar : jars ) {
            scanJar( jar );
        }
    }

    private final Map<String, Set<String>> refs = new HashMap<>();

    private void scanJar( String jar )
        throws IOException
    {
        JarFile jarFile = new JarFile( jar );
        for( JarEntry je : Collections.list( jarFile.entries() ) ) {
            String n = je.getName();
            if( !n.endsWith( ".class" ) ) {
                continue;
            }

            n = n.substring( 0, n.length() - 6 );
            n = n.replaceAll( "/", "." );
            avail.add( n );

            InputStream is = jarFile.getInputStream( je );
            try {
                ClassReader classReader = new ClassReader( is );
                HashSet<String> rs = new HashSet<String>()
                {
                    @Override
                    public boolean add( String e )
                    {
//                        LOG.info( "addJar: " + e);

//                        if( e.contains( "MsgQueue1" ) ) {
//                            super.getClass();
//                        }
//                        if( e.startsWith( "[" ) ) {
//                            super.getClass();
//                        }
//                        if( e.endsWith( "[]" ) ) {
//                            super.getClass();
//                        }
                        return super.add( e );
                    }
                };
                ClassScanner cs = new ClassScanner( rs );
                classReader.accept( cs, ClassReader.EXPAND_FRAMES );
                cs.postProcess( classReader );
                final HashSet<String> refset = cs.getRefset();
                refs.put( n, refset );

            } finally {
                is.close();
            }

        }
        jarFile.close();
    }
}

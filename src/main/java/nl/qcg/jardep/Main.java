/*
 *  Main.java
 *
 *  Created on Oct 10, 2017 2:42:35 PM by Simon IJskes
 *
 */

package nl.qcg.jardep;

/**
 *
 * @author Simon IJskes
 */
public class Main
{

    /**
     * @param args the command line arguments
     */
    public static void main( String[] args )
        throws Exception
    {
        final Analyzer an = new Analyzer();

        an.addJar( "../aaa.jar" );
        an.addJar( "../bbb.jar" );

        an.addEntryPoint( "nl.qcg.myapp.Main" );

        //an.setReportUnused( false);
        an.setReportUsed( false );
        an.setRepotNotFound( false );

        an.run();
    }

}

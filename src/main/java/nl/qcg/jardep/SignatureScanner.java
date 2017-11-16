/*
 *  SignatureScanner.java
 *
 *  Created on Oct 8, 2017 12:54:18 PM by Simon IJskes
 *
 */

package nl.qcg.jardep;

import java.util.HashSet;
import java.util.logging.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

/**
 *
 * @author Simon IJskes
 */
public class SignatureScanner
    extends SignatureVisitor
{

    private static final Logger LOG = Logger.getLogger( SignatureScanner.class.getName() );

    private final HashSet<String> refset;

    public SignatureScanner( HashSet<String> refset )
    {
        super( Opcodes.ASM6 );
        this.refset = refset;
    }

    void parseOwner( String signature )
    {
        if( !signature.startsWith( "[") ) {
            signature = "L" + signature + ";" ;
        }
        try {
            SignatureReader sr = new SignatureReader( signature );
            sr.accept( this );
        } catch (Exception e) {
            LOG.severe( signature );
            throw new RuntimeException( e );
        }
    }

    void parseMethod( String signature )
    {
        if( signature == null ) {
            return;
        }
//        LOG.info( "" + signature);
        SignatureReader sr = new SignatureReader( signature );
        sr.accept( this );
    }

    @Override
    public void visitClassType( String name )
    {
        String n = name.replace( '/', '.' );
        refset.add( n );
    }

}

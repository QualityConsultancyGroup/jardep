/*
 *  AnnotationScanner.java
 *
 *  Created on Oct 8, 2017 11:13:06 AM by Simon IJskes
 *
 */

package nl.qcg.jardep;

import java.util.HashSet;
import java.util.logging.Logger;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 *
 * @author Simon IJskes
 */
public class AnnotationScanner
    extends AnnotationVisitor
{

    private static final Logger LOG = Logger.getLogger( AnnotationScanner.class.getName() );

    private final HashSet<String> refset;

    AnnotationScanner( HashSet<String> refset )
    {
        super( Opcodes.ASM6 );
        this.refset = refset;
    }

    @Override
    public AnnotationVisitor visitAnnotation( String name, String desc )
    {
        return new AnnotationScanner(refset );
    }

    @Override
    public void visitEnum( String name, String desc, String value )
    {
        super.visitEnum( name, desc, value );
    }

    @Override
    public void visit( String name, Object value )
    {
//        if( Main.trace ) {
//            LOG.log(Level.INFO, "{0} {1}", new Object[]{name, value});
//        }
//        Class<?> cls = value.getClass();
//        if( value instanceof String ) {
////            super.visit( name, value );
//            return;
//        }
        if( value instanceof Type ) {
            Type type = (Type)value;
            String cn = type.getClassName();
//            if( Main.trace ) {
//                LOG.info( cn );
//            }
            refset.add( cn );

            //LOG.log( Level.INFO, "{0} {1}", new Object[]{name, cn, value.getClass()} );

//            super.visit( name, value );
            return;
        }

//        LOG.log( Level.INFO, "{0} {1}", new Object[]{name, value.getClass()} );

//        super.visit( name, value );
    }

    @Override
    public AnnotationVisitor visitArray( String name )
    {
        //LOG.info( name );
        return new AnnotationScanner( refset );
    }

}

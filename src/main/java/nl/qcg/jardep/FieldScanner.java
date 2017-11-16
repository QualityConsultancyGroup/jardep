/*
 *  FieldScanner.java
 * 
 *  Created on Oct 8, 2017 1:26:42 PM by Simon IJskes
 * 
 */

package nl.qcg.jardep;

import java.util.HashSet;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;

/**
 *
 * @author Simon IJskes
 */
public class FieldScanner
    extends FieldVisitor
{

    private final HashSet<String> refset;

    public FieldScanner( HashSet<String> refset )
    {
        super( Opcodes.ASM6 );
        this.refset = refset;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation( int typeRef, TypePath typePath, String desc, boolean visible )
    {
        throw new RuntimeException();
//        return new AnnotationScanner( refset );
//        return super.visitTypeAnnotation( typeRef, typePath, desc, visible );
    }

    @Override
    public AnnotationVisitor visitAnnotation( String desc, boolean visible )
    {
        return new AnnotationScanner( refset );
//        return super.visitAnnotation( desc, visible );
    }

}

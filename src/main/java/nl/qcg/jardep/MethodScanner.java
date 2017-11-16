/*
 *  MethodScanner.java
 *
 *  Created on Oct 8, 2017 12:24:05 PM by Simon IJskes
 *
 */

package nl.qcg.jardep;

import java.util.HashSet;
import java.util.logging.Logger;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

/**
 *
 * @author Simon IJskes
 */
public class MethodScanner
    extends MethodVisitor
{

    private static final Logger LOG = Logger.getLogger( MethodScanner.class.getName() );

    private final HashSet<String> refset;

    public MethodScanner( HashSet<String> refset )
    {
        super( Opcodes.ASM6 );
        this.refset = refset;
    }

    @Override
    public void visitParameter( String name, int access )
    {
        throw new RuntimeException();
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation( int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible )
    {
       return super.visitLocalVariableAnnotation( typeRef, typePath, start, end, index, desc, visible );
    }

    @Override
    public void visitLocalVariable( String name, String desc, String signature, Label start, Label end, int index )
    {
        new SignatureScanner( refset ).parseMethod( desc );
        if( signature != null ) {
            new SignatureScanner( refset ).parseMethod( signature );
        }

        super.visitLocalVariable( name, desc, signature, start, end, index );
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation( int typeRef, TypePath typePath, String desc, boolean visible )
    {
        return super.visitInsnAnnotation( typeRef, typePath, desc, visible );
    }

    @Override
    public void visitInvokeDynamicInsn( String name, String desc, Handle bsm, Object... bsmArgs )
    {
        new SignatureScanner( refset ).parseMethod( desc );

        super.visitInvokeDynamicInsn( name, desc, bsm, bsmArgs );
    }

    @Override
    public void visitMethodInsn( int opcode, String owner, String name, String desc, boolean itf )
    {
        new SignatureScanner( refset ).parseOwner( owner );
        new SignatureScanner( refset ).parseMethod( desc );

        super.visitMethodInsn( opcode, owner, name, desc, itf );
    }

    @Override
    public void visitLdcInsn( Object cst )
    {
//        LOG.info( "" + cst );

        if( cst instanceof Type ) {
            Type type = (Type)cst ;
            String cn = type.getClassName();

            type.getSort();
            refset.add( cn );
        } else {
//            LOG.info( ""+cst.getClass() );
        }

        super.visitLdcInsn( cst );
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault()
    {
        return new AnnotationScanner( refset );
    }

    @Override
    public AnnotationVisitor visitAnnotation( String desc, boolean visible )
    {
        new SignatureScanner( refset ).parseMethod( desc );

        return new AnnotationScanner( refset );
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation( int parameter, String desc, boolean visible )
    {
        new SignatureScanner( refset ).parseMethod( desc );

        return new AnnotationScanner( refset );
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation( int typeRef, TypePath typePath, String desc, boolean visible )
    {
        throw new RuntimeException();
//        return super.visitTypeAnnotation( typeRef, typePath, desc, visible );
    }

//    @Override
//    public void visitEnd()
//    {
//        Main.trace = false ;
//        super.visitEnd();
//    }

}

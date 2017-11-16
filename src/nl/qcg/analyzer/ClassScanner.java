/*
 *  ClassScanner.java
 *
 *  Created on Oct 8, 2017 11:03:30 AM by Simon IJskes
 *
 */

package nl.qcg.analyzer;

import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

/**
 *
 * @author Simon IJskes
 */
public class ClassScanner
    extends ClassVisitor
{

    private static final Logger LOG = Logger.getLogger( ClassScanner.class.getName() );

    private final HashSet<String> refset;

    public ClassScanner()
    {
        super( Opcodes.ASM6 );
        this.refset = new HashSet<>();
    }

    public ClassScanner( HashSet<String> refset )
    {
        super( Opcodes.ASM6 );
        this.refset = refset;
    }

    public HashSet<String> getRefset()
    {
        return refset;
    }

    @Override
    public void visitAttribute( Attribute attr )
    {
        super.visitAttribute( attr );
    }

    @Override
    public void visitInnerClass( String name, String outerName, String innerName, int access )
    {
        new SignatureScanner( refset ).visitClassType( name );

        super.visitInnerClass( name, outerName, innerName, access );
    }

    @Override
    public void visit( int version, int access, String name, String signature, String superName, String[] interfaces )
    {
        new SignatureScanner( refset ).visitClassType( superName );

        for( String intf : interfaces ) {
            new SignatureScanner( refset ).visitClassType( intf );
        }
    }

    @Override
    public AnnotationVisitor visitAnnotation( String desc, boolean visible )
    {
        new SignatureScanner( refset ).parseMethod( desc );

        return new AnnotationScanner( refset );
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation( int typeRef, TypePath typePath, String desc, boolean visible )
    {
        throw new RuntimeException();
        //return super.visitTypeAnnotation( typeRef, typePath, desc, visible );
    }

    @Override
    public MethodVisitor visitMethod( int access, String name, String desc, String signature, String[] exceptions )
    {
        if( signature != null && signature.contains( "LogUtil" ) ) {
//            Main.trace = true ;
            getClass();
        }
        new SignatureScanner( refset ).parseMethod( desc );
        if( signature != null ) {
            new SignatureScanner( refset ).parseMethod( signature );
        }

        return new MethodScanner( refset );
    }

    @Override
    public FieldVisitor visitField( int access, String name, String desc, String signature, Object value )
    {
        new SignatureScanner( refset ).parseMethod( desc );
        if( signature != null ) {
            new SignatureScanner( refset ).parseMethod( signature );
        }

        return new FieldScanner( refset );
    }

    void postProcess( ClassReader cr )
    {
        int cnt = cr.getItemCount();
        int len = cr.getMaxStringLength() + 2; // +2 for safety.
        char buf[] = new char[len];

  //      LOG.info( "" + cr.getClassName() );

        for( int i = 0; i < (cnt-1); i++ ) {
            int cidx = i + 1;
            int r = cr.getItem( cidx ) - 1;
            if( r < 0 ) {
                continue ;
            }
            int tag = cr.readByte( r ) & 0xFF;
            LOG.log( Level.FINE, "#{0} pos:{1} tag:{2}", new Object[]{cidx, r, tag} );

            if( tag == 7 ) {

                Object cnst = cr.readConst( cidx, buf );
//                LOG.log(Level.INFO, "#{0} {1} {2}", new Object[]{cidx, cnst, cnst.getClass()});
                if( cnst instanceof Type ) {
                    Type type = (Type)cnst ;
                    if( type.getSort() == Type.ARRAY ) {
                        type = type.getElementType();
                    }
                    if( type.getSort() == Type.OBJECT ) {
                        String cn = type.getClassName();
                        refset.add( cn );
                    } else {
                        //LOG.info(  "type: " + type.getSort() );
                    }
                } else {
                    throw new RuntimeException();
                }
            }
        }
    }

}

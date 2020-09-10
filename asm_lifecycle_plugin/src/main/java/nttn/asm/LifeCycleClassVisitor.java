package nttn.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Description:
 * Version: 1.0.0.0
 * Update：
 * Created by Apple.
 */
public class LifeCycleClassVisitor extends ClassVisitor {
    private String className;
    private String superName;

    public LifeCycleClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
        this.superName = superName;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        //System.out.println("ClassVisitor visitMethod----> " + name + ", superName is " + superName);

        MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
        if ("android/support/v7/app/AppCompatActivity".equals(superName)) {
            if ("onCreate".equals(name)) {
                //处理 onCreate() 方法
                return new LifeCycleMethodVisitor(mv, className, name);
            }
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}

package nttn.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Description:
 * Version: 1.0
 * Update：
 * Created by Apple.
 */
public class LifeCycleMethodVisitor extends MethodVisitor {
    private String className;
    private String methodName;


    public static final String owner = "android/util/Log";
    public static final String name = "i";
    public static final String descriptor = "(Ljava/lang/String/;Ljava/lang/String;)I";

    public LifeCycleMethodVisitor(MethodVisitor methodVisitor, String className, String methodName) {
        super(Opcodes.ASM5, methodVisitor);
        this.className = className;
        this.methodName = methodName;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        //System.out.println("========== MethodVisitor visit ==========");

        //方法执行前插入代码
        mv.visitLdcInsn("TAG");
        mv.visitLdcInsn(className + " ----> " + methodName);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, owner, name, descriptor, false);
        mv.visitInsn(Opcodes.POP);

    }
}

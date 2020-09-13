package nttn.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import groovy.io.FileType
import nttn.asm.LifeCycleClassVisitor
import org.apache.commons.io.FileUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

public class LifeTransform extends Transform {
    /**
     * Transform 可看作 Gradle 在编译项目时的一个 task，在 .class 文件转换成 .dex 的流程中会执行这些 task；
     * 主要作用是检索项目编译过程中的所有文件；
     *
     * 通过 Transform 实现的常用功能如：
     * 混淆 （Proguard）、分包（multi-dex）、jar包合并（jarMerge）等。
     */


    /**
     * 设置自定义的 Transform 对应的 Task 名称；
     * Gradle 在编译时会将这个名称显示在控制台上。
     *
     * 如：Task:app:transformClassesWithXXXForDebug
     *
     * @return 自定义的 Transform 对应的 Task 名称；
     */
    @Override
    String getName() {
        return "LifeTransform"
    }

    /**
     * 项目中存在各种格式的文件，该方法可以设置自定义 Transform 接收的文件类型
     *
     * ContentType 有 2 种取值: {@link QualifiedContent.DefaultContentType}
     *
     *  CLASS(0x01),
     *  RESOURCES(0x02)
     *
     * @return 类型集合 {@link TransformManager}
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * 自定义 Transform 检索的范围
     *
     * Scope 有 7 种取值: {@link QualifiedContent.Scope}
     *
     *  PROJECT(0x01),                //项目内容
     *  SUB_PROJECTS(0x04),           //子项目
     *  EXTERNAL_LIBRARIES(0x10),     //外部库
     *  TESTED_CODE(0x20),            //当前变量（包括依赖项）测试的代码
     *  PROVIDED_ONLY(0x40),          //本地或远程依赖项
     *
     *  @Deprecated
     *  PROJECT_LOCAL_DEPS(0x02),     //项目本地依赖
     *  @Deprecated
     *  SUB_PROJECTS_LOCAL_DEPS(0x08);//子项目的本地依赖
     *
     * @return 范围集合 {@link TransformManager}
     */
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.PROJECT_ONLY
    }

    /**
     * 表示当前 Transform 是否支持增量编译
     *
     * @return
     */
    @Override
    boolean isIncremental() {
        return false
    }

    /**
     * 获取两个数据的流向
     *
     * inputs：输入流（两种格式：jar包格式、directory目录格式）
     * outputProvider：输出目录，将修改的文件输出到该目录，这一步必须
     *
     * @param transformInvocation
     * @throws TransformException
     * @throws InterruptedException
     * @throws IOException
     */
    @Override
    void transform(TransformInvocation transformInvocation)
            throws TransformException, InterruptedException, IOException {
        //获取所有的文件
        Collection<TransformInput> transformInputs = transformInvocation.inputs

        /*TransformOutputProvider outputProvider = transformInvocation.outputProvider
        if (outputProvider != null) {
            outputProvider.deleteAll()
        }*/

        //遍历directoryInputs(文件夹中的class文件)
        transformInputs.each { TransformInput transformInput ->
            // 遍历directoryInputs(文件夹中的class文件) directoryInputs代表着以源码方式参与项目编译的所有目录结构及其目录下的源码文件
            // 比如我们手写的类以及R.class、BuildConfig.class以及MainActivity.class等
            transformInput.directoryInputs.each { DirectoryInput directoryInput ->
                File dir = directoryInput.file
                if (dir) {
                    dir.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) { File file ->
                        System.out.println("find class---> " + file.name)

                       /* //对class文件进行读取与解析
                        ClassReader classReader = new ClassReader(file.bytes)
                        //对class文件的写入
                        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                        //访问class文件相应的内容，解析到某一个结构就会通知到ClassVisitor的相应方法
                        ClassVisitor classVisitor = new LifeCycleClassVisitor(classWriter)
                        //依次调用 ClassVisitor 的方法
                        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
                        //toByteArray方法会将最终修改的字节码以byte数组形式返回
                        byte[] bytes = classWriter.toByteArray()

                        //通过文件流写入方法覆盖原先的内容，实现class文件的改写
                        FileOutputStream outputStream = new FileOutputStream(file.path)
                        outputStream.write(bytes)
                        outputStream.close()*/

                    }
                }
               /* //处理完输入文件后把输出传给下一个文件
                def dest = outputProvider.getContentLocation(
                        directoryInput.name,
                        directoryInput.contentTypes,
                        directoryInput.scopes,
                        Format.DIRECTORY
                )
                FileUtils.copyDirectory(directoryInput.file, dest)*/
            }
        }
    }
}
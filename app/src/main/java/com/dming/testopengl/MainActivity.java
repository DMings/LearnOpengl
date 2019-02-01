package com.dming.testopengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DMUI";
    private GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLSurfaceView = new GLSurfaceView(this);
        // 请求一个OpenGL ES 2.0兼容的上下文
        mGLSurfaceView.setEGLContextClientVersion(2);
        // 设置渲染器(后面会着重讲这个渲染器的类)
        mGLSurfaceView.setRenderer(new MyRenderer());
        // 设置渲染模式为连续模式(会以60fps的速度刷新)
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        setContentView(mGLSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    public class MyRenderer implements GLSurfaceView.Renderer {
        private int program;
        private int vPosition;
        private int uColor;
        /**
         * Float类型占4Byte
         */
        private static final int BYTES_PER_FLOAT = 4;

        /**
         * 获取图形的顶点
         * 特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
         * 转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
         *
         * @return 顶点Buffer
         */
        private FloatBuffer getVertices() {
            float vertices[] = {
                    -0.5f,   0.5f,
                    -0.5f, -0.5f,
                    0.5f,  -0.5f,
                    0.5f,  0.5f,
            };

            // 创建顶点坐标数据缓冲
            // vertices.length*4是因为一个float占四个字节
            ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * BYTES_PER_FLOAT);
            vbb.order(ByteOrder.nativeOrder());             //设置字节顺序
            FloatBuffer vertexBuf = vbb.asFloatBuffer();    //转换为Float型缓冲
            vertexBuf.put(vertices);                        //向缓冲区中放入顶点坐标数据
            vertexBuf.position(0);                          //设置缓冲区起始位置

            return vertexBuf;
        }

        private int uMatrixLocation;
        /**
         * 矩阵数组
         */
        private final float[] mProjectionMatrix = new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1,
        };

        /**
         * 当GLSurfaceView中的Surface被创建的时候(界面显示)回调此方法，一般在这里做一些初始化
         * @param gl10 1.0版本的OpenGL对象，这里用于兼容老版本，用处不大
         * @param eglConfig egl的配置信息(GLSurfaceView会自动创建egl，这里可以先忽略)
         */
        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            // 初始化着色器
            // 基于顶点着色器与片元着色器创建程序
            program = ShaderHelper.makeProgram(verticesShader, fragmentShader);
            // 获取着色器中的属性引用id(传入的字符串就是我们着色器脚本中的属性名)
            vPosition = GLES20.glGetAttribLocation(program, "vPosition");
            uMatrixLocation = GLES20.glGetUniformLocation(program,"u_Matrix");
            uColor = GLES20.glGetUniformLocation(program, "uColor");

            // 设置clear color颜色RGBA(这里仅仅是设置清屏时GLES20.glClear()用的颜色值而不是执行清屏)
            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        }

        /**
         * 当GLSurfaceView中的Surface被改变的时候回调此方法(一般是大小变化)
         * @param gl10 同onSurfaceCreated()
         * @param width Surface的宽度
         * @param height Surface的高度
         */
        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            // 边长比(>=1)，非宽高比
            float aspectRatio = width > height ?
                    (float) width / (float) height :
                    (float) height / (float) width;

            // 1. 矩阵数组
            // 2. 结果矩阵起始的偏移量
            // 3. left：x的最小值
            // 4. right：x的最大值
            // 5. bottom：y的最小值
            // 6. top：y的最大值
            // 7. near：z的最小值
            // 8. far：z的最大值
            if (width > height) {
                // 横屏
                Matrix.orthoM(mProjectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
            } else {
                // 竖屏or正方形
                Matrix.orthoM(mProjectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
            }
            // 更新u_Matrix的值，即更新矩阵数组
            GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, mProjectionMatrix, 0);
            // 设置绘图的窗口(可以理解成在画布上划出一块区域来画图)
            GLES20.glViewport(0,0,width,height);
        }

        // 顶点着色器的脚本
        private static final String verticesShader = ""
                // mat4：4×4的矩阵
                + "uniform mat4 u_Matrix;\n"
                + "attribute vec2 vPosition;            \n" // 顶点位置属性vPosition
                + "void main(){                         \n"
                + "   gl_Position = u_Matrix * vec4(vPosition,0,1);\n" // 确定顶点位置
//                +"    gl_PointSize = 40.0;\n"
                + "}";

        // 片元着色器的脚本
        private static final String fragmentShader
                = "precision mediump float;         \n" // 声明float类型的精度为中等(精度越高越耗资源)
                + "uniform vec4 uColor;             \n" // uniform的属性uColor
                + "void main(){                     \n"
                + "   gl_FragColor = uColor;        \n" // 给此片元的填充色
                + "}";

        /**
         * 当Surface需要绘制的时候回调此方法
         * 根据GLSurfaceView.setRenderMode()设置的渲染模式不同回调的策略也不同：
         * GLSurfaceView.RENDERMODE_CONTINUOUSLY : 固定一秒回调60次(60fps)
         * GLSurfaceView.RENDERMODE_WHEN_DIRTY   : 当调用GLSurfaceView.requestRender()之后回调一次
         * @param gl10 同onSurfaceCreated()
         */
        @Override
        public void onDrawFrame(GL10 gl10) {
            // 获取图形的顶点坐标
            FloatBuffer vertices = getVertices();

            // 清屏
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            // 使用某套shader程序
            GLES20.glUseProgram(program);
            // 为画笔指定顶点位置数据(vPosition)
            GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, vertices);
            // 允许顶点位置数据数组
            GLES20.glEnableVertexAttribArray(vPosition);
            // 设置属性uColor(颜色 索引,R,G,B,A)
            GLES20.glUniform4f(uColor, 1.0f, 0.0f, 0.0f, 1.0f);
            // 绘制
//            GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 3);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        }
    }

//    /**
//     * 顶点着色器
//     */
//    private static final String VERTEX_SHADER = "" +
//            // vec4：4个分量的向量：x、y、z、w
//            "attribute vec4 a_Position;\n" +
//            "void main()\n" +
//            "{\n" +
//            // gl_Position：GL中默认定义的输出变量，决定了当前顶点的最终位置
//            "    gl_Position = a_Position;\n" +
//            // gl_PointSize：GL中默认定义的输出变量，决定了当前顶点的大小
//            "    gl_PointSize = 40.0;\n" +
//            "}";
//
//    /**
//     * 片段着色器
//     */
//    private static final String FRAGMENT_SHADER = "" +
//            // 定义所有浮点数据类型的默认精度；有lowp、mediump、highp 三种，但只有部分硬件支持片段着色器使用highp。(顶点着色器默认highp)
//            "precision mediump float;\n" +
//            "uniform mediump vec4 u_Color;\n" +
//            "void main()\n" +
//            "{\n" +
//            // gl_FragColor：GL中默认定义的输出变量，决定了当前片段的最终颜色
//            "    gl_FragColor = u_Color;\n" +
//            "}";


}
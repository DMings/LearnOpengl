##### 入门opengl成功作：
![在这里插入图片描述](https://img-blog.csdnimg.cn/201910150959367.gif)
###### 程序地址：[github](https://github.com/DMings/LearnOpengl)
###### 由左上角到右下角Z排列的着色器效果为：
 1. 图像绘制划分为三块，插值器控制中间图像移动
 2. 由垂直与横向两步组成的快速模糊
 3. NDK线程解码gif后数据转纹理与图像锐化后一同显示
 4. 图像抖动效果（图像颜色RGB分别位移）
 5. 图像纹理在同一屏幕取其中间分三次绘制
 6. 视频数据转OES纹理显示
 7. 图像灵魂出窍效果（图像做动画放大并适当透明度叠加在原图像上）
 8. 图像转亮度后显示的黑白图片
 9. 图像锐化后仅显示锐化轮廓，达到显示图像边缘效果
##### 这个练手的作品，能学习到的技术：
###### 虽然说练手，但程序是由我一手极其注意构建；目前运行良好，性能说得过去。
 - camera/视频数据转OES纹理显示
 - 运用矩阵移动图像，android的插值器控制移动
 - 运用帧缓冲FBO，由垂直与横向两步组成的快速模糊
 - 自定义GL环境共享Context
 - NDK线程解码gif转纹理后绘制显示
 - 不同效果的着色器 
##### OpengGL难学，难以入门？
###### 以下从OpengGL层面上和环境两方面叙述：
 1. OpengGL层面上：个人认为OpengGL即可以简单又可是深是海，为什么这样说，原因很简单，就看你需要学习到什么程度，要实现到什么效果。像我上面的效果前前后后花费了我两个多月的业余时间，对于一般的android应用，在使用上已经的够玩了。由于不涉及3D，光照，雾化等，所以这个入门也是算是力所能及的；但要较为全面学习Opengl，至少花上一年时间才有可能有建树。既然是入门移动端就不必死磕用不上的东西，也就有意地仅学习2D内容，不涉3D及光照、雾化等，也没有深度冲突问题。
2. OpengGL学习环境：初学OpenGL的环境是相当的差的，网上虽然有教程，不过很零散，没有一系列完整的教程，建议买书结合网上进行学习。另外就是学习中遇到错误的问题，网上资料较少，网上搜索经常出不到想要的答案，靠自己觉悟吧。。。
   


 
##### 我的OpenGL学习思路、过程
1. 学习GL概念，先给自己一个大概的轮廓，它是干什么的，用在那里比较好
2. 了解GL 1.0、2.0有什么区别，可以的话也了解一下3.0；
3. 学习GL的坐标系，搞清楚顶点坐标和纹理坐标；学习如何编译着色器程序，搭建GLSurfaceView开发环境，并跑通最简单的绘制三角形程序
4. 学习着色器语言glsl,注意2.0与3.0的区别，以免混用不察觉
5. 学习矩阵，清楚图像矩阵是怎样的；GL2.0中，图像的旋转，移动，变换等离不开矩阵
6. 学习绘制纹理，尝试编写简单的着色器程序，或者从网上参考学习其他人的着色器程序，增强对着色器理解
7. 学习颜色混合(GLES20.GL_BLEND)，在绘制带透明的bitmap时候；不进行特殊设置情况下会达不到预期效果
8. android的MediaPlayer和Camera数据能转纹理显示到GL上；这里需要用到OES纹理，OES是android的扩展纹理，了解OES是非常有需要的。
9. 学习GL的帧缓冲(FBO），FBO个人认为是比较重要的东西，可以在同一次绘制把某步骤绘制结果缓存起来，然后再利用
10. 前面内容学会后，可以进一步学习如何建立EGL，定制自己需要的GL环境。GLSurfaveView是已经包含一个完整EGL，但正所谓越简单的东西，约束就越厉害，使用GLSurfaceView，一般用发情况下就没什么问题，但在很多情况下，我们并不需要面面俱全的EGL环境，就用到了一小部分，或者追求更灵活的用法（如实现一个共享GLContext），杀鸡不用牛刀，这些情况最合适就是自定义一个EGL环境
11. NDK同样支持GL/EGL，在需要高性能的时候，学习在NDK层绘制数据到纹理中，不必再要传递到java层中去绘制

###### 下面再分别详细描述上述11个过程：
1. 学习GL概念：
一开始图像显示都是依赖cpu的，随着图像的复杂度增加，cpu压力越来越大，这时候GPU登场，为了缓解CPU压力把一些运算放在GPU，随着画面越精细GPU也越来越强大。CPU可以编程，同样GPU也可以编程，然后就出现了OpenGL这些软件让开发着可以编写程序运行于GPU。OpenGL它是由C语言编写的，所以它的代码风格都是偏向面向过程的。也像状态机，OpenGL可以记录自己的状态，例如：当是否开启了混合功能、深度等后面的操作都是记录了这个状态的。学习OpenGL编程有利于我们作出一些实时性高的复杂特效等。
2. GL 1.0、2.0有什么区别，选择GL2.0开发：
GL2.0对比1.0最大的区别是多了着色器，也就是可编程管线，令到程序自定义程度更高，灵活性更强。GL2.0目前的android手机基本都支持了，1.0太古老为固定管线，编程方式也差别大，人认为不必学了。android从4.3开始支持GL3.0，了解3.0也是有用处的，但是目前一般用法2.0也是够用的，初学的话，建议从2.0开始。
3. 学习GL的坐标系：
GL中需要重点学习的是顶点坐标和纹理坐标，GL的所有屏幕的宽高都被映射为-1到1，不管物理屏幕的大小。由于手机屏幕是矩形的多，通常情况下，直接绘制图像是会变形的，需要用矩阵变换进行调整，下图的顶点坐标：
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/20191009224116319.png)
 纹理坐标跟顶点坐标有点不一样，他是0-1，而且方向是向上的，在进行贴图的时候需要注意方向：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191013162649643.jpg)
在初学的时候，需要理解清楚坐标的顺序，不同的顺序是会产生不同的方向，也就是会出现旋转、镜像等。这里坐标通常是逆时针开始算的，从那个点开始都行，一般左上角。
清楚坐标后，清楚三角形是GL的基本图形，画矩形其实是由两个三角形组成的。开始搭建GLSurfaceView的环境，这里百度一下，网上一堆绘制一个三角形的Demo程序，跑一跑，感受一下。
4. 学习着色器语言glsl,注意2.0与3.0的区别：
当大概清楚GL程序是如何运行后，下一步就进入到glsl(着色器语言)的学习，glsl长得比较像c语言，但是也不完全是。着色器分为顶点着色器和片元着色器，GL程序运行的时候是先运行顶点着色器确定坐标，图形大小，然后再运行片元着色器在这个图形中绘制图案。在学习着色器的时候，重点是搞清楚变量修饰符，const、attribute、 uniform、varying，初学清楚这四个修饰符就足够了。const是常量的意思，attribute用在顶点着色器中，由程序（java）传递给GL着色器的，一般用来传递的是顶点坐标和纹理坐标。uniform修饰的是统一变量，同样是由程序传给着色器，传的通常是矩阵、一些自定义变量。varying是由顶点着色器传递到片元着色器的，一般是传递的是纹理。GL3.0对这些修饰符简化了不少,修饰变为in、out、inout，layout ，遇到的时候知道是3.0就可以了；别混用，例如顶点着色器是2.0，片元着色器是3.0，链接是会出错的。此外在编写着色器程序的时候需要清楚着色器是不建议跑复杂的程序，由于GPU跟CPU架构不一样，片元着色器是在很多个单元中运行的，越简单，速度才越快。
- 程序运行过程：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191009221414724.png) 
- 顶点着色器示例：
```
precision mediump float;

attribute vec4 inputPosition;
attribute vec4 inputTextureCoordinate;
varying vec2 textureCoordinate;
uniform mat4 inputMatrix;
uniform mat4 uTexMatrix;

void main() {
     gl_Position  = inputMatrix * inputPosition;
     textureCoordinate = (uTexMatrix * inputTextureCoordinate).xy;
}
```
- 片元着色器示例：

```
precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D inputImageTexture;

void main() {
    gl_FragColor = texture2D(inputImageTexture, textureCoordinate);
}
```
5. 学习矩阵：
矩阵对OpenGL来说，我认为是非常重要的，否则图形变换就会变得相当困难。当然矩阵变换的原理可以不必深入理解，适当知道是什么回事就可以了，在java,矩阵变换已经有api支持，用法也简单，下面为图像矩阵使用方式：矩阵乘以坐标点就可以变换图像了，注意前乘和后乘的区别。
 - 平移矩阵 
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191013211556743.png)
 - 缩放矩阵 
     ![在这里插入图片描述](https://img-blog.csdnimg.cn/2019101321160751.png)
 - 旋转矩阵 
下图依次为分别绕X、Y、Z轴的旋转矩阵：  
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191013211659833.png)![在这里插入图片描述](https://img-blog.csdnimg.cn/20191013211711299.png)![在这里插入图片描述](https://img-blog.csdnimg.cn/20191013211646984.png)
 6. 尝试写着色器：
当拥有上面的知识后，下一步是学会把图片转换为纹理，然后进行一些简单的图像处理出，例如把一张图片转为灰度图，单独显示某一个RGB通道。也可以学习如何同时绘制两个纹理；这里说一下，一开始学习绘制两个纹理感觉很懵逼，由于网上的绘制纹理示例都会忽略绑定纹理到着色器中的纹理变量，但这也不能说程序错，只是GL默认使用0号纹理，写不写绑定都一样，但是多个纹理就要写了，不然绑定是会混乱的。
 - 绑定纹理：

```
 ...
 GLES20.glUniform1i(mImageTexture1, 0); // 着色器中的mImageTexture1 统一变量绑定到0号纹理
 GLES20.glUniform1i(mImageTexture2, 1); // 着色器中的mImageTexture2 统一变量绑定到1号纹理
 ...
```

 - bitmap转纹理：
```
public static void loadTexture(Bitmap bitmap) {
        int[] texture = new int[1];
        int target = GLES20.GL_TEXTURE_2D;
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(target, texture[0]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES20.glTexParameterf(target, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(target, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glBindTexture(target, 0);
        bitmap.recycle();
    }
```

7. 学习颜色混合(GLES20.GL_BLEND)：
在绘制带透明的bitmap时候，此时你背景是白色的，可是你会发现图片的透明部分是黑色，尽管你会觉得不合乎你所想的，但事实上这就是透明，绘制是正常的。那么如何实现在白色背景上绘制带透明的bitmap呢？这就需要用到颜色混合:
glBlendFunc(int sfactor,int dfactor)
sfactor 源混合因子
dfactor 目标混合因子
不同因子会有不同效果，这里需要自行去尝试体会一下
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191013220837335.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2E3NTYyMTM5MzI=,size_16,color_FFFFFF,t_70)
- 代码示例
```
GLES20.glEnable(GLES20.GL_BLEND);
GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
....
draw1
draw2
....
GLES20.glDisable(GLES20.GL_BLEND);
```
8. 学习OES拓展纹理：
android的MediaPlayer和Camera数据能转纹理显示到GL上；这里需要用到OES纹理，OES是android的扩展纹理，学会OES是非常有需要的。相对于普通纹理，OES显示出更为强大的一面，它能跨越GL环境传递纹理数据；也就因为这样，像MediaPlayer和Camera这些在自己内部线程解码后的数据可以通过纹理ID直接传递出去。用户拿到这个OES纹理ID就能显示。 此外还是创建Surface必须的东西。
在编写片元着色器的时候需要注意是引入这个扩展纹理：
 - OES着色器
```
#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 textureCoordinate;
uniform samplerExternalOES inputImageOESTexture;

void main() {
    gl_FragColor = texture2D(inputImageOESTexture, textureCoordinate);
}
```
 - java示例
```
mOESVideoTexture = createOESTexture();
mSurfaceTexture = new SurfaceTexture(mOESVideoTexture);
mSurface = new Surface(mSurfaceTexture);
mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(final SurfaceTexture surfaceTexture) {
                    glSurfaceView.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                                mSurfaceTexture.updateTexImage();
                                // 这里 mOESVideoTexture 就有数据，绘制这个就得了；需要注意在GL环境中绘制
                        }
                    });
            }
        });
```
9. 学习GL的帧缓冲(FBO）：
FBO个人认为是比较重要的东西，可以在同一次绘制把某步骤绘制结果缓存起来，然后再利用；就像两步方式的快速高斯模糊，需要先模糊一个方向，如垂直。然后得到模糊后的垂直方向图像后再模糊横向，这种方式少了很多次循环计算，速度快。这里就有个问题，就是如何储存模糊后的垂直方向图像，这里就轮到FBO登场了，创建一个帧缓存区，此缓冲区同时绑定了一个纹理，在这个缓冲区绘制的内容会反映到这个纹理上面，这样首先把模糊后的垂直方向图像绘制到FBO，然后拿这个纹理再跑一次着色器程序把横向模糊，这就实现了这个两步快速高斯模糊。
 - FBO创建并绑定纹理
```
private boolean createFBO(int width, int height) {
        mFBOWidth = width;
        mFBOHeight = height;
        GLES20.glGenFramebuffers(1, mFrameBuffer, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer[0]);
        GLES20.glGenTextures(1, mFrameBufferTexture, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTexture[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mFrameBufferTexture[0], 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            GLES20.glDeleteFramebuffers(1, mFrameBuffer, 0);
            GLES20.glDeleteTextures(1, mFrameBufferTexture, 0);
            DLog.e("create framebuffer failed");
            return false;
        }
        return true;
    } 
```
- 使用示例
```
GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer[0]); // 绑定FBO
drawOES(textureId, texMatrix, 0, 0, mFBOWidth, mFBOHeight); // OES纹理数据绘制到FBO
GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0); // 解除FBO绑定
draw(mFrameBufferTexture[0], verMatrix, x, y, width, height); // 绘制FBO中的数据
```
10. 自定义一个EGL环境：
当把前面的内容学会后，这时候需要拓展一下思维，不能局限于只使用GLSurfaceView；又或者想实现像在解码线程解码数据，GL线程显示数据，由于GL线程才能绘制数据，解码线程要把数据传递给GL线程，此时想提高性能不想数据传来传去。这里解码线程共享资源GL线程Context，在GL线程产生一个纹理，直接给解码线程使用，解码线程绘制数据到纹理，GL线程只管显示，不用管解码线程。其实自定义EGL环境也不是什么难事，就是设置一些环境参数，一般都是用通用就参数就可以了。另外说一下，OpenGL绘制是会把数据绘制到surface，然后由SurfaceFinger合成窗口数据后最终显示在屏幕上的。
- 这里顺带引用官方介绍一下EGLSurface，当额外拓展了
>OpenGL ES 定义了一个渲染图形的 API，但没有定义窗口系统。为了让 GLES 能够适合各种平台，GLES 将与知道如何通过操作系统创建和访问窗口的库结合使用。用于 Android 的库称为 EGL。如果要绘制纹理多边形，应使用 GLES 调用；如果要在屏幕上进行渲染，应使用 EGL 调用。
> 在使用 GLES 进行任何操作之前，需要创建一个 GL 上下文。在 EGL 中，这意味着要创建一个 EGLContext 和一个
> EGLSurface。GLES
> 操作适用于当前上下文，该上下文通过线程局部存储访问，而不是作为参数进行传递。这意味着您必须注意渲染代码在哪个线程上执行，以及该线程上的当前上下文。
> EGLSurface 可以是由 EGL 分配的离屏缓冲区（称为“pbuffer”），或由操作系统分配的窗口。EGL 窗口 Surface
> 通过eglCreateWindowSurface()调用被创建。该调用将“窗口对象”作为参数，在 Android 上，该对象可以是
> SurfaceView、SurfaceTexture、SurfaceHolder 或 Surface，所有这些对象下面都有一个
> BufferQueue。当您进行此调用时，EGL 将创建一个新的 EGLSurface 对象，并将其连接到窗口对象的 BufferQueue
> 的生产方接口。此后，渲染到该 EGLSurface
> 会导致一个缓冲区离开队列、进行渲染，然后排队等待消耗方使用。（术语“窗口”表示预期用途，但请注意，输出内容不一定会显示在显示屏上。） EGL
> 不提供锁定/解锁调用，而是由您发出绘制命令，然后调用eglSwapBuffers()来提交当前帧。方法名称来自传统的前后缓冲区交换，但实际实现可能会有很大的不同。
> 一个 Surface 一次只能与一个 EGLSurface 关联（您只能将一个生产方连接到一个 BufferQueue），但是如果您销毁该
> EGLSurface，它将与该 BufferQueue 断开连接，并允许其他内容连接到该 BufferQueue。
> 通过更改“当前”EGLSurface，指定线程可在多个 EGLSurface 之间进行切换。一个 EGLSurface
> 一次只能在一个线程上处于当前状态。


 - 自定义GL环境示例

```
public class EglHelper {
    private static final String TAG = "EglHelper";
    private EGL10 mEgl;
    private EGLDisplay mEglDisplay;
    private EGLContext mEglContext;
    private EGLSurface mEglSurface;


    public void initEgl(EGLContext eglContext, Surface surface) {
        //1. 得到Egl实例
        mEgl = (EGL10) EGLContext.getEGL();

        //2. 得到默认的显示设备（就是窗口）
        mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (mEglDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed");
        }
        //3. 初始化默认显示设备
        int[] version = new int[2];
        if (!mEgl.eglInitialize(mEglDisplay, version)) {
            throw new RuntimeException("eglInitialize failed");
        }

        //4. 设置显示设备的属性
        int[] attrib_list = new int[]{
                EGL10.EGL_RED_SIZE, mRedSize,
                EGL10.EGL_GREEN_SIZE, mGreenSize,
                EGL10.EGL_BLUE_SIZE, mBlueSize,
                EGL10.EGL_ALPHA_SIZE, mAlphaSize,
                EGL10.EGL_DEPTH_SIZE, mDepthSize,
                EGL10.EGL_STENCIL_SIZE, mStencilSize,
                EGL10.EGL_RENDERABLE_TYPE, mRenderType,//egl版本  2.0
                EGL10.EGL_NONE};


        int[] num_config = new int[1];
        if (!mEgl.eglChooseConfig(mEglDisplay, attrib_list, null, 1,
                num_config)) {
            throw new IllegalArgumentException("eglChooseConfig failed");
        }
        int numConfigs = num_config[0];
        if (numConfigs <= 0) {
            throw new IllegalArgumentException(
                    "No configs match configSpec");
        }

        //5. 从系统中获取对应属性的配置
        EGLConfig[] configs = new EGLConfig[numConfigs];
        if (!mEgl.eglChooseConfig(mEglDisplay, attrib_list, configs, numConfigs,
                num_config)) {
            throw new IllegalArgumentException("eglChooseConfig#2 failed");
        }
        EGLConfig eglConfig = chooseConfig(mEgl, mEglDisplay, configs);
        if (eglConfig == null) {
            eglConfig = configs[0];
        }

        //6. 创建EglContext
        int[] contextAttr = new int[]{
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL10.EGL_NONE
        };
        if (eglContext == null) {
            mEglContext = mEgl.eglCreateContext(mEglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, contextAttr);
        } else {
            mEglContext = mEgl.eglCreateContext(mEglDisplay, eglConfig, eglContext, contextAttr);
        }

        //7. 创建渲染的Surface
        if (surface == null) {
            mEglSurface = EGL10.EGL_NO_SURFACE;
        } else {
            mEglSurface = mEgl.eglCreateWindowSurface(mEglDisplay, eglConfig, surface, null);
        }
    }

    public void glBindThread() {
        //8. 绑定EglContext和Surface到显示设备中
        if (mEglDisplay != null && mEglSurface != null && mEglContext != null) {
            if (!mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {
                throw new RuntimeException("eglMakeCurrent fail");
            }
        }
    }


    //9. 刷新数据，显示渲染场景
    public boolean swapBuffers() {
        if (mEgl != null) {
            return mEgl.eglSwapBuffers(mEglDisplay, mEglSurface);
        } else {
            throw new RuntimeException("egl is null");
        }
    }

    public void destroyEgl() {
        if (mEgl != null) {
            if (mEglSurface != null && mEglSurface != EGL10.EGL_NO_SURFACE) {
                mEgl.eglMakeCurrent(mEglDisplay, EGL10.EGL_NO_SURFACE,
                        EGL10.EGL_NO_SURFACE,
                        EGL10.EGL_NO_CONTEXT);

                mEgl.eglDestroySurface(mEglDisplay, mEglSurface);
                mEglSurface = null;
            }


            if (mEglContext != null) {
                mEgl.eglDestroyContext(mEglDisplay, mEglContext);
                mEglContext = null;
            }


            if (mEglDisplay != null) {
                mEgl.eglTerminate(mEglDisplay);
                mEglDisplay = null;
            }

            mEgl = null;
        }


    }


    public EGLContext getEglContext() {
        return mEglContext;
    }

    private final int mRedSize = 8;
    private final int mGreenSize = 8;
    private final int mBlueSize = 8;
    private final int mAlphaSize = 8;
    private final int mDepthSize = 8;
    private final int mStencilSize = 8;
    private final int mRenderType = 4;

    private EGLConfig chooseConfig(EGL10 egl, EGLDisplay display,
                                   EGLConfig[] configs) {
        for (EGLConfig config : configs) {
            int d = findConfigAttrib(egl, display, config,
                    EGL10.EGL_DEPTH_SIZE, 0);
            int s = findConfigAttrib(egl, display, config,
                    EGL10.EGL_STENCIL_SIZE, 0);
            if ((d >= mDepthSize) && (s >= mStencilSize)) {
                int r = findConfigAttrib(egl, display, config,
                        EGL10.EGL_RED_SIZE, 0);
                int g = findConfigAttrib(egl, display, config,
                        EGL10.EGL_GREEN_SIZE, 0);
                int b = findConfigAttrib(egl, display, config,
                        EGL10.EGL_BLUE_SIZE, 0);
                int a = findConfigAttrib(egl, display, config,
                        EGL10.EGL_ALPHA_SIZE, 0);
                if ((r == mRedSize) && (g == mGreenSize)
                        && (b == mBlueSize) && (a == mAlphaSize)) {
                    return config;
                }
            }
        }
        return null;
    }

    private int findConfigAttrib(EGL10 egl, EGLDisplay display,
                                 EGLConfig config, int attribute, int defaultValue) {
        int[] value = new int[1];
        if (egl.eglGetConfigAttrib(display, config, attribute, value)) {
            return value[0];
        }
        return defaultValue;
    }
}
```
11. NDK同样支持GL/EGL，在需要高性能的时候，学习在NDK层绘制数据到纹理中，不必再要传递到java层中去绘制;OpenGL这个技术不只是服务于android，它是跨平台的，代码规范好；把代码迁移到NDK中函数名字基本一样，代码逻辑都一样，迁移起来也容易。NDK这里我觉得有点奇怪的是居然没有矩阵Matrix相关的工具类，我也不知道为什么。NDK中开发OpenGL注意就是在cmake加入相关的库如GLESv2，需要什么版本的库就加入什么就是，难度不大。
- 像这里把gif数据绘制到纹理
```
glActiveTexture(GL_TEXTURE0);
glBindTexture(GL_TEXTURE_2D, texture);
glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, gif_width, gif_height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
glBindTexture(GL_TEXTURE_2D, 0);
```
###### 程序地址：[github](https://github.com/DMings/LearnOpengl)

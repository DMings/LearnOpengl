//
// Created by Administrator on 2019/9/11.
//

#ifndef TESTPLAYER_GLUTILS_H
#define TESTPLAYER_GLUTILS_H

#include <GLES2/gl2.h>
#include "log.h"
#include <malloc.h>

class GLUtils {
public:
    static GLuint createShader(GLenum shaderType, const char *src);
    static GLuint createProgram(const char *vtxSrc, const char *fragSrc);
    static void checkErr();
    static void checkErr(const char *tagName);
private:
};


#endif //TESTPLAYER_GLUTILS_H

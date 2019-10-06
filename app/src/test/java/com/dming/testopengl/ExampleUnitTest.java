package com.dming.testopengl;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() {
//        assertEquals(4, 2 + 2);
        float[] tempTex = {
                0, 0.667f,
                0, 0.334f,
                1, 0.334f,
                1, 0.667f,
        };
        FloatBuffer fBuffer = ByteBuffer.allocateDirect(tempTex.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(tempTex);
        fBuffer.position(0);
        //
        System.out.println("fBuffer.capacity()>" + fBuffer.capacity());
        for (int i = 0; i < fBuffer.capacity(); i++) {
            System.out.println("fBuffer-" + i + " > " + fBuffer.get(i));
        }
        tempTex[3] = 88;
        tempTex[6] = 44;
        tempTex[7] = 99;
//        fBuffer.position(0);
        fBuffer.put(tempTex);
        for (int i = 0; i < fBuffer.capacity(); i++) {
            System.out.println("=-" + i + " > " + fBuffer.get(i));
        }
    }
}
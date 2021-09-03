#include "jpawFP128.h"

// rounding modes
#define ROUND_UP           0
#define ROUND_DOWN         1
#define ROUND_CEILING      2
#define ROUND_FLOOR        3
#define ROUND_HALF_UP      4
#define ROUND_HALF_DOWN    5
#define ROUND_HALF_EVEN    6
#define ROUND_UNNECESSARY  7

/*
 * Class:     de_jpaw_jni_Multiply128
 * Method:    mult128
 * Signature: (JJJI)J
 *
 * multiplies two 64 bit integers, and scales the result.
 */
JNIEXPORT jlong JNICALL Java_de_jpaw_fixedpoint_FixedPointNative_multdiv128(JNIEnv *env, jclass cls, jlong a, jlong b, jlong c, jint rounding) {
    long long result = (long) a * (long) b;
    jlong tmp = (jlong) (result / c);
    jlong mod = (jlong) (result % c);
    // printf("a=%ld, b=%ld, c=%ld, rounding=%d, tmp=%ld, mod is %ld\n", a, b, c, rounding, (long)tmp, (long)mod);
    if (mod == 0)
        return tmp;  // no rounding required: same for all modes...

    switch (rounding) {
    case ROUND_UP:              // round towards bigger absolute value
        return tmp + (result >= 0 ? 1 : -1);
    case ROUND_DOWN:            // // round towards smaller absolute value
        return tmp;
    case ROUND_CEILING:         // round towards bigger numerical value
        return tmp + (result >= 0);
    case ROUND_FLOOR:           // round towards smaller numerical value
        return tmp - (result < 0);
    case ROUND_HALF_UP:
        if (result >= 0) {
            return tmp + (mod >= (c >> 1));
        } else {
            return tmp - (mod <= -(c >> 1));
        }
    case ROUND_HALF_DOWN:
        if (result >= 0) {
            return tmp + (mod > (c >> 1));
        } else {
            return tmp - (mod < -(c >> 1));
        }
    case ROUND_HALF_EVEN:
        if (result >= 0) {
            if (mod > (c >> 1)) {
                return tmp + 1;
            } else if (mod < (c >> 1)) {
                return tmp;
            } else {
                // in this case, the rounding also depends on the last digit of the result. In case of equidistant numbers, it is rounded towards the nearest even number.
                return tmp + (tmp & 1);
            }
        } else {
            if (mod < -(c >> 1)) {
                return tmp - 1;
            } else if (mod > -(c >> 1)) {
                return tmp;
            } else {
                // in this case, the rounding also depends on the last digit of the result. In case of equidistant numbers, it is rounded towards the nearest even number.
                return tmp - (tmp & 1);
            }
        }
    case ROUND_UNNECESSARY:
    {
        jclass exceptionCls = (*env)->FindClass(env, "java/lang/ArithmeticException");
        (*env)->ThrowNew(env, exceptionCls, "Rounding required but forbidden by roundingMode parameter");
        return 0;
    }
    default: // any other: use fastest. here: equals to ROUND_DOWN, which is the native rounding in C
        return tmp;
    }
}

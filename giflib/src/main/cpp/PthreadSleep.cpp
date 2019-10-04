//
// Created by Administrator on 2019/9/29.
//

#include "PthreadSleep.h"

PthreadSleep::PthreadSleep() : is_interrupt(false) {
    pthread_mutex_init(&sleep_mutex, NULL);
    pthread_cond_init(&sleep_cond, NULL);
}

PthreadSleep::~PthreadSleep() {
    pthread_mutex_destroy(&sleep_mutex);
    pthread_cond_destroy(&sleep_cond);
}


void PthreadSleep::msleep(unsigned int ms) {
    struct timespec deadline;
    struct timeval now;

    gettimeofday(&now, NULL);
    time_t seconds = (time_t) (ms / 1000);
    long nanoseconds = (long) ((ms - seconds * 1000) * 1000000);

    deadline.tv_sec = now.tv_sec + seconds;
    deadline.tv_nsec = now.tv_usec * 1000 + nanoseconds;

    if (deadline.tv_nsec >= 1000000000L) {
        deadline.tv_nsec -= 1000000000L;
        deadline.tv_sec++;
    }
    pthread_mutex_lock(&sleep_mutex);
    if (!is_interrupt) {
        pthread_cond_timedwait(&sleep_cond, &sleep_mutex, &deadline);
    }
    pthread_mutex_unlock(&sleep_mutex);
}

void PthreadSleep::reset() {
    pthread_mutex_lock(&sleep_mutex);
    is_interrupt = false;
    pthread_mutex_unlock(&sleep_mutex);
}

void PthreadSleep::interrupt() {
    pthread_mutex_lock(&sleep_mutex);
    is_interrupt = true;
    pthread_cond_signal(&sleep_cond);
    pthread_mutex_unlock(&sleep_mutex);
}
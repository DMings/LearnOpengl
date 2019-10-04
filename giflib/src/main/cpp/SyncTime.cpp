//
// Created by Administrator on 2019/9/29.
//

#include "SyncTime.h"

void SyncTime::set_clock() {
    clock_gettime(CLOCK_MONOTONIC, &current_ts);
    last_ts.tv_sec = current_ts.tv_sec;
    last_ts.tv_nsec = current_ts.tv_nsec;
}

unsigned int SyncTime::synchronize_time(int m_time) {
    unsigned int c_m_time;
    int diff_time;
    clock_gettime(CLOCK_MONOTONIC, &current_ts);
    c_m_time = (unsigned int) (1000 * (current_ts.tv_sec - last_ts.tv_sec) +
                               (current_ts.tv_nsec - last_ts.tv_nsec) / 1000000);
    diff_time = m_time - c_m_time;
//    LOGI("synchronize_time c_m_time: %d  m_time: %d diff_time:%d", c_m_time, m_time, diff_time);
//    return m_time;
    if (diff_time > 0) {
        return (unsigned int) diff_time;
    } else {
        return 0;
    }
}
//
// Created by Administrator on 2019/9/29.
//

#ifndef TESTGIF_SYNCTIME_H
#define TESTGIF_SYNCTIME_H

#include <time.h>
#include "log.h"

class SyncTime {

public:

    void set_clock();

    unsigned int synchronize_time(int m_time);

private:
    timespec current_ts;
    timespec last_ts;
};

#endif //TESTGIF_SYNCTIME_H

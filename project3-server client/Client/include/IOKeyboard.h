//
// Created by sagiv on 1/13/20.
//

#ifndef BOOST_ECHO_CLIENT_IOKEYBOARD_H
#define BOOST_ECHO_CLIENT_IOKEYBOARD_H


#include "connectionHandler.h"
#include <queue>

class ConnectionHandler;

class IOKeyboard {

private:
    ConnectionHandler &connectionHandler;

public:
    IOKeyboard(ConnectionHandler &connectionHandler);
    void run();

};


#endif //BOOST_ECHO_CLIENT_IOKEYBOARD_H

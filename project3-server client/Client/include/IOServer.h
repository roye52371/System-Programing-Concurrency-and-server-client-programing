//
// Created by sagiv on 1/14/20.
//

#ifndef BOOST_ECHO_CLIENT_IOSERVER_H
#define BOOST_ECHO_CLIENT_IOSERVER_H


#include "connectionHandler.h"

class ConnectionHandler;

class IOServer {

private:
    ConnectionHandler &connectionHandler;
    void printFrame(string frame);

public:
    IOServer(ConnectionHandler &connectionHandler);
    void run();

};


#endif //BOOST_ECHO_CLIENT_IOSERVER_H
